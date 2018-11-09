package co.nf.tuxedofish.socialapp.utils.databasing;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import co.nf.tuxedofish.socialapp.utils.Constants;

public class DBOutput {
    /*
    Updates the status in USERS for a given user
    Returns the status for use in such a way [ mStatus = setStatus(...); ]
     */
    public static int setStatus(FirebaseFirestore db, int new_status, GeoPoint mLocation, String userID) {
        Map<String, Object> data = new HashMap<>();
        data.put("location", mLocation);
        data.put("status", new_status);

        Log.d("Info", "updated status of : " + userID + " to " + new_status);

        db.collection("USERS").document(userID).update(data);

        return new_status;
    }

    /*
    Updates the position of the user in the USERS collection
     */
    public static void makeUseOfNewLocation(FirebaseFirestore db, double longitude, double latitude, String mUserUID,
                                            OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        DocumentReference docref = db.collection("USERS").document(mUserUID);
        GeoPoint user_loc = new GeoPoint(latitude, longitude);

        DBInput.getUser(db, mUserUID, onCompleteListener);

        Map<String, Object> locationData = new HashMap<>();
        locationData.put("location", user_loc);

        Log.d("help", "tried to add data : " + mUserUID);

        docref.update(locationData);
    }
}
