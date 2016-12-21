package co.rapiddelivery.src;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import co.rapiddelivery.RDApplication;
import co.rapiddelivery.models.DeliveryModel;
import co.rapiddelivery.utils.KeyConstants;
import co.rapiddelivery.views.CustomTextView;

public class DeliveryDetailsActivity extends AppCompatActivity {

    private CustomTextView txtTempContent;
    private DeliveryModel deliveryModel;

    private Activity mActivityContext;
    private Context mAppContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        txtTempContent = (CustomTextView) findViewById(R.id.txt_temp);

        String deliveryNumber = getIntent().getStringExtra(KeyConstants.INTENT_EXTRA_DELIVERY_NUMBER);
        deliveryModel = RDApplication.getDeliveryModelByTrackingNumber(deliveryNumber);

        if (deliveryModel == null) {
            Toast.makeText(mActivityContext, "Error in fetching delivery data..", Toast.LENGTH_SHORT).show();
            finish();
        }

        txtTempContent.setText(deliveryModel.getName() + "\n" + deliveryModel.getAddress() + "\n"  + deliveryModel.getPincode());
    }

}
