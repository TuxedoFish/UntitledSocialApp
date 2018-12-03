package co.nf.tuxedofish.socialapp.frontend.hubfragments.mapping;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import co.nf.tuxedofish.socialapp.R;

public class DirectionsManager {
    /*
    Grabs the GeoContext in order to load in directions
     */
    private static GeoApiContext getGeoContext(Context context) {
        GeoApiContext geoApiContext = new GeoApiContext.Builder().queryRateLimit(3).apiKey(context.getString(R.string.google_maps_key))
                .connectTimeout(1, TimeUnit.SECONDS).readTimeout(1, TimeUnit.SECONDS)
                .writeTimeout(1, TimeUnit.SECONDS).build();
        return geoApiContext;
    }
    /*
    Load FULL directions from A (mLocation) to B (latitude, longitude)
     */
    public static void loadDirections(double latitude, double longitude, com.google.maps.model.LatLng mLocation,
                                      GoogleMap googleMap, Context appContext) throws IOException, ApiException, InterruptedException{
        GeoApiContext context = getGeoContext(appContext);

        com.google.maps.model.LatLng meetupLocation = new com.google.maps.model.LatLng(latitude, longitude);

        DateTime departure_time = new DateTime();
        departure_time = departure_time.plusHours(2);

        DirectionsResult results = DirectionsApi.newRequest(context).origin(mLocation).destination(meetupLocation)
                .departureTime(departure_time).mode(TravelMode.WALKING).await();

        addDirectionsToMap(results, googleMap);
        Log.d("info", "loading directions from : " + mLocation + " : " + meetupLocation + " at " + departure_time);
    }
    /*
    Will add the full range of directions to the map
     */
    private static void addDirectionsToMap (DirectionsResult results, GoogleMap mMap) {
        if(results.routes.length == 0) {
            Log.e("err", "failed to create path to meetup location : ");
        } else {
            //adds start location
            mMap.addMarker(new MarkerOptions().position(new com.google.android.gms.maps.model.LatLng(
                    results.routes[0].legs[0].startLocation.lat, results.routes[0].legs[0].startLocation.lng)));
            //adds end location
            mMap.addMarker(new MarkerOptions().position(new com.google.android.gms.maps.model.LatLng(
                    results.routes[0].legs[0].endLocation.lat, results.routes[0].legs[0].endLocation.lng)));
            //adds poly line representing journey
            List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
            mMap.addPolyline(new PolylineOptions().addAll(decodedPath));

            //Communicates up the chain that the next direction has occurred
            //mCommunicator.nextDirection(results.routes[0].legs[0].steps[0].htmlInstructions);

            //focuses the camera onto the 1st legs direction
            focusOn(results, mMap, 0);

            Log.d("info", "completed adding directions to map");
        }
    }

    public static void focusOn(DirectionsResult results, GoogleMap mMap, int leg_no) {
        //Calculates the variables required to orientate the camera
        //ORIENTATES AROUND THE ith LOCATION [i]
        double l1 =  results.routes[0].legs[leg_no].startLocation.lng; double l2 =  results.routes[0].legs[leg_no].endLocation.lng;
        double la1 =  results.routes[0].legs[leg_no].startLocation.lat; double la2 =  results.routes[0].legs[leg_no].endLocation.lat;

        double y = Math.sin(l2-l1) * Math.cos(la2);
        double x = Math.cos(la1)*Math.sin(la2) -
                Math.sin(la1)*Math.cos(la2)*Math.cos(l2-l1);
        float bearing = (float)Math.toDegrees(Math.atan2(y, x));
        //This segment will set the camera position to the current direction
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(results.routes[0].legs[0].startLocation.lat, results.routes[0].legs[0].startLocation.lng))      // Sets the center of the map to Mountain View
                .zoom(18)                   // Sets the zoom
                .bearing(bearing)                // Sets the orientation of the camera to east
                .tilt(45)                   // Sets the tilt of the camera to 30 degrees
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /*
    THIS IS THE CODE FOR ADDING THE UI WHEN A DIRECTION GETS LOADED
     */
//            try {
//                mMapFragment.loadDirections(longitude, latitude);
//            } catch (IOException ie) {
//                Log.e("error", "IOException loading directions : " + ie.getLocalizedMessage());
//            } catch (ApiException ae) {
//                Log.e("error", "IOException loading directions : " + ae.getLocalizedMessage());
//            } catch (InterruptedException ine) {
//                Log.e("error", "IOException loading directions : " + ine.getLocalizedMessage());
//            }
//            ConstraintLayout layout = findViewById(R.id.top_bar);
//            // Gets the layout params that will allow you to resize the layout
//            ViewGroup.LayoutParams params = layout.getLayoutParams();
//            // Changes the height and width to the specified *pixels*
//            params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());;
//            layout.setLayoutParams(params);
//
//            mTravelOverlayFragment = new TravelOverlayFragment();
//            openFragment(mTravelOverlayFragment, R.id.top_bar);
//
//            mTravelOverlayFragment.changeDirectionStep(CURRENT_DIRECTION);

            /*
             !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                FOR PROTOTYPING OPEN GOOGLE MAPS FOR DIRECTIONS
             !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
             */
//
//    @Override
//    public void nextDirection(String DIRECTION_STEP) {
//        CURRENT_DIRECTION = DIRECTION_STEP;
//        if(mTravelOverlayFragment != null) {
//            mTravelOverlayFragment.changeDirectionStep(DIRECTION_STEP);
//        }
//    }
}
