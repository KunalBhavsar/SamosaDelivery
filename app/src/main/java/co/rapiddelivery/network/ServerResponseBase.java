package co.rapiddelivery.network;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Kunal on 15/12/16.
 */

public class ServerResponseBase {
    @SerializedName("status_code")
    private String statusCode;

    private String message;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
