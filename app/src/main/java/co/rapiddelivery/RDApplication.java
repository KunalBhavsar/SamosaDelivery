package co.rapiddelivery;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

import java.util.ArrayList;
import java.util.List;

import co.rapiddelivery.models.DeliveryModel;
import co.rapiddelivery.models.PickUpModel;
import co.rapiddelivery.models.PickupSetModel;
import co.rapiddelivery.network.LoginResponse;

/**
 * Created by Kunal on 15/12/16.
 */

public class RDApplication extends Application {

    private static List<DeliveryModel> deliveryModels;
    private static PickupSetModel pickupSetModel;
    private static LoginResponse appOwnerData;

    @Override
    public void onCreate() {
        super.onCreate();

        // Here you start using the ActiveAndroid library.
        ActiveAndroid.initialize(this);

        initializeDummyData();
    }

    private void initializeDummyData() {
        deliveryModels = new ArrayList<>();

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

    public static void setPickupSetModel(PickupSetModel pickupSetModel) {
        RDApplication.pickupSetModel = pickupSetModel;
    }

    public static List<DeliveryModel> getDeliveryModels() {
        if (deliveryModels == null) {
            deliveryModels = new ArrayList<>();
        }
        return deliveryModels;
    }

    public static void setDeliveryModels(List<DeliveryModel> deliveryModels) {
        RDApplication.deliveryModels = deliveryModels;
    }

    public static LoginResponse getAppOwnerData() {
        if (appOwnerData == null) {
            appOwnerData = new LoginResponse();
        }
        return appOwnerData;
    }

    public static void setAppOwnerData(LoginResponse appOwnerData) {
        RDApplication.appOwnerData = appOwnerData;
    }

    public static DeliveryModel getDeliveryModelByTrackingNumberAndAWB(String trackingNumber, String awb) {
        if (deliveryModels == null) {
            return null;
        }
        for (DeliveryModel deliveryModel : deliveryModels) {
            if (!deliveryModel.isHeader() && deliveryModel.getDeliveryNumber().equals(trackingNumber) && deliveryModel.getAwb().equals(awb)) {
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
