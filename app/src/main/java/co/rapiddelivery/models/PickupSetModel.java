package co.rapiddelivery.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kunal on 19/12/16.
 */

public class PickupSetModel {
    private int pickupSetId;
    private List<PickUpModel> pickupSetModels;

    public PickupSetModel() {
        pickupSetModels = new ArrayList<>();
    }

    public int getPickupSetId() {
        return pickupSetId;
    }

    public void setPickupSetId(int pickupSetId) {
        this.pickupSetId = pickupSetId;
    }

    public List<PickUpModel> getPickupSetModels() {
        return pickupSetModels;
    }

    public void setPickupSetModels(List<PickUpModel> pickupSetModels) {
        this.pickupSetModels = pickupSetModels;
    }
}
