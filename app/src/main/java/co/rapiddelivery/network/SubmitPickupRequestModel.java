package co.rapiddelivery.network;

import java.util.List;

/**
 * Created by Kunal on 25/02/17.
 */

public class SubmitPickupRequestModel {

    List<String> waybills;

    public SubmitPickupRequestModel(List<String> waybills) {
        this.waybills = waybills;
    }

    public List<String> getWaybills() {
        return waybills;
    }

    public void setWaybills(List<String> waybills) {
        this.waybills = waybills;
    }
}
