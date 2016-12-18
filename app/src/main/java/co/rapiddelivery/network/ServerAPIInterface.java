package co.rapiddelivery.network;

import retrofit2.Call;
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
}
