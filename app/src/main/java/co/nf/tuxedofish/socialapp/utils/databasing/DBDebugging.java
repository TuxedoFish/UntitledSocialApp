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
        for(int i = 0; i< Constants.USERS_ADDED-1; i++) {
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
    public static void addFakeUser(FirebaseFirestore db, GeoPoint loc) {
        if(Constants.USERS_ADDED<Constants.MAX_NUMBER_FAKE_USERS) {
            Log.d("DEBUG", "New fake user added id : user_" + Constants.USERS_ADDED);
            db.collection("USERS").document("user_" + Constants.USERS_ADDED)
                    .set(getFakeUser("user_" + Constants.USERS_ADDED, loc));
            Constants.USERS_ADDED++;
        }
    }

    /*
    Populates the surrounding area with fake users
     */
    public static GeoPoint getSimulatedLocation(GeoPoint loc) {
        //all in miles
        double degree_lat = 69;
        double degree_long = Math.cos(Math.toRadians(loc.getLatitude())) * 69.172;

        double theta;
        double radius;

        double x;
        double y;
        double longitude_user;
        double latitude_user;

        //FUNCTION THAT POPULATES A CIRCLE UNIFORMLY
        theta = Math.PI*2 * Math.random();
        radius = Constants.QUERY_RADIUS * Math.sqrt(Math.random());

        x=radius*Math.cos(theta);
        y=radius*Math.sin(theta);

        longitude_user=y/degree_long;
        latitude_user=x/degree_lat;

        GeoPoint user_loc = new GeoPoint(loc.getLatitude()+latitude_user, loc.getLongitude()+longitude_user);

        return user_loc;
    }


    //Returns some fake user data
    private static Map<String, Object> getFakeUser(String mName, GeoPoint loc) {
        Map<String, Object> mFakeData = new HashMap<>();

        mFakeData.put("first_name", mName);
        mFakeData.put("age", -1);
        mFakeData.put("gender", "UNKNOWN");
        mFakeData.put("bio", "please enter a bio");
        mFakeData.put("profile_picture", "icon-profile.png");
        mFakeData.put("email", "someone@ucl.ac.uk");
        mFakeData.put("status", Constants.STATUS_ONLINE);
        mFakeData.put("id", mName);
        mFakeData.put("location", getSimulatedLocation(loc));

        return mFakeData;
    }
}
