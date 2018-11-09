package co.nf.tuxedofish.socialapp.utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import co.nf.tuxedofish.socialapp.frontend.registration.PasswordFragment;

public class AuthorizationLogic {
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDB;

    public AuthorizationLogic() {
        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseFirestore.getInstance();
    }

    public void signIn(final String email, final String password, OnCompleteListener<AuthResult> callback) {
        if(!email.equals("") && !password.equals("")) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(callback);
        }
    }

    public void createUser(final String mFirstName, final String mFullName, final String mEmail,
                           final String is_student, final String department, final String mPass, final Activity context,
                           final OnCompleteListener<AuthResult> callback) {
    //When User has decided and confirmed their password now it comes time to add their data to firebase and load up the app
        mAuth.createUserWithEmailAndPassword(mEmail, mPass)
            .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("created", "createUserWithEmail:success");
                updateUserInfo(mFirstName, mFullName, mEmail, is_student, department, context);
                //Sign in and callback original function
                mAuth.signInWithEmailAndPassword(mEmail, mPass).addOnCompleteListener(callback);
            } else {
                // If sign in fails, display a message to the user.
                Log.w("failed", "createUserWithEmail : failure", task.getException());
                Toast.makeText(context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    });
}

    private void updateUserInfo(String mFirstName, String mFullName, String mEmail, String is_student, String department, final Activity context) {
        FirebaseUser user = mAuth.getCurrentUser();

        // Create a Map to store the data we want to set
        DocumentReference docRef = mDB.collection("USERS").document(user.getUid());
        Map<String, Object> data = new HashMap<>();
        data.put("first_name", mFirstName);
        data.put("full_name", mFullName);
        data.put("is_student", is_student);
        data.put("department", department);
        data.put("email", mEmail);
        data.put("status", Constants.STATUS_ONLINE);
        data.put("id", user.getUid());

        // Add a new document (asynchronously) in collection "users" with id "uid"
        docRef.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Log.d("alert", "added data to database");

                    //NEED TO DECIDE IF CONFIRMATION EMAILS ARE SUITABLE
                    //sendVerificationEmail();
                    Log.d("success", "log in after sign up : success");

                    // Log back out
                    FirebaseAuth.getInstance().signOut();
                } else {
                    Log.d("alert", "failed to add data to database" + task.getException());
                    PasswordFragment.alert("failed to add data ", context);
                }
            }
        });
    }

    public String getCurrentUserId() {
        return mAuth.getCurrentUser().getUid();
    }

    public void signOut() {
        mAuth.signOut();
    }

    private boolean checkIfEmailVerified()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.isEmailVerified()) { return true; } else { return false; }
    }

    public boolean isLoggedIn() {
        return true;
    }
}
