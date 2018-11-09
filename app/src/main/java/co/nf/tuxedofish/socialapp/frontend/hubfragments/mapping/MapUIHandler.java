package co.nf.tuxedofish.socialapp.frontend.hubfragments.mapping;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

import co.nf.tuxedofish.socialapp.utils.databasing.DBDebugging;

public class MapUIHandler {

    //Interface for returning the google map in a callback function
    public interface onMapSetUp {
        void mapSetUp(GoogleMap mMap);
    }

    /*
    Takes the original map view
    Obtains the google map from it
    Returns it
     */
    public static void setMapSettings(final MapView mapView, final onMapSetUp callback) {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
            try {
                // For showing a move to my location button
                mMap.setMyLocationEnabled(true);
            } catch(SecurityException  e) {
                Log.println(Log.DEBUG,"err","location err : " + e);
            }

            //Turn off all movement
            mMap.getUiSettings().setAllGesturesEnabled(false);

            callback.mapSetUp(mMap);
            }
        });
    }
    /*
    Zooms in the camera, cool effect needs to handled by another class
     */
    public static boolean signIn(Context context, GoogleMap googleMap) {
        android.location.LocationManager locationManager = (android.location.LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if(lastKnownLocationGPS!=null) {
                    // For zooming automatically to the location of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lastKnownLocationGPS.getLatitude(),
                            lastKnownLocationGPS.getLongitude())).zoom(14).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    return true;
                }
            }
        }
        return false;
    }
}
