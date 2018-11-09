package co.nf.tuxedofish.socialapp.utils.databasing;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.nf.tuxedofish.socialapp.R;
import co.nf.tuxedofish.socialapp.utils.Constants;
import co.nf.tuxedofish.socialapp.utils.User;
import co.nf.tuxedofish.socialapp.utils.Utilities;

public class DBDebugging {
    /*
    Deletes all the fake users upon debug device closing the app
     */
    public static void clearFakeUsers(FirebaseFirestore db) {
        for(int i = 0; i< Constants.NUMBER_FAKE_USERS-1; i++) {
            db.collection("USERS").document("user_"+i).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Log.d("help","user_x was deleted");
                    }
                }
            });
        }
    }

    /*
    Adds a whole series of fake data to the USERS collection
     */
    public static void addFakeUsers(FirebaseFirestore db) {
        for (int i = 0; i < Constants.NUMBER_FAKE_USERS; i++) {
            db.collection("USERS").document("user_" + i).set(getFakeUser("user_"+i));
        }
    }

    /*
    Populates the surrounding area with fake users
     */
    public static void populateArea(FirebaseFirestore db, double longitude, double latitude, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        //all in miles
        double degree_lat = 69;
        double degree_long = Math.cos(Math.toRadians(latitude)) * 69.172;

        double theta = 0;
        double radius = 0;

        double x = 0;
        double y = 0;
        double longitude_user = 0;
        double latitude_user = 0;

        ArrayList<User> users = new ArrayList<User>();

        for(int i = 0; i< Constants.NUMBER_FAKE_USERS; i++) {
            //FUNCTION THAT POPULATES A CIRCLE UNIFORMLY
            theta = Math.PI*2 * Math.random();
            radius = Constants.QUERY_RADIUS * Math.sqrt(Math.random());

            x=radius*Math.cos(theta);
            y=radius*Math.sin(theta);

            longitude_user=y/degree_long;
            latitude_user=x/degree_lat;

            //SET LOCATION IN DATABASE
            DocumentReference docref = db.collection("USERS").document("user_"+i);
            GeoPoint user_loc = new GeoPoint(latitude+latitude_user, longitude+longitude_user);

            DBInput.getUser(db, "user_"+i, onCompleteListener);

            Map<String, Object> locationData = new HashMap<>();
            locationData.put("location", user_loc);
            locationData.put("status", Constants.STATUS_ONLINE);

            docref.update(locationData);
        }
    }


    /*
    Returns some fake user data
     */
    private static Map<String, Object> getFakeUser(String mName) {
        Map<String, Object> mFakeData = new HashMap<>();

        mFakeData.put("first_name", mName);
        mFakeData.put("age", -1);
        mFakeData.put("gender", "UNKNOWN");
        mFakeData.put("bio", "please enter a bio");
        mFakeData.put("profile_picture", "icon-profile.png");
        mFakeData.put("email", "someone@ucl.ac.uk");
        mFakeData.put("status", Constants.STATUS_ONLINE);
        mFakeData.put("id", mName);

        return mFakeData;
    }
}
