package co.rapiddelivery;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

/**
 * Created by Kunal on 15/12/16.
 */

public class RDApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Here you start using the ActiveAndroid library.
        ActiveAndroid.initialize(this);
    }
}
