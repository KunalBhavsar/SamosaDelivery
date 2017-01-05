package co.rapiddelivery.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import co.rapiddelivery.utils.KeyConstants;
import co.rapiddelivery.utils.NotificationUtils;
import co.rapiddelivery.utils.SPrefUtils;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Shraddha on 4/1/17.
 */

public class GpsLocationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            int loginStatus = SPrefUtils.getIntegerPreference(context, SPrefUtils.LOGIN_STATUS, KeyConstants.LOGIN_STATUS_BLANK);
            LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && loginStatus == KeyConstants.LOGIN_STATUS_LOGGED_IN) {
                NotificationUtils.sendNotification("Please turn on GPS to provide location", context);
            }

        }
    }
}
