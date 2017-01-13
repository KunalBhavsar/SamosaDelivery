package co.rapiddelivery.src;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import co.rapiddelivery.RDApplication;
import co.rapiddelivery.models.DeliveryModel;
import co.rapiddelivery.utils.KeyConstants;
import co.rapiddelivery.views.CustomButton;
import co.rapiddelivery.views.CustomTextView;
import co.rapiddelivery.views.TouchEventView;

public class DeliveryDetailsActivity extends AppCompatActivity {

    private TouchEventView viewSignPad;
    private DeliveryModel deliveryModel;

    private Activity mActivityContext;
    private Context mAppContext;

    private RelativeLayout relBeforeCallStart;
    private RelativeLayout relAfterCallStart;
    private LinearLayout lnrSignRelateButtons;

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

        String deliveryNumber = getIntent().getStringExtra(KeyConstants.INTENT_EXTRA_DELIVERY_NUMBER);
        String awb = getIntent().getStringExtra(KeyConstants.INTENT_EXTRA_SHIPMENT_AWB);
        deliveryModel = RDApplication.getDeliveryModelByTrackingNumberAndAWB(deliveryNumber, awb);

        if (deliveryModel == null) {
            Toast.makeText(mActivityContext, "Error in fetching delivery data..", Toast.LENGTH_SHORT).show();
            finish();
        }

        relBeforeCallStart = (RelativeLayout) findViewById(R.id.rel_before_call_start_content);
        relAfterCallStart = (RelativeLayout) findViewById(R.id.rel_after_call_start_content);
        lnrSignRelateButtons = (LinearLayout) findViewById(R.id.lnr_buttons_sing);

        CustomTextView txtCustomerName = (CustomTextView) findViewById(R.id.txt_customer_name);
        CustomTextView txtCustomerAddress = (CustomTextView) findViewById(R.id.txt_customer_address);
        CustomTextView txtTrackingNumberAndMode = (CustomTextView) findViewById(R.id.txt_tracking_number_and_mode);

        txtTrackingNumberAndMode.setText(deliveryModel.getAwb() + " (" + deliveryModel.getMode()+ ")");
        txtCustomerName.setText(deliveryModel.getName());
        txtCustomerAddress.setText(deliveryModel.getAddress1() + " " + deliveryModel.getAddress2() + " - "  + deliveryModel.getPincode());

        CustomButton btnStartDelivery = (CustomButton) findViewById(R.id.btn_start_delivery);
        CustomButton btnDelivered = (CustomButton) findViewById(R.id.btn_delivered);
        CustomButton btnFailed = (CustomButton) findViewById(R.id.btn_failed);
        final ImageView imgSign = (ImageView) findViewById(R.id.img_sign_output);

        if (deliveryModel.getStatus().equalsIgnoreCase("dispatched")) {
            relBeforeCallStart.setVisibility(View.VISIBLE);
        }
        else {
            relBeforeCallStart.setVisibility(View.GONE);
        }

        btnStartDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relBeforeCallStart.setVisibility(View.GONE);
                relAfterCallStart.setVisibility(View.VISIBLE);
                viewSignPad.setVisibility(View.VISIBLE);
                lnrSignRelateButtons.setVisibility(View.VISIBLE);
                imgSign.setVisibility(View.GONE);
            }
        });
        btnDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = viewSignPad.getBitmapDrawing();
                if (bitmap == null) {
                    Toast.makeText(mAppContext, "Please take singature of customer", Toast.LENGTH_SHORT).show();
                    return;
                }
                viewSignPad.resetDrawing();
                relBeforeCallStart.setVisibility(View.VISIBLE);
                relAfterCallStart.setVisibility(View.GONE);
            }
        });
        btnFailed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewSignPad.resetDrawing();
                relBeforeCallStart.setVisibility(View.VISIBLE);
                relAfterCallStart.setVisibility(View.GONE);
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

}
