package co.rapiddelivery.services;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
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

/** Service to send current location
 * Created by Shraddha on 16/12/16.
 */

public class LocationService extends IntentService implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LocationService.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;

    public LocationService() {
        super(TAG);
    }

    public LocationService(String name) {
        super(name);
        Log.e(TAG, "loc LocationService");
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
    }

    protected void createLocationRequest() {
        Log.e(TAG, "loc req created ");
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.e(TAG, "loc onStart");
        mGoogleApiClient.connect();
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "loc onDestroy");
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "loc onHandleIntent");
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

    private void handlelocation() {
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

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.e(TAG, "FusedLocationApi lat " + mLastLocation.getLatitude() + " long " + mLastLocation.getLongitude());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG, "connection established");
        handlelocation();
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
        Log.e(TAG, "onLocationChanged lat " + location.getLatitude() + " long " + location.getLongitude());
    }
}
