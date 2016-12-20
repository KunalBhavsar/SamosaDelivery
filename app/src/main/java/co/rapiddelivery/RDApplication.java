package co.rapiddelivery;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

import java.util.ArrayList;

import co.rapiddelivery.models.DeliveryModel;
import co.rapiddelivery.models.DeliverySetModel;
import co.rapiddelivery.models.PickupSetModel;
import co.rapiddelivery.network.LoginResponse;

/**
 * Created by Kunal on 15/12/16.
 */

public class RDApplication extends Application {

    private PickupSetModel pickupSetModel;
    private DeliverySetModel deliverySetModel;
    private static LoginResponse appOwnerData;

    @Override
    public void onCreate() {
        super.onCreate();

        // Here you start using the ActiveAndroid library.
        ActiveAndroid.initialize(this);

        deliverySetModel = new DeliverySetModel();
        for (int i = 0; i < 12; i++) {
            DeliveryModel deliveryModel = new DeliveryModel();
            switch (i % 3) {
                case 0 :
                    deliveryModel.setName("Kunal Bhavsar");
                    deliveryModel.setAddress("11, ABBK, Mahajan Wadi, Opp. Central Railway Workshop, Parel - Mumbai");
                    deliveryModel.setPincode("400012");
                    deliveryModel.setCodAmount((i + 1) * 19);
                    deliveryModel.setLatitude(19.0022 + (i * 0.0001 * 19));
                    deliveryModel.setLongitude(72.8416 + (i * 0.0001 * 19));
                    break;
                case 1 :
                    deliveryModel.setName("Shraddha Pednekar");
                    deliveryModel.setAddress("27, Jagruti Building, Devipada, near National Park, Boriwali - Mumbai");
                    deliveryModel.setPincode("400012");
                    deliveryModel.setCodAmount((i + 1) * 13);
                    deliveryModel.setLatitude(19.0022 + (i * 0.0001 * 13));
                    deliveryModel.setLongitude(72.8416 + (i * 0.0001 * 13));
                    break;
                case 2:
                    deliveryModel.setName("Yojana Rangnekar");
                    deliveryModel.setAddress("3, Shivaji Park, Dangal Road, Virar - Thane");
                    deliveryModel.setPincode("471012");
                    deliveryModel.setCodAmount((i + 1) * 17);
                    deliveryModel.setLatitude(19.0022 + (i * 0.0001 * 17));
                    deliveryModel.setLongitude(72.8416 + (i * 0.0001 * 17));
                    break;
            }
            deliverySetModel.getDeliveryModels().add(deliveryModel);
        }
    }

    public PickupSetModel getPickupSetModel() {
        return pickupSetModel;
    }

    public void setPickupSetModel(PickupSetModel pickupSetModel) {
        this.pickupSetModel = pickupSetModel;
    }

    public DeliverySetModel getDeliverySetModel() {
        return deliverySetModel;
    }

    public void setDeliverySetModel(DeliverySetModel deliverySetModel) {
        this.deliverySetModel = deliverySetModel;
    }

    public static LoginResponse getAppOwnerData() {
        if (appOwnerData == null) {
            appOwnerData = new LoginResponse();
        }
        return appOwnerData;
    }

    public void setAppOwnerData(LoginResponse appOwnerData) {
        this.appOwnerData = appOwnerData;
    }
}
