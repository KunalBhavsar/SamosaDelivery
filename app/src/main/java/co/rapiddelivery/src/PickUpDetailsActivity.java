package co.rapiddelivery.src;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import co.rapiddelivery.BarcodeReaderInterface;
import co.rapiddelivery.RDApplication;
import co.rapiddelivery.models.PickUpModel;
import co.rapiddelivery.network.APIClient;
import co.rapiddelivery.network.LoginResponse;
import co.rapiddelivery.network.ServerResponseBase;
import co.rapiddelivery.network.SubmitPickupRequestModel;
import co.rapiddelivery.utils.ActivityUtils;
import co.rapiddelivery.utils.BarcodeTrackerFactory;
import co.rapiddelivery.utils.KeyConstants;
import co.rapiddelivery.utils.SPrefUtils;
import co.rapiddelivery.views.CameraSourcePreview;
import co.rapiddelivery.views.CustomButton;
import co.rapiddelivery.views.CustomTextView;
import co.rapiddelivery.views.GraphicOverlay;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PickUpDetailsActivity extends AppCompatActivity implements BarcodeReaderInterface, DatePickerDialog.OnDateSetListener {

    private final static int BARCODE_READER_STATUS_CAMERA_CLOSED = 1;
    private final static int BARCODE_READER_STATUS_CAMERA_OPEN = 2;
    private final static int MY_PERMISSIONS_REQUEST_CALL_PHONE = 101;
    private final static int MY_PERMISSIONS_REQUEST_CAMERA = 102;

    private final static String INSTANCE_STATE_BARCODE_READER_STATUS = "barcode_reader_status";
    private final static String INSTANCE_STATE_SELECTED_BARCODE_RAW_VALUE = "selected_barcode_raw_value";
    private static final String TAG = PickUpDetailsActivity.class.getSimpleName();

    private int currentBarcodeReaderStatus = BARCODE_READER_STATUS_CAMERA_CLOSED;

    private TextView txtID;
    private CustomTextView txtCust;
    private CustomTextView txtPhone;
    private CustomTextView txtAddress;
    private CustomTextView txtExpectedCount;
    private CustomTextView txtBarcodeReading;

    private LinearLayout lnrButtonsBarcode;
    private CustomButton btnCaptureBarcode;
    private CustomButton btnEnterBarcode;
    private CustomButton btnCloseCamera;

    private PickUpModel pickUpModel;

    private Activity mActivityContext;
    private Context mAppContext;

    private CameraSourcePreview mPreview;
    private CameraSource mCameraSource;
    private GraphicOverlay mGraphicOverlay;

    private ArrayList<String> selectedBarcodeRawValue;

    private ProgressDialog progressDialog;
    private LinearLayout lnrButtons;
    private CustomButton btnPickedUp;
    private CustomButton btnFailed;
    private CardView cardBarcodeScanned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_up_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActivityContext = this;
        mAppContext = getApplicationContext();

        progressDialog = new ProgressDialog(mActivityContext);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        selectedBarcodeRawValue = new ArrayList<>();

        String pickupNumber = getIntent().getStringExtra(KeyConstants.INTENT_EXTRA_PICKUP_NUMBER);
        pickUpModel = RDApplication.getPickUpModelByPickupNumber(pickupNumber);

        if (pickUpModel == null) {
            Toast.makeText(mActivityContext, "Error in fetching pickup data..", Toast.LENGTH_SHORT).show();
            finish();
        }

        txtID = (TextView) findViewById(R.id.txt_id);
        txtCust = (CustomTextView) findViewById(R.id.txt_name);
        txtPhone = (CustomTextView) findViewById(R.id.txt_phone);
        txtPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callOnCustomerNumber();
            }
        });
        txtAddress = (CustomTextView) findViewById(R.id.txt_loc);
        txtExpectedCount = (CustomTextView) findViewById(R.id.txt_data);
        lnrButtons = (LinearLayout) findViewById(R.id.lnr_buttons);
        btnPickedUp = (CustomButton) findViewById(R.id.btn_picked_up);
        btnFailed = (CustomButton) findViewById(R.id.btn_failed);

        //txtTempContent.setText(pickUpModel.getName() + "\n" + pickUpModel.getAddress() + "\n"  + pickUpModel.getPincode());
        txtID.setText(pickUpModel.getPickupNumber());
        txtCust.setText(pickUpModel.getName());
        txtPhone.setText(pickUpModel.getPhoneNumber());
        txtAddress.setText(pickUpModel.getAddress() + ", Pincode" + pickUpModel.getPincode());
        txtExpectedCount.setText("Expected count is " + pickUpModel.getExpectedCount());

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.overlay);
        lnrButtonsBarcode = (LinearLayout) findViewById(R.id.lnr_buttons_read_barcode);
        btnCaptureBarcode = (CustomButton) findViewById(R.id.btn_capture_barcode);
        btnEnterBarcode = (CustomButton) findViewById(R.id.btn_enter_barcode);
        btnCloseCamera = (CustomButton) findViewById(R.id.btn_close_camera);
        txtBarcodeReading = (CustomTextView) findViewById(R.id.txt_barcode_reading);
        cardBarcodeScanned = (CardView) findViewById(R.id.card_barcodes_scanned);
        cardBarcodeScanned.setVisibility(View.GONE);

        if (savedInstanceState != null) {
            currentBarcodeReaderStatus = savedInstanceState.getInt(INSTANCE_STATE_BARCODE_READER_STATUS, BARCODE_READER_STATUS_CAMERA_CLOSED);
            selectedBarcodeRawValue = savedInstanceState.getStringArrayList(INSTANCE_STATE_SELECTED_BARCODE_RAW_VALUE);

            if (currentBarcodeReaderStatus == BARCODE_READER_STATUS_CAMERA_OPEN) {
                startCameraSource();
            }

            if (selectedBarcodeRawValue != null && !selectedBarcodeRawValue.isEmpty()) {
                txtBarcodeReading.setText("");
                for (String barcodeValue : selectedBarcodeRawValue) {
                    txtBarcodeReading.append(barcodeValue + "\n");
                }
            }
        }

        btnCloseCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchCameraSourceStatus();
            }
        });

        btnCaptureBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchCameraSourceStatus();
            }
        });

        btnEnterBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEnterBarcodeDialog();
            }
        });

        btnPickedUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedBarcodeRawValue.isEmpty()) {
                    new AlertDialog.Builder(mActivityContext)
                            .setTitle("Are you sure??")
                            .setMessage("No waybill scanned, do you still want to proceed.")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    sendPickupStatusToServer("1", "", null);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                    dialog.dismiss();
                                }
                            })
                            .show();
                    return;
                }
                sendPickupStatusToServer("1", "", null);
            }
        });

        btnFailed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder b = new AlertDialog.Builder(mActivityContext);
                b.setTitle("Select Remark");
                final String[] types = {"Consignee not Available", "Door Locked", "Office Closed", "Asked For Delayed Pickup On (Date)",
                        "Address Not Found",  "Wrong Address", "Cash Not Ready", "Consignee Wanted Open Delivery",
                        "Payment Mode Mismatch", "Phone Not Reachable", "Entry Restricted", "Customer Rejected",
                        "Product Not Ready", "Product Description Mismatch"};

                b.setItems(types, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (types[which].equalsIgnoreCase("Asked For Delayed Pickup On (Date)")) {
                            dialog.dismiss();
                            android.support.v4.app.DialogFragment newFragment = new DatePickerFragment();
                            newFragment.show(getSupportFragmentManager(), "datePicker");
                        }
                        else {
                            dialog.dismiss();
                            sendPickupStatusToServer("0", types[which], null);
                        }
                    }
                });

                b.show();
            }
        });
    }

    private void callOnCustomerNumber() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivityContext,
                    new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
            return;
        }
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + pickUpModel.getPhoneNumber()));
        startActivity(callIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    callOnCustomerNumber();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(mActivityContext, "Permission denied, hence cant call the customer", Toast.LENGTH_SHORT).show();
                }
            }
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    startCameraSource();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(mActivityContext, "Permission denied, hence cant open the camera", Toast.LENGTH_SHORT).show();
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void showEnterBarcodeDialog() {
        LayoutInflater li = LayoutInflater.from(mActivityContext);
        View promptsView = li.inflate(R.layout.user_input_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                mActivityContext);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.edt_waybill_number);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                processBarcode(userInput.getText().toString());
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivityContext,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
            return;
        }
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
            lnrButtonsBarcode.setVisibility(View.GONE);
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
            lnrButtonsBarcode.setVisibility(View.VISIBLE);
            btnCloseCamera.setVisibility(View.GONE);
            mPreview.setVisibility(View.GONE);
        }
    }

    private void showLoader(boolean show) {
        if (progressDialog != null) {
            if (show && !progressDialog.isShowing()) {
                progressDialog.show();
            } else if (!show && progressDialog.isShowing()) {
                progressDialog.hide();
            }
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
            outState.putStringArrayList(INSTANCE_STATE_SELECTED_BARCODE_RAW_VALUE, selectedBarcodeRawValue);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBarcodeRead(final Barcode barcode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pauseCameraSource();
                stopCameraSource();
                processBarcode(barcode.rawValue);
            }
        });
    }

    private void processBarcode(final String barcode) {

        if (barcode == null || barcode.trim().isEmpty()) {
            wrongWaybillHandling();
        }
        else if (selectedBarcodeRawValue.contains(barcode)) {
            Toast.makeText(mAppContext, "This waybill is already present", Toast.LENGTH_SHORT).show();
        }
        else {
            if (ActivityUtils.isNetworkConnected(mAppContext)) {
                showLoader(true);
                LoginResponse loginResponse = RDApplication.getAppOwnerData();

                APIClient.getClient().checkAwbAvailability(loginResponse.getUserName(), loginResponse.getPassword(), loginResponse.getEmp_id(), pickUpModel.getPickupNumber(), barcode)
                        .enqueue(new Callback<ServerResponseBase>() {
                            @Override
                            public void onResponse(Call<ServerResponseBase> call, Response<ServerResponseBase> response) {
                                ServerResponseBase responseModel = response.body();
                                showLoader(false);
                                if (null != responseModel) {
                                    switch (responseModel.getStatusCode()) {
                                        case "200":
                                            if (!selectedBarcodeRawValue.contains(barcode)) {
                                                selectedBarcodeRawValue.add(barcode);
                                                if (selectedBarcodeRawValue.isEmpty()) {
                                                    cardBarcodeScanned.setVisibility(View.GONE);
                                                }
                                                else {
                                                    cardBarcodeScanned.setVisibility(View.VISIBLE);
                                                    txtBarcodeReading.append((selectedBarcodeRawValue.size() > 1 ? ", " : "") + barcode);
                                                    txtExpectedCount.setText("Expected count is " + selectedBarcodeRawValue.size());
                                                }
                                            }
                                            break;
                                        default:
                                            wrongWaybillHandling();
                                            break;
                                    }
                                } else {
                                    wrongWaybillHandling();
                                }
                            }

                            @Override
                            public void onFailure(Call<ServerResponseBase> call, Throwable t) {
                                t.printStackTrace();
                                showLoader(false);
                                Toast.makeText(mAppContext, "Error in reaching server", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(mAppContext, "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void wrongWaybillHandling() {
        Toast.makeText(mAppContext, "Wrong waybill scanned, try again", Toast.LENGTH_SHORT).show();
        MediaPlayer mp = MediaPlayer.create(this, R.raw.erro);
        mp.start();
    }

    private void sendPickupStatusToServer(String deliveryStatus, String remark, String date) {
        String loginDetails = SPrefUtils.getStringPreference(this, SPrefUtils.LOGGEDIN_USER_DETAILS);
        LoginResponse loginResponse = new Gson().fromJson(loginDetails, LoginResponse.class);

        showLoader(true);

        String pickupList = new Gson().toJson(new SubmitPickupRequestModel(selectedBarcodeRawValue));
        APIClient.getClient().submitPickup(loginResponse.getUserName(), loginResponse.getPassword(), loginResponse.getEmp_id(), remark, deliveryStatus, date, pickUpModel.getPickupNumber(),  pickupList)
            .enqueue(new Callback<ServerResponseBase>() {
                @Override
                public void onResponse(Call<ServerResponseBase> call, Response<ServerResponseBase> response) {
                    ServerResponseBase responseModel = response.body();
                    showLoader(false);
                    switch (responseModel.getStatusCode()) {
                        case "200":
                            Toast.makeText(mAppContext, "Pickup status updated..", Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                        default:
                            Toast.makeText(mActivityContext, responseModel.getMessage(), Toast.LENGTH_SHORT).show();
                            break;
                    }
                }

                @Override
                public void onFailure(Call<ServerResponseBase> call, Throwable t) {
                    Log.e(TAG,  "LoginResponse : " + t.getLocalizedMessage(), t);
                    showLoader(false);
                }
            });
    }

    public static class DatePickerFragment extends android.support.v4.app.DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), ((PickUpDetailsActivity)getActivity()), year, month, day);
            DatePicker datePicker = datePickerDialog.getDatePicker();
            datePicker.setMinDate(Calendar.getInstance().getTimeInMillis());
            datePicker.setMaxDate(Calendar.getInstance().getTimeInMillis() + (1000 * 60 * 60 * 72));
            // Create a new instance of DatePickerDialog and return it
            return datePickerDialog;
        }
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = format.format(new Date(year-1900, month, day));
        Log.i(TAG, date);
        sendPickupStatusToServer("0", "Customer Asked for Delayed Pickup on " + date, date);
    }
}
