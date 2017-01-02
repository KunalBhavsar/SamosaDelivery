package co.rapiddelivery.network;

import java.io.Serializable;

/**
 * Created by Shraddha on 27/12/16.
 */

public class DRList implements Serializable {

    private Delivery delivery[];
    private String status_code;

    public Delivery[] getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery[] delivery) {
        this.delivery = delivery;
    }

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }
}
