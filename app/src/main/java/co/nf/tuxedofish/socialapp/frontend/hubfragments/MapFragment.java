package co.nf.tuxedofish.socialapp.frontend.hubfragments;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import co.nf.tuxedofish.socialapp.frontend.HubActivity;
import co.nf.tuxedofish.socialapp.frontend.hubfragments.mapping.LocationManager;
import co.nf.tuxedofish.socialapp.frontend.hubfragments.mapping.MapUIHandler;
import co.nf.tuxedofish.socialapp.frontend.hubfragments.matching.Debugger;
import co.nf.tuxedofish.socialapp.utils.Constants;
import co.nf.tuxedofish.socialapp.utils.PermissionHandler;
import co.nf.tuxedofish.socialapp.utils.User;
import co.nf.tuxedofish.socialapp.R;
import co.nf.tuxedofish.socialapp.utils.databasing.DBDebugging;
import co.nf.tuxedofish.socialapp.utils.databasing.DBInput;

public class MapFragment extends Fragment implements LocationManager.LocationUpdater {
    private MapView mMapView;
    private GoogleMap googleMap;

    private LocationManager mLocationManager;
    private Debugger mDebugger;
    private FirebaseFirestore db;

    private User mUser;
    private boolean isZoomed = false;
    private Handler handler;
    private Handler debugHandler;

    //Interface for passing information up the chain to the Hub Activity
    public Communicator mCommunicator;
    public interface Communicator {
        public void sendRequest();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);

        //Sets up everything map related
        PermissionHandler.getPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION,
                Constants.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        mMapView = rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        //Sets up the map in a static function in MapUIHandler and then obtains result
        MapUIHandler.setMapSettings(mMapView, new MapUIHandler.onMapSetUp() {
            @Override
            public void mapSetUp(GoogleMap mMap) {
                googleMap = mMap;
            }
        });
        //Unnecessary provided that the google map returned is non null
        try { MapsInitializer.initialize(getActivity().getApplicationContext());} catch (Exception e) { e.printStackTrace() ; }

        //Set up the classes that handle all the interactions with the map
        mLocationManager = new LocationManager(db);

        //Initialises database
        db = FirebaseFirestore.getInstance();

        //Debugger if required
        if(Constants.debugging) {
            mDebugger = new Debugger(db);
            debugHandler = new Handler();
            final int delay = 2000; //milliseconds

            debugHandler.postDelayed(new Runnable() {
                public void run() {
                    mDebugger.update(delay);
                    handler.postDelayed(this, delay);
                }}, delay);
        }

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCommunicator = (HubActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement Communicator");
        }
    }

    //Gets called when the current user's data is loaded from the database
    @Override
    public void onUserLoaded(User user) {
        mUser = user;
    }

    //Gets called every time that a movement in the location of the user is observed
    @Override
    public void onLocationMoved(GeoPoint location) {
        //Updates the fake users with positions around the user
        if(Constants.debugging) { mDebugger.updateLocation(location); }
        //Adds markers to the map for the user to observe
        DBInput.addMarkers(db, getActivity(), googleMap, location.getLatitude(), location.getLongitude());
    }

    public void beginSearch() {
        if(mUser!=null) {
            mUser.beginSearching(db);

            handler = new Handler();
            final int delay = 2000; //milliseconds

            handler.postDelayed(new Runnable() {
                public void run() {
                        mUser.update(db);
                        handler.postDelayed(this, delay);

                        if(!mUser.isMatched()) {
                            mUser.update(db);
                        } else {
                            //start up decision activity FOR RESULT
                            handler.removeCallbacksAndMessages(null);
                            mCommunicator.sendRequest();
                        }
                }
            }, delay);
        }
    }

    public User getMyUser() { return mUser; }
    public void enableLocation(String userID) { mLocationManager.enableLocationServices(userID); }
    public void signIn() { if(!isZoomed) { isZoomed = MapUIHandler.signIn(getActivity(), googleMap); } }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        DBDebugging.clearFakeUsers(db);
    }
}
