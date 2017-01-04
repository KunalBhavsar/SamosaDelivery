package co.rapiddelivery.network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shraddha on 27/12/16.
 */

public class DeliveryResponseModel extends ServerResponseBase implements Serializable {

    private List<DeliveryModel> delivery;

    public List<DeliveryModel> getDelivery() {
        return delivery;
    }

    public void setDelivery(ArrayList<DeliveryModel> delivery) {
        this.delivery = delivery;
    }

    public class DeliveryModel implements Serializable {

        private List<ShipmentModel> shipments;
        private String dispatch_number;

        public List<ShipmentModel> getShipments() {
            return shipments;
        }

        public void setShipments(ArrayList<ShipmentModel> shipments) {
            this.shipments = shipments;
        }

        public String getDispatch_number() {
            return dispatch_number;
        }

        public void setDispatch_number(String dispatch_number) {
            this.dispatch_number = dispatch_number;
        }

        public class ShipmentModel implements Serializable {

            private String awb;
            private String status;
            private String address_1;
            private String address_2;
            private String pincode;
            private String value;
            private String flow;
            private String mode;
            private double lat;
            private double lng;
            private String dispatch_count;
            private String name;

            public String getAwb() {
                return awb;
            }

            public void setAwb(String awb) {
                this.awb = awb;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getAddress_1() {
                return address_1;
            }

            public void setAddress_1(String address_1) {
                this.address_1 = address_1;
            }

            public String getAddress_2() {
                return address_2;
            }

            public void setAddress_2(String address_2) {
                this.address_2 = address_2;
            }

            public String getPincode() {
                return pincode;
            }

            public void setPincode(String pincode) {
                this.pincode = pincode;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public String getFlow() {
                return flow;
            }

            public void setFlow(String flow) {
                this.flow = flow;
            }

            public String getMode() {
                return mode;
            }

            public void setMode(String mode) {
                this.mode = mode;
            }

            public double getLat() {
                return lat;
            }

            public void setLat(double lat) {
                this.lat = lat;
            }

            public double getLng() {
                return lng;
            }

            public void setLng(double lng) {
                this.lng = lng;
            }

            public String getDispatch_count() {
                return dispatch_count;
            }

            public void setDispatch_count(String dispatch_count) {
                this.dispatch_count = dispatch_count;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}