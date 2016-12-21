package co.rapiddelivery;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

import java.util.ArrayList;
import java.util.List;

import co.rapiddelivery.models.DeliveryModel;
import co.rapiddelivery.models.DeliverySetModel;
import co.rapiddelivery.models.PickUpModel;
import co.rapiddelivery.models.PickupSetModel;
import co.rapiddelivery.network.LoginResponse;

/**
 * Created by Kunal on 15/12/16.
 */

public class RDApplication extends Application {

    private static PickupSetModel pickupSetModel;
    private static DeliverySetModel deliverySetModel;
    private static LoginResponse appOwnerData;

    @Override
    public void onCreate() {
        super.onCreate();

        // Here you start using the ActiveAndroid library.
        ActiveAndroid.initialize(this);

        initializeDummyData();
    }

    private void initializeDummyData() {
        deliverySetModel = new DeliverySetModel();
        for (int i = 0; i < 6; i++) {
            DeliveryModel deliveryModel = new DeliveryModel();
            deliveryModel.setTrackingNumber(i + "");
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

        pickupSetModel = new PickupSetModel();
        for (int i = 0; i < 6; i++) {
            PickUpModel pickUpModel = new PickUpModel();
            pickUpModel.setPickupNumber(i + "");
            switch (i % 3) {
                case 0 :
                    pickUpModel.setName("Kunal Bhavsar");
                    pickUpModel.setAddress("11, ABBK, Mahajan Wadi, Opp. Central Railway Workshop, Parel - Mumbai");
                    pickUpModel.setPincode("400012");
                    pickUpModel.setCutOffTime((i + 1));
                    pickUpModel.setLatitude(19.0022 + (i * 0.0001 * 19));
                    pickUpModel.setLongitude(72.8416 - (i * 0.0001 * 19));
                    break;
                case 1 :
                    pickUpModel.setName("Shraddha Pednekar");
                    pickUpModel.setAddress("27, Jagruti Building, Devipada, near National Park, Boriwali - Mumbai");
                    pickUpModel.setPincode("400012");
                    pickUpModel.setCutOffTime((i + 1));
                    pickUpModel.setLatitude(19.0022 + (i * 0.0001 * 13));
                    pickUpModel.setLongitude(72.8416 - (i * 0.0001 * 13));
                    break;
                case 2:
                    pickUpModel.setName("Yojana Rangnekar");
                    pickUpModel.setAddress("3, Shivaji Park, Dangal Road, Virar - Thane");
                    pickUpModel.setPincode("471012");
                    pickUpModel.setCutOffTime((i + 1));
                    pickUpModel.setLatitude(19.0022 + (i * 0.0001 * 17));
                    pickUpModel.setLongitude(72.8416 - (i * 0.0001 * 17));
                    break;
            }
            pickupSetModel.getPickupSetModels().add(pickUpModel);
        }
    }

    public static PickupSetModel getPickupSetModel() {
        if (pickupSetModel == null) {
            pickupSetModel = new PickupSetModel();
        }
        return pickupSetModel;
    }

    public void setPickupSetModel(PickupSetModel pickupSetModel) {
        RDApplication.pickupSetModel = pickupSetModel;
    }

    public static DeliverySetModel getDeliverySetModel() {
        if (deliverySetModel == null) {
            deliverySetModel = new DeliverySetModel();
        }
        return deliverySetModel;
    }

    public void setDeliverySetModel(DeliverySetModel deliverySetModel) {
        RDApplication.deliverySetModel = deliverySetModel;
    }

    public static LoginResponse getAppOwnerData() {
        if (appOwnerData == null) {
            appOwnerData = new LoginResponse();
        }
        return appOwnerData;
    }

    public void setAppOwnerData(LoginResponse appOwnerData) {
        RDApplication.appOwnerData = appOwnerData;
    }

    public static DeliveryModel getDeliveryModelByTrackingNumber(String trackingNumber) {
        if (deliverySetModel == null) {
            return null;
        }
        List<DeliveryModel> deliveryModels = deliverySetModel.getDeliveryModels();
        for (DeliveryModel deliveryModel : deliveryModels) {
            if (deliveryModel.getTrackingNumber().equals(trackingNumber)) {
                return deliveryModel;
            }
        }
        return null;
    }

    public static PickUpModel getPickUpModelByPickupNumber(String pickupNumber) {
        if (pickupSetModel == null) {
            return null;
        }
        List<PickUpModel> pickupModels = pickupSetModel.getPickupSetModels();
        for (PickUpModel pickupModel : pickupModels) {
            if (pickupModel.getPickupNumber().equals(pickupNumber)) {
                return pickupModel;
            }
        }
        return null;
    }
}
