package co.rapiddelivery.network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kunal on 03/02/17.
 */

public class PickupResponseModel extends ServerResponseBase implements Serializable {
    private List<PickupResponseModel.PickupModel> pickups;

    public List<PickupResponseModel.PickupModel> getPickups() {
        return pickups;
    }

    public void setPickups(ArrayList<PickupResponseModel.PickupModel> pickups) {
        this.pickups = pickups;
    }

    public class PickupModel implements Serializable {

        private List<PickupResponseModel.PickupModel.RequestModel> requests;

        public List<PickupResponseModel.PickupModel.RequestModel> getRequests() {
            return requests;
        }

        public void setRequests(ArrayList<PickupResponseModel.PickupModel.RequestModel> requests) {
            this.requests = requests;
        }

        public class RequestModel implements Serializable {

            private String pick_no;
            private String status;
            private String address;
            private String pincode;
            private String phone;
            private String mode;
            private double lat;
            private double lng;
            private int expected_count;
            private String name;

            public String getPick_no() {
                return pick_no;
            }

            public void setPick_no(String pick_no) {
                this.pick_no = pick_no;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getPincode() {
                return pincode;
            }

            public void setPincode(String pincode) {
                this.pincode = pincode;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
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

            public int getExpected_count() {
                return expected_count;
            }

            public void setExpected_count(int expected_count) {
                this.expected_count = expected_count;
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
