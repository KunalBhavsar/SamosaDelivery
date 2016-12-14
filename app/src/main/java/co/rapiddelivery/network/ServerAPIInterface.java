package co.rapiddelivery.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Kunal on 10/12/16.
 */

public interface ServerAPIInterface {

    @POST("app/emp/auth.php")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("app/emp/auth.php")
    Call<LoginResponse> loginNew(@Query("username") String username, @Query("password") String password);

}
