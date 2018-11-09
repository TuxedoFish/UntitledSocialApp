package co.nf.tuxedofish.socialapp.frontend.hubfragments.matching;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import co.nf.tuxedofish.socialapp.utils.User;

public class Debugger {

    private ArrayList<User> mUsers;
    private FirebaseFirestore db;

    public Debugger(FirebaseFirestore db) {
        mUsers = new ArrayList<>();
        this.db = db;
    }

    //The listener that receives the data on the user loaded in and saves it
    public OnCompleteListener addData = new OnCompleteListener<DocumentSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isSuccessful()) {
                User user = null;
                DocumentSnapshot result = task.getResult();

                if (result.exists()) {
                    user = new User(result.getId(), (String) result.get("first_name"), (String) result.get("email"),
                            (String) result.get("full_name"), (String) result.get("department"),
                            (String) result.get("is_student"), result.getGeoPoint("location"));
                } else {
                    Log.d("error", "no such user found @ Line 30 of LocationManager");
                }

                boolean shouldAdd = true;

                for (int i = 0; i < mUsers.size(); i++) {
                    if (mUsers.get(i).getMyId() == user.getMyId()) {
                        shouldAdd = false;
                    }
                }
                if (shouldAdd) {
                    user.beginSearching(db);
                    mUsers.add(user);
                }
            }
        }
    };

    public void update(int delay) {
        for(int i=0; i<mUsers.size(); i++) {
            if (!mUsers.get(i).isMatched()) {
                mUsers.get(i).update(db);
            }
        }
    }
}
