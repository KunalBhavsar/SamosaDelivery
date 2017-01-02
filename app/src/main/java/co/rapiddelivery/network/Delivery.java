package co.rapiddelivery.network;

import java.io.Serializable;

/**
 * Created by Shraddha on 27/12/16.
 */

public class Delivery implements Serializable {

    private Shipment shipments[];
    private String dispatch_number;

    public Shipment[] getShipments() {
        return shipments;
    }

    public void setShipments(Shipment[] shipments) {
        this.shipments = shipments;
    }

    public String getDispatch_number() {
        return dispatch_number;
    }

    public void setDispatch_number(String dispatch_number) {
        this.dispatch_number = dispatch_number;
    }
}
