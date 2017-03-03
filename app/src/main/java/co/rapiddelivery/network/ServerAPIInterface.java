package co.rapiddelivery.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Kunal on 10/12/16.
 */

public interface ServerAPIInterface {

    @FormUrlEncoded
    @POST("app/emp/auth.php")
    Call<LoginResponse> login(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("app/emp/update_location.php")
    Call<ServerResponseBase> submitLocation(@Field("username") String username, @Field("password") String password, @Field("battery") int batteryState, @Field("lat") String lat, @Field("long") String log);

    @FormUrlEncoded
    @POST("app/ops/drs.php")
    Call<DeliveryResponseModel> getDeliveryList(@Field("username") String username, @Field("password") String password, @Field("emp_id") String empId);

    @FormUrlEncoded
    @POST("app/ops/start.php")
    Call<ResponseBody> startDeliveryTask(@Field("username") String username, @Field("password") String password, @Field("emp_id") String empId,
                                         @Field("waybill") String waybill);

    @FormUrlEncoded
    @POST("app/ops/cancel.php")
    Call<ResponseBody> cancelDeliveryTask(@Field("username") String username, @Field("password") String password, @Field("emp_id") String empId,
                                         @Field("waybill") String waybill);
    @FormUrlEncoded
    @POST("app/ops/update.php")
    Call<ResponseBody> updateDeliveryTask(@Field("username") String username, @Field("password") String password, @Field("emp_id") String empId,
                                          @Field("waybill") String waybill, @Field("delivered") String deliveryStatus, @Field("remarks") String remarks, @Field("date") String date);

    @FormUrlEncoded
    @POST("app/ops/pickup.php")
    Call<PickupResponseModel> getPickupList(@Field("username") String username, @Field("password") String password, @Field("emp_id") String empId);

    @FormUrlEncoded
    @POST("app/ops/check_awb.php")
    Call<ServerResponseBase> checkAwbAvailability(@Field("username") String username, @Field("password") String password, @Field("emp_id") String empId,  @Field("pick_no") String pickupNumber, @Field("waybill") String waybill);

    @FormUrlEncoded
    @POST("app/ops/submit_pickup.php")
    Call<ServerResponseBase> submitPickup(@Field("username") String username, @Field("password") String password, @Field("emp_id") String empId, @Field("status") String deliveryStatus,
                                          @Field("remarks") String remarks, @Field("date") String date, @Field("pick_no") String pickupNumber, @Field("json_list") String waybillList);
}

