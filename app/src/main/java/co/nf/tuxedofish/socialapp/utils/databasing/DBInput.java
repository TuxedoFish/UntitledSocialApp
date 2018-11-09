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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.nf.tuxedofish.socialapp.R;
import co.nf.tuxedofish.socialapp.utils.AuthorizationLogic;
import co.nf.tuxedofish.socialapp.utils.Constants;
import co.nf.tuxedofish.socialapp.utils.Utilities;

public class DBInput {
    /*
    Returns the result of a query to find information on a particular userID
     */
    public static void getUser(FirebaseFirestore db, String userId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        DocumentReference docref = db.collection("USERS").document(userId);
        docref.get().addOnCompleteListener(onCompleteListener);
    }

    public static void getMatches(FirebaseFirestore db, String userId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        CollectionReference colref = db.collection("USERS").document(userId).collection("MATCHES");
        colref.get().addOnCompleteListener(onCompleteListener);
    }

    public static void getGroupDocument(FirebaseFirestore db, String groupFileLoc, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        DocumentReference docref = db.collection("GROUPS").document(groupFileLoc);
        docref.get().addOnCompleteListener(onCompleteListener);
    }

    /*
    Returns online users nearby
     */
    public static Query getUsersNearby(FirebaseFirestore db, double latitude, double longitude) {
        GeoPoint[] bounds = DBUtils.getLocationBounds(latitude, longitude);

        CollectionReference colref = db.collection("USERS");
        Query query = colref.whereGreaterThan("location", bounds[0]).whereLessThan("location", bounds[1]);

        return query;
    }
    /*
    Adds user markers to the map in order to show on the map
     */
    public static void addMarkers(FirebaseFirestore db, final Context context, final GoogleMap googleMap,
                                  double mlatitude, double mlongitude) {
        final Utilities utils = new Utilities();
        Query query = DBInput.getUsersNearby(db, mlatitude, mlongitude);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    List<DocumentSnapshot> results = task.getResult().getDocuments();
                    if(task.getResult().size()!=0) {
                        BitmapDescriptor img = utils.bitmapDescriptorFromVector(context, R.drawable.ic_coffee_image_white);
                        //query worked
                        //there was people in the vicinity of you
                        googleMap.clear();

                        for(int i=0; i<results.size(); i++) {
                            if(results.get(i).getId().contains("user")) {
                                DocumentSnapshot snapshot = results.get(i);
                                GeoPoint user_loc = (GeoPoint) snapshot.get("location");

                                //add a marker for each person in your vicinity
                                LatLng user = new LatLng(user_loc.getLatitude(), user_loc.getLongitude());
                                googleMap.addMarker(new MarkerOptions().position(user).icon(img));
                            }
                        }
                    } else {
                        //nobody could be found near you
                        Log.d("notify", "nobody could be found in your area");
                    }
                } else {
                    //query failed
                    Log.d("notify", "nobody could be found in your area");
                }
            }
        });
    }

    public static void refreshUser(final FirebaseFirestore db, final AuthorizationLogic authLogic) {
        DBInput.getUser(db, authLogic.getCurrentUserId(), new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Long status = task.getResult().getLong("status");
                Map<String, Object> data = new HashMap<>(); data.put("status", Constants.STATUS_ONLINE);

                //Reset a user to just online if they are coming from not being matched
                if(status==null) {db.collection("USERS").
                        document(authLogic.getCurrentUserId()).update(data);
                } else if(status!=Constants.STATUS_MATCHED) {
                    db.collection("USERS").
                            document(authLogic.getCurrentUserId()).update(data);
                }
            }
        });
    }
}
