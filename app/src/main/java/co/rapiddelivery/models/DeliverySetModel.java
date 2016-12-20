package co.rapiddelivery.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kunal on 19/12/16.
 */

public class DeliverySetModel {
    private int deliverySetId;
    private List<DeliveryModel> deliveryModels;

    public DeliverySetModel() {
        deliveryModels = new ArrayList<>();
    }

    public int getDeliverySetId() {
        return deliverySetId;
    }

    public void setDeliverySetId(int deliverySetId) {
        this.deliverySetId = deliverySetId;
    }

    public List<DeliveryModel> getDeliveryModels() {
        return deliveryModels;
    }

    public void setDeliveryModels(ArrayList<DeliveryModel> deliveryModels) {
        this.deliveryModels = deliveryModels;
    }
}
