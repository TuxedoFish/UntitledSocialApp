package co.nf.tuxedofish.socialapp.frontend.hubfragments.mapping;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import co.nf.tuxedofish.socialapp.utils.User;
import co.nf.tuxedofish.socialapp.utils.databasing.DBOutput;

public class LocationManager implements LocationListener {
    private boolean live = false;

    private String mUserID;
    private FirebaseFirestore db;

    private GeoPoint recentLoc;

    public LocationManager(FirebaseFirestore db) {
        this.db = db;
        this.recentLoc = new GeoPoint(0, 0);
    }

    public LocationManager.LocationUpdater mLocationUpdater;
    public interface LocationUpdater {
        void onLocationMoved(GeoPoint location);
        void onUserLoaded(User user);
    }
    public void enableLocationServices(String userID) {
        this.mUserID = userID;
        this.live = true;
    }

    public void onLocationChanged(Location location) {
        if(live) {
            // Called when a new location is found by the network location provider.
            recentLoc = new GeoPoint(location.getLatitude(), location.getLongitude());

            DBOutput.makeUseOfNewLocation(db, recentLoc.getLongitude(), recentLoc.getLatitude(),
                    mUserID, new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) { addUserData(task);
                        }
                    });

            mLocationUpdater.onLocationMoved(recentLoc);
        }
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {}

    public void onProviderEnabled(String provider) {}

    public void onProviderDisabled(String provider) {}

    public void addUserData(@NonNull Task<DocumentSnapshot> task) {
        if(task.isSuccessful()) {
            User user = null;
            DocumentSnapshot result = task.getResult();

            if (result.exists()) {
                user = new User(result.getId(), (String)result.get("first_name"), (String)result.get("email"),
                        (String)result.get("full_name"), (String)result.get("department"),
                        (String)result.get("is_student"), recentLoc);
            } else {
                Log.d("error", "no such user found @ Line 81 of LocationManager");
            }
            if(user!=null) {
                if (user.getMyId().equals(mUserID)) {
                    mLocationUpdater.onUserLoaded(user);
                }
            }
        } else {
            Log.d("error", "Database query looking for user failed");
        }
    }

}
