package co.nf.tuxedofish.socialapp.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import co.nf.tuxedofish.socialapp.utils.databasing.DBInput;
import co.nf.tuxedofish.socialapp.utils.databasing.DBNaming;
import co.nf.tuxedofish.socialapp.utils.databasing.DBOutput;

public class User implements Serializable {
    //ESSENTIAL USER INFO
    private String mFirstName, mEmail, mFullName, mDepartment, mID;
    private boolean isStudent;
    private GeoPoint mLocation;
    private int mStatus = Constants.STATUS_ONLINE;

    //OPTIONAL USER INFO
    private Long mAge;
    private String mImageURI;
    private Constants.Gender mGender;

    //FOR LOGIC
    private String groupFileLoc;
    private boolean statusLoaded, matchesLoaded, groupDocLoaded, matched = false;
    private Long groupSize;
    private ArrayList<User> matches;

    /*
    Intialise a user with all the capability to be able to search
     */
    public User(String mID, String firstname, String email, String full_name,
                String department, String is_student, GeoPoint location) {
        setFirstName(firstname); setEmail(email); setFullName(full_name);
        setDepartment(department); setIsStudent(Boolean.getBoolean(is_student));
        this.mID = mID; mLocation = location; matches=new ArrayList<>();
    }

    /*
    Initialise a user with the purpose of displaying information
     */
    public User(String mID, String firstName) {
        this.mID = mID; this.mFirstName = firstName;
    }

    public ArrayList<User> getMatches() {
        return matches;
    }

    public void beginSearching(FirebaseFirestore db) { setmStatus(db, Constants.STATUS_SEARCHING); }
    public void setmStatus(FirebaseFirestore db, int new_status) { mStatus = DBOutput.setStatus(db, new_status, mLocation, mID); }
    public int getmStatus() {
        return mStatus;
    }

    public void update(FirebaseFirestore db) {
        if(matched) {
            //Function will check and update if there has been any changes in the group file
            checkGroupDoc(db);
            //No need to evaluate rest of the logic if we have already matched
            return;
        }

        /*
        Basically just need to check that all of the data is available to be able to warrant classifying a match
        Checklist:
               mDocument:
                    status changed 0
                    matches exist 0
               groupDocument exists 0
         3 checks
         */

        //Checks that the status has loaded and if it has then counts as status loaded
        if(!statusLoaded) {
            DBInput.getUser(db, mID, new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult() != null && task.getResult().getDouble("status") != null) {
                        if (task.getResult().getDouble("status") == Constants.STATUS_MATCHED) {
                            groupFileLoc = task.getResult().getString("group_file_loc");
                            mStatus = (int) Math.round(task.getResult().getDouble("status"));
                            statusLoaded = true;
                        }
                    } else {
                        Log.e("error ", "error loading user : Could not find user, " + mID);
                        Log.e("error ", "location: line 84 of utils/User.java");
                    }
                }
            });
        }

        //Checks that the document has been loaded into the group only once we found where
        //the group file is located
        if(statusLoaded && !groupDocLoaded) {
            Log.d("information", "looking for : " + groupFileLoc);
            DBInput.getGroupDocument(db, groupFileLoc, new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.getResult()!=null) {
                        groupSize = task.getResult().getLong("group_size");
                        groupDocLoaded = true;
                    } else {
                        Log.d("info ", "no group located found for : " + mID + " at : " + groupFileLoc);
                    }
                }
            });
        }

        //Checks that the matches have appeared but only once the group document is available and
        //so we know how big the group should be
        if(groupDocLoaded) {
            DBInput.getMatches(db, mID, new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.getResult() != null) {
                        //Only load data if no users yet
                        if(matches.size()==0) {
                            List<DocumentSnapshot> results = task.getResult().getDocuments();
                            //Load the data into my matches
                            for (int i = 0; i < groupSize-1; i++) {
                                //Create new user
                                matches.add(new User(results.get(i).getString("id"),
                                        results.get(i).getString("first_name")));
                            }
                        }
                        //Update the fact the matches have loaded
                        matchesLoaded = true;
                    } else {
                        Log.d("info ", "no matches found for : " + mID);
                    }
                }
            });
        }

        //Having confirmed all 3 things it logs the match
        Log.d("info", mID + " : " + statusLoaded + " : " + matchesLoaded + " : " + groupDocLoaded);
        if(statusLoaded && matchesLoaded && groupDocLoaded) {
            Log.d("info", "managed to find a match for : " + mID);
            matched = true;
        }
    }

    public void checkGroupDoc(FirebaseFirestore db) {
        DBInput.getMatches(db, mID, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    Log.d("success", "succesfully loaded group doc to check size in User");
                    List<DocumentSnapshot> results = task.getResult().getDocuments();
                    int newSize = results.size();

                    if(newSize != groupSize) {
                        //Our size has changed

                        //Loops and adds all the users into matches that have not previously been seen
                        for(int i=(int)(groupSize-1); i<(newSize-1); i++) {
                            matches.add(new User(results.get(i).getString("id"),
                                    results.get(i).getString("first_name")));
                        }
                    }
                } else {
                    Log.e("error", "Failed to find group file : " + groupFileLoc + " in line 163 of User.java");
                }
            }
        });
    }

    public boolean isMatched() { return matched; }
    public String getMyId() { return mID; }

    public String getFirstName() { return mFirstName; }
    public void setFirstName(String firstName) { this.mFirstName = firstName; }

    public String getFullName() { return mFullName; }
    public void setFullName(String fullName) { this.mFullName = fullName; }

    public String getEmail() { return mEmail; }
    public void setEmail(String email) { this.mEmail = email; }

    public String getDepartment() { return mDepartment; }
    public void setDepartment(String department) { this.mDepartment = department; }

    public Boolean isStudent() { return isStudent; }
    public void setIsStudent(Boolean isStudent) { this.isStudent=isStudent; }

    public Long getAge() { return mAge; }
    public void setAge(Long age) { this.mAge = age; }

    public String getImageURI() { return mImageURI; }
    public void setImageURI(String imageURI) { this.mImageURI = imageURI; }

    public Constants.Gender getGender() { return mGender; }
    public void setGender(String gender) {
        if(gender.equals("MALE")) {
            this.mGender = Constants.Gender.MALE;
        } else if(gender.equals("FEMALE")) {
            this.mGender = Constants.Gender.FEMALE;
        } else {
            this.mGender = Constants.Gender.UNKNOWN;
        }
    }
}
