package co.rapiddelivery.models;

/**
 * Created by Kunal on 19/12/16.
 */

public class PickUpModel {

    private String pickupNumber;
    private String name;
    private String pincode;
    private String phoneNumber;
    private String address;
    private int numberOfShipments;
    private int expectedWeight;
    private int cutOffTime;
    private double lat;
    private double lon;
    private int status;

    public String getPickupNumber() {
        return pickupNumber;
    }

    public void setPickupNumber(String pickupNumber) {
        this.pickupNumber = pickupNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getNumberOfShipments() {
        return numberOfShipments;
    }

    public void setNumberOfShipments(int numberOfShipments) {
        this.numberOfShipments = numberOfShipments;
    }

    public int getExpectedWeight() {
        return expectedWeight;
    }

    public void setExpectedWeight(int expectedWeight) {
        this.expectedWeight = expectedWeight;
    }

    public int getCutOffTime() {
        return cutOffTime;
    }

    public void setCutOffTime(int cutOffTime) {
        this.cutOffTime = cutOffTime;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
