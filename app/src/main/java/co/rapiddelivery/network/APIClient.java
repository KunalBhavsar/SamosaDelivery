package co.rapiddelivery.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Kunal on 10/12/16.
 */

public class APIClient {
    private static final String TEST_BASE_URL = "http://trace.rapiddelivery.co/api/";
    private static final String PROD_BASE_URL = "http://trace.rapiddelivery.co/api/";

    private static Retrofit retrofit = null;


    public static ServerAPIInterface getClient() {
        if (retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logging);
            httpClient.connectTimeout(2, TimeUnit.MINUTES);

            OkHttpClient client = httpClient.build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(TEST_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit.create(ServerAPIInterface.class);
    }
}
