package co.rapiddelivery.src;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import co.rapiddelivery.RDApplication;
import co.rapiddelivery.models.DeliveryModel;
import co.rapiddelivery.utils.KeyConstants;
import co.rapiddelivery.views.CustomTextView;
import co.rapiddelivery.views.TouchEventView;

public class DeliveryDetailsActivity extends AppCompatActivity {

    private CustomTextView txtTempContent;
    private TouchEventView viewSignPad;
    private DeliveryModel deliveryModel;

    private Activity mActivityContext;
    private Context mAppContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActivityContext = this;
        mAppContext = getApplicationContext();

        String deliveryNumber = getIntent().getStringExtra(KeyConstants.INTENT_EXTRA_DELIVERY_NUMBER);
        deliveryModel = RDApplication.getDeliveryModelByTrackingNumber(deliveryNumber);

        if (deliveryModel == null) {
            Toast.makeText(mActivityContext, "Error in fetching delivery data..", Toast.LENGTH_SHORT).show();
            finish();
        }

        txtTempContent = (CustomTextView) findViewById(R.id.txt_temp);
        txtTempContent.setText(deliveryModel.getName() + "\n" + deliveryModel.getAddress() + "\n"  + deliveryModel.getPincode());

        viewSignPad = (TouchEventView) findViewById(R.id.view_sign_pad);

        final ImageView imgSign = (ImageView) findViewById(R.id.img_sign_output);

        ((Button)findViewById(R.id.btn_reset_drawing)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewSignPad.resetDrawing();
            }
        });

        ((Button)findViewById(R.id.btn_capture_drawing)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgSign.setImageBitmap(viewSignPad.getBitmapDrawing());
            }
        });
    }

}
