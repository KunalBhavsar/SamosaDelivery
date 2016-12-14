package co.rapiddelivery.network;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Kunal on 15/12/16.
 */

public class LoginResponse extends ServerResponseBase {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
