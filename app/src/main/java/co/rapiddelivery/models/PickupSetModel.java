package co.rapiddelivery.models;

import java.util.List;

/**
 * Created by Kunal on 19/12/16.
 */

public class PickupSetModel {
    private int pickupSetId;
    private List<PickupSetModel> pickupSetModels;

    public int getPickupSetId() {
        return pickupSetId;
    }

    public void setPickupSetId(int pickupSetId) {
        this.pickupSetId = pickupSetId;
    }

    public List<PickupSetModel> getPickupSetModels() {
        return pickupSetModels;
    }

    public void setPickupSetModels(List<PickupSetModel> pickupSetModels) {
        this.pickupSetModels = pickupSetModels;
    }
}
