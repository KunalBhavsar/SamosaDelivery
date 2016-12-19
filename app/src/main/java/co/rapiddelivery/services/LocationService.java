package co.rapiddelivery.services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Date;

import co.rapiddelivery.network.APIClient;
import co.rapiddelivery.network.ServerResponseBase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Service to send current location
 * Created by Shraddha on 16/12/16.
 */

public class LocationService extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LocationService.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;

    public LocationService() {
        super();
        Log.e(TAG, "constructor");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "loc onCreate");
        // Create an instance of GoogleAPIClient.
        if (isGooglePlayServicesAvailable() && mGoogleApiClient == null) {
            createLocationRequest();
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    protected void createLocationRequest() {
        Log.e(TAG, "loc req created ");
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "loc onDestroy");
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    private boolean isGooglePlayServicesAvailable() {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        Log.i(TAG, "play service status " + status);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            /*googleApiAvailability.getErrorDialog(thus, status, 0).show();*/
            Toast.makeText(this, status, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void handleLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.e(TAG, "FusedLocationApi lat " + mLastLocation.getLatitude() + " long " + mLastLocation.getLongitude() + " time" + new Date().toString());
        APIClient.getClient().submitLocation("marshal.chettiar", "", 80, "", "");
        //stopSelf();

        // TODO: 18/12/16 testing - whether this works in background or device is off
        APIClient.getClient().submitLocation("marshal.chettiar", "rapid123", 80, "19.237188", "72.844136")
                .enqueue(new Callback<ServerResponseBase>() {
                    @Override
                    public void onResponse(Call<ServerResponseBase> call, Response<ServerResponseBase> response) {
                        ServerResponseBase serverResponse = response.body();
                        Log.e(TAG, serverResponse.getMessage());
                        stopSelf();
                    }

                    @Override
                    public void onFailure(Call<ServerResponseBase> call, Throwable t) {
                        Log.e(TAG, t.getMessage(), t);
                        stopSelf();
                    }
                });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG, "connection established");
        //handleLocation();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        Log.i(TAG, "Location update started ..............: ");

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "connection failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        handleLocation();
        Log.e(TAG, "onLocationChanged lat " + location.getLatitude() + " long " + location.getLongitude());
    }
}
