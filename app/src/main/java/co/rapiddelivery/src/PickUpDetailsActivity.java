package co.rapiddelivery.src;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import co.rapiddelivery.BarcodeReaderInterface;
import co.rapiddelivery.RDApplication;
import co.rapiddelivery.models.PickUpModel;
import co.rapiddelivery.utils.BarcodeTrackerFactory;
import co.rapiddelivery.utils.KeyConstants;
import co.rapiddelivery.views.CameraSourcePreview;
import co.rapiddelivery.views.CustomButton;
import co.rapiddelivery.views.CustomTextView;
import co.rapiddelivery.views.GraphicOverlay;

public class PickUpDetailsActivity extends AppCompatActivity implements BarcodeReaderInterface {

    private final static int BARCODE_READER_STATUS_CAMERA_CLOSED = 1;
    private final static int BARCODE_READER_STATUS_CAMERA_OPEN = 2;

    private final static String INSTANCE_STATE_BARCODE_READER_STATUS = "barcode_reader_status";
    private final static String INSTANCE_STATE_SELECTED_BARCODE_RAW_VALUE = "selected_barcode_raw_value";

    private int currentBarcodeReaderStatus = BARCODE_READER_STATUS_CAMERA_CLOSED;

    private CustomTextView txtTempContent;
    private CustomTextView txtBarcodeReading;

    private CustomButton btnReadBarcode;
    private CustomButton btnCloseCamera;

    private PickUpModel pickUpModel;

    private Activity mActivityContext;
    private Context mAppContext;

    private CameraSourcePreview mPreview;
    private CameraSource mCameraSource;
    private GraphicOverlay mGraphicOverlay;

    private String selectedBarcodeRawValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_up_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActivityContext = this;
        mAppContext = getApplicationContext();

        String pickupNumber = getIntent().getStringExtra(KeyConstants.INTENT_EXTRA_PICKUP_NUMBER);
        pickUpModel = RDApplication.getPickUpModelByPickupNumber(pickupNumber);

        if (pickUpModel == null) {
            Toast.makeText(mActivityContext, "Error in fetching pickup data..", Toast.LENGTH_SHORT).show();
            finish();
        }

        txtTempContent = (CustomTextView) findViewById(R.id.txt_temp);
        txtTempContent.setText(pickUpModel.getName() + "\n" + pickUpModel.getAddress() + "\n"  + pickUpModel.getPincode());

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.overlay);
        btnReadBarcode = (CustomButton) findViewById(R.id.btn_read_barcode);
        btnCloseCamera = (CustomButton) findViewById(R.id.btn_close_camera);
        txtBarcodeReading = (CustomTextView) findViewById(R.id.txt_barcode_reading);

        if (savedInstanceState != null) {
            currentBarcodeReaderStatus = savedInstanceState.getInt(INSTANCE_STATE_BARCODE_READER_STATUS, BARCODE_READER_STATUS_CAMERA_CLOSED);
            selectedBarcodeRawValue = savedInstanceState.getString(INSTANCE_STATE_SELECTED_BARCODE_RAW_VALUE, null);

            if (currentBarcodeReaderStatus == BARCODE_READER_STATUS_CAMERA_OPEN) {
                startCameraSource();
            }

            if (selectedBarcodeRawValue != null) {
                txtBarcodeReading.setText(selectedBarcodeRawValue);
            }
        }

        btnCloseCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchCameraSourceStatus();
            }
        });

        btnReadBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchCameraSourceStatus();
            }
        });
    }

    private void switchCameraSourceStatus() {
        if (currentBarcodeReaderStatus == BARCODE_READER_STATUS_CAMERA_CLOSED) {
            startCameraSource();
        }
        else if (currentBarcodeReaderStatus == BARCODE_READER_STATUS_CAMERA_OPEN) {
            pauseCameraSource();
            stopCameraSource();
        }
    }

    //starting the preview
    private void startCameraSource() {
        if (currentBarcodeReaderStatus == BARCODE_READER_STATUS_CAMERA_CLOSED) {
            BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(mAppContext).build();
            BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay, this);
            barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());

            mCameraSource = new CameraSource.Builder(mAppContext, barcodeDetector)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 960)
                    .setAutoFocusEnabled(true)
                    .build();

            currentBarcodeReaderStatus = BARCODE_READER_STATUS_CAMERA_OPEN;

            txtBarcodeReading.setVisibility(View.GONE);
            btnReadBarcode.setVisibility(View.GONE);
            btnCloseCamera.setVisibility(View.VISIBLE);
            mPreview.setVisibility(View.VISIBLE);

            resumeCameraSource();
        }
    }

    private void pauseCameraSource() {
        if (currentBarcodeReaderStatus == BARCODE_READER_STATUS_CAMERA_OPEN) {
            mPreview.stop(); //stop
        }
    }

    private void resumeCameraSource() {
        if (currentBarcodeReaderStatus == BARCODE_READER_STATUS_CAMERA_OPEN) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    private void stopCameraSource() {
        if (currentBarcodeReaderStatus == BARCODE_READER_STATUS_CAMERA_OPEN) {
            mCameraSource.release();
            currentBarcodeReaderStatus = BARCODE_READER_STATUS_CAMERA_CLOSED;
            txtBarcodeReading.setVisibility(View.VISIBLE);
            btnReadBarcode.setVisibility(View.VISIBLE);
            btnCloseCamera.setVisibility(View.GONE);
            mPreview.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeCameraSource(); //start
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseCameraSource(); //start
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopCameraSource(); //release the resources
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_STATE_BARCODE_READER_STATUS, currentBarcodeReaderStatus);
        if (selectedBarcodeRawValue != null) {
            outState.putString(INSTANCE_STATE_SELECTED_BARCODE_RAW_VALUE, selectedBarcodeRawValue);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBarcodeRead(Barcode barcode) {
        selectedBarcodeRawValue = barcode.rawValue;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pauseCameraSource();
                stopCameraSource();
                txtBarcodeReading.setText(selectedBarcodeRawValue);
            }
        });
    }
}
