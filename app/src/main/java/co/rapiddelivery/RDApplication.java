package co.rapiddelivery;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import co.rapiddelivery.models.DeliveryModel;
import co.rapiddelivery.models.PickUpModel;
import co.rapiddelivery.network.LoginResponse;

/**
 * Created by Kunal on 15/12/16.
 */

public class RDApplication extends Application {

    private static final String TAG = RDApplication.class.getSimpleName();

    private static List<DeliveryModel> deliveryModels;
    private static List<PickUpModel> pickupModels;
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
        pickupModels = new ArrayList<>();
    }

    public static List<PickUpModel> getPickupModels() {
        if (pickupModels == null) {
            pickupModels = new ArrayList<>();
        }
        return pickupModels;
    }

    public static void setPickupModels(List<PickUpModel> pickupModels) {
        if (pickupModels == null) {
            pickupModels = new ArrayList<>();
        }
        RDApplication.pickupModels = pickupModels;
        EventBus.getDefault().post(new PickupDataUpdatedEvent(pickupModels));
    }

    public static List<DeliveryModel> getDeliveryModels() {
        if (deliveryModels == null) {
            deliveryModels = new ArrayList<>();
        }
        return deliveryModels;
    }

    public static void setDeliveryModels(List<DeliveryModel> deliveryModels) {
        if (deliveryModels == null) {
            deliveryModels = new ArrayList<>();
        }
        EventBus.getDefault().post(new DeliveryDataUpdatedEvent(deliveryModels));
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
        if (pickupModels == null) {
            return null;
        }
        for (PickUpModel pickupModel : pickupModels) {
            if (pickupModel.getPickupNumber().equals(pickupNumber)) {
                return pickupModel;
            }
        }
        return null;
    }

    public static class DeliveryDataUpdatedEvent {

        public DeliveryDataUpdatedEvent(List<DeliveryModel> deliveryModels) {
            this.deliveryModels = deliveryModels;
        }

        public final List<DeliveryModel> deliveryModels;
    }

    public static class PickupDataUpdatedEvent {

        public PickupDataUpdatedEvent(List<PickUpModel> pickupModels) {
            this.pickupModels = pickupModels;
        }

        public final List<PickUpModel> pickupModels;
    }

    public static class AppOwnerDataUpdatedEvent {

        public AppOwnerDataUpdatedEvent(LoginResponse loginResponse) {
            this.loginResponse = loginResponse;
        }

        public final LoginResponse loginResponse;
    }
}
