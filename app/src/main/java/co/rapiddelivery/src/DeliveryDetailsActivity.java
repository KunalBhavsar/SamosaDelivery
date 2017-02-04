package co.rapiddelivery.src;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import co.rapiddelivery.RDApplication;
import co.rapiddelivery.models.DeliveryModel;
import co.rapiddelivery.network.APIClient;
import co.rapiddelivery.network.LoginResponse;
import co.rapiddelivery.receiver.AlarmReceiver;
import co.rapiddelivery.utils.ActivityUtils;
import co.rapiddelivery.utils.KeyConstants;
import co.rapiddelivery.utils.SPrefUtils;
import co.rapiddelivery.views.CustomButton;
import co.rapiddelivery.views.CustomTextView;
import co.rapiddelivery.views.TouchEventView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryDetailsActivity extends AppCompatActivity {

    private static final String TAG = DeliveryDetailsActivity.class.getSimpleName();
    private TouchEventView viewSignPad;
    private DeliveryModel deliveryModel;

    private Activity mActivityContext;
    private Context mAppContext;

    private RelativeLayout relBeforeCallStart;
    private RelativeLayout relAfterCallStart;
    private LinearLayout lnrSignRelateButtons;
    private ImageView imgSign;
    String deliveryNumber;
    String awb;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Delivery Details");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActivityContext = this;
        mAppContext = getApplicationContext();

        progressDialog = new ProgressDialog(mActivityContext);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        deliveryNumber = getIntent().getStringExtra(KeyConstants.INTENT_EXTRA_DELIVERY_NUMBER);
         awb = getIntent().getStringExtra(KeyConstants.INTENT_EXTRA_SHIPMENT_AWB);
        deliveryModel = RDApplication.getDeliveryModelByTrackingNumberAndAWB(deliveryNumber, awb);

        if (deliveryModel == null) {
            Toast.makeText(mActivityContext, "Error in fetching delivery data..", Toast.LENGTH_SHORT).show();
            finish();
        }

        relBeforeCallStart = (RelativeLayout) findViewById(R.id.rel_before_call_start_content);
        relAfterCallStart = (RelativeLayout) findViewById(R.id.rel_after_call_start_content);
        lnrSignRelateButtons = (LinearLayout) findViewById(R.id.lnr_buttons_sing);

        TextView txtCustomer = (TextView) findViewById(R.id.txt_cust);
        CustomTextView txtCustomerName = (CustomTextView) findViewById(R.id.txt_customer_name);
        CustomTextView txtCustomerAddress = (CustomTextView) findViewById(R.id.txt_customer_address);
        CustomTextView txtTrackingNumberAndMode = (CustomTextView) findViewById(R.id.txt_tracking_number_and_mode);


        txtCustomer.setText(deliveryNumber);
        txtTrackingNumberAndMode.setText(deliveryModel.getAwb() + " (" + deliveryModel.getMode()+ ")");
        txtCustomerName.setText(deliveryModel.getName());
        txtCustomerAddress.setText(deliveryModel.getAddress1() + " " + deliveryModel.getAddress2() + " - "  + deliveryModel.getPincode());

        CustomButton btnStartDelivery = (CustomButton) findViewById(R.id.btn_start_delivery);
        CustomButton btnDelivered = (CustomButton) findViewById(R.id.btn_delivered);
        CustomButton btnFailed = (CustomButton) findViewById(R.id.btn_failed);
        imgSign = (ImageView) findViewById(R.id.img_sign_output);

        String strtedDeliveryNo = SPrefUtils.getStringPreference(mAppContext, SPrefUtils.STARTED_DELIVERY_NUMBER);
        if(null != strtedDeliveryNo && strtedDeliveryNo.equals(awb)) {
            relBeforeCallStart.setVisibility(View.GONE);
            relAfterCallStart.setVisibility(View.VISIBLE);
        } else if (deliveryModel.getStatus().equalsIgnoreCase("dispatched")) {
            relBeforeCallStart.setVisibility(View.VISIBLE);
        }
        else {
            relBeforeCallStart.setVisibility(View.GONE);
        }

        btnStartDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkGpsStatus();

            }
        });
        btnDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Bitmap bitmap = viewSignPad.getBitmapDrawing();
            if (bitmap == null) {
                Toast.makeText(mAppContext, "Please take signature of customer", Toast.LENGTH_SHORT).show();
                return;
            }
            sendDeliveryStatusToServer(deliveryModel.getAwb(), "1", "");
            }
        });
        btnFailed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            AlertDialog.Builder b = new AlertDialog.Builder(mActivityContext);
            b.setTitle("Select Remark");
            final String[] types = {"Consignee not Available", "Phone Not Reachable", "Door Locked",
                        "Address Not Found",  "Wrong Address", "Cash Not Ready", "Consignee Wanted Open Delivery",
                        "Payment Mode Mismatch", "Customer Asked for Delayed Delivery on (date)",
                        "Address Not Found",  "Wrong Address", "Cash Not Ready", "Consignee Wanted Open Delivery",
                        "Payment Mode Mismatch", "Customer Asked for Delayed Delivery on (date)"};
            b.setItems(types, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    sendDeliveryStatusToServer(deliveryModel.getAwb(), "0", types[which]);
                }
            });

            b.show();
            }
        });
        viewSignPad = (TouchEventView) findViewById(R.id.view_sign_pad);

        ((Button)findViewById(R.id.btn_reset_drawing)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewSignPad.resetDrawing();
            }
        });

        ((Button)findViewById(R.id.btn_capture_drawing)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewSignPad.setVisibility(View.GONE);
                lnrSignRelateButtons.setVisibility(View.GONE);
                imgSign.setVisibility(View.VISIBLE);
                imgSign.setImageBitmap(viewSignPad.getBitmapDrawing());
            }
        });
    }

    private void sendDeliveryStatusToServer(String waybill, String deliveryStatus, String remark) {
        String loginDetails = SPrefUtils.getStringPreference(this, SPrefUtils.LOGGEDIN_USER_DETAILS);
        LoginResponse loginResponse = new Gson().fromJson(loginDetails, LoginResponse.class);

        showLoader(true);
        APIClient.getClient().updateDeliveryTask(loginResponse.getUserName(), loginResponse.getPassword(), loginResponse.getEmp_id(), waybill, deliveryStatus, remark)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Toast.makeText(mAppContext, "Delivery status updated..", Toast.LENGTH_SHORT).show();
                        showLoader(false);
                        finish();
                        SPrefUtils.setStringPreference(mAppContext, SPrefUtils.STARTED_DELIVERY_NUMBER, null);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG,  "LoginResponse : " + t.getLocalizedMessage(), t);
                        showLoader(false);
                    }
                });
    }

    private void startDeliveryOnServer(String waybill) {
        String loginDetails = SPrefUtils.getStringPreference(this, SPrefUtils.LOGGEDIN_USER_DETAILS);
        LoginResponse loginResponse = new Gson().fromJson(loginDetails, LoginResponse.class);

        showLoader(true);
        APIClient.getClient().startDeliveryTask(loginResponse.getUserName(), loginResponse.getPassword(), loginResponse.getEmp_id(), waybill)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        relBeforeCallStart.setVisibility(View.GONE);
                        relAfterCallStart.setVisibility(View.VISIBLE);
                        viewSignPad.setVisibility(View.VISIBLE);
                        lnrSignRelateButtons.setVisibility(View.VISIBLE);
                        imgSign.setVisibility(View.GONE);
                        showLoader(false);
                        SPrefUtils.setStringPreference(mAppContext, SPrefUtils.STARTED_DELIVERY_NUMBER, awb);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG,  "LoginResponse : " + t.getLocalizedMessage(), t);
                        showLoader(false);
                    }
                });
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
    protected void onStop() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        super.onStop();
    }

    private void checkGpsStatus() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            ActivityUtils.showAlertDialog(this, "Please turn on GPS to provide locations");
        } else {
            AlarmReceiver alarmReceiver = new AlarmReceiver();
            alarmReceiver.cancelAlarm(getApplicationContext());
            alarmReceiver.setDailyUpdateAlarm(getApplicationContext());
            String strtedDeliveryNo = SPrefUtils.getStringPreference(mAppContext, SPrefUtils.STARTED_DELIVERY_NUMBER);
            if(null != strtedDeliveryNo && !strtedDeliveryNo.equals(awb)) {
                Toast.makeText(mAppContext, "Please mark current started delivery " + strtedDeliveryNo + " as delivered or not ", Toast.LENGTH_LONG).show();
            }  else {
                startDeliveryOnServer(deliveryModel.getAwb());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        checkGpsStatus();
    }
}
