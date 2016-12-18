package co.rapiddelivery;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

import java.util.ArrayList;

import co.rapiddelivery.models.DeliverySetModel;
import co.rapiddelivery.models.PickupSetModel;

/**
 * Created by Kunal on 15/12/16.
 */

public class RDApplication extends Application {

    private PickupSetModel pickupSetModel;
    private DeliverySetModel deliverySetModel;

    @Override
    public void onCreate() {
        super.onCreate();

        // Here you start using the ActiveAndroid library.
        ActiveAndroid.initialize(this);
    }

    
}
