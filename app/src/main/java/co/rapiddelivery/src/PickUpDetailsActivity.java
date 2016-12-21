package co.rapiddelivery.src;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import co.rapiddelivery.RDApplication;
import co.rapiddelivery.models.PickUpModel;
import co.rapiddelivery.utils.KeyConstants;
import co.rapiddelivery.views.CustomTextView;

public class PickUpDetailsActivity extends AppCompatActivity {

    private CustomTextView txtTempContent;
    private PickUpModel pickUpModel;

    private Activity mActivityContext;
    private Context mAppContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_up_details);
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtTempContent = (CustomTextView) findViewById(R.id.txt_temp);

        String pickupNumber = getIntent().getStringExtra(KeyConstants.INTENT_EXTRA_PICKUP_NUMBER);
        pickUpModel = RDApplication.getPickUpModelByPickupNumber(pickupNumber);

        if (pickUpModel == null) {
            Toast.makeText(mActivityContext, "Error in fetching pickup data..", Toast.LENGTH_SHORT).show();
            finish();
        }

        txtTempContent.setText(pickUpModel.getName() + "\n" + pickUpModel.getAddress() + "\n"  + pickUpModel.getPincode());
    }

}
