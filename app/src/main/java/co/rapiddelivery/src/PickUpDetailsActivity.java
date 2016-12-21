package co.rapiddelivery.src;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import co.rapiddelivery.RDApplication;
import co.rapiddelivery.models.PickUpModel;
import co.rapiddelivery.utils.BarcodeTrackerFactory;
import co.rapiddelivery.utils.KeyConstants;
import co.rapiddelivery.views.CameraSourcePreview;
import co.rapiddelivery.views.CustomTextView;
import co.rapiddelivery.views.GraphicOverlay;

public class PickUpDetailsActivity extends AppCompatActivity {

    private CustomTextView txtTempContent;
    private PickUpModel pickUpModel;

    private Activity mActivityContext;
    private Context mAppContext;

    private CameraSourcePreview mPreview;
    private CameraSource mCameraSource;
    private GraphicOverlay mGraphicOverlay;

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

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(mAppContext).build();
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay);
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());

        mCameraSource = new CameraSource.Builder(mAppContext, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .build();
    }

    //starting the preview
    private void startCameraSource() {
        try {
            mPreview.start(mCameraSource, mGraphicOverlay);
        } catch (IOException e) {
            mCameraSource.release();
            mCameraSource = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource(); //start
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop(); //stop
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCameraSource.release(); //release the resources
    }
}
