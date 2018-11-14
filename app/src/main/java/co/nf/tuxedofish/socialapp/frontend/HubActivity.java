package co.nf.tuxedofish.socialapp.frontend;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import co.nf.tuxedofish.socialapp.R;
import co.nf.tuxedofish.socialapp.frontend.hubfragments.ConnectionFragment;
import co.nf.tuxedofish.socialapp.frontend.hubfragments.CountdownFragment;
import co.nf.tuxedofish.socialapp.frontend.hubfragments.MapFragment;
import co.nf.tuxedofish.socialapp.frontend.hubfragments.SearchOverlayFragment;
import co.nf.tuxedofish.socialapp.frontend.hubfragments.TravelOverlayFragment;
import co.nf.tuxedofish.socialapp.frontend.matchingfragments.QuestionsFragment;
import co.nf.tuxedofish.socialapp.frontend.registration.ConsentFragment;
import co.nf.tuxedofish.socialapp.frontend.registration.LogInFragment;
import co.nf.tuxedofish.socialapp.frontend.registration.PasswordFragment;
import co.nf.tuxedofish.socialapp.utils.AuthorizationLogic;
import co.nf.tuxedofish.socialapp.utils.User;
import co.nf.tuxedofish.socialapp.utils.Utilities;
import co.nf.tuxedofish.socialapp.utils.databasing.DBInput;

public class HubActivity extends AppCompatActivity implements MapFragment.Communicator, SearchOverlayFragment.clickListener,
        ConnectionFragment.OnFragmentInteractionListener, CountdownFragment.OnFragmentInteractionListener,
        TravelOverlayFragment.OnFragmentInteractionListener, ConsentFragment.onConsentConfirmed, PasswordFragment.PasswordCheck,
        LogInFragment.OnLogInAction, QuestionsFragment.OnFragmentInteractionListener{
    private SearchOverlayFragment mSearchOverlayFragment;
    private MapFragment mMapFragment;
    private ConnectionFragment mConnectionFragment;
    private TravelOverlayFragment mTravelOverlayFragment;
    private ConsentFragment mConsentFragment;
    private PasswordFragment mPasswordFragment;
    private LogInFragment mLogInFragment;
    private QuestionsFragment mQuestionsFragment;

    private AuthorizationLogic authLogic;

    private boolean foundGroup = false;
    private boolean questionsFinished = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub);

        authLogic = new AuthorizationLogic();

        Intent intent = getIntent();
        String is_registration = intent.getStringExtra("is_registration");
        if(is_registration==null) {is_registration = "false";}

        mMapFragment = new MapFragment();
        Utilities.openFragment(mMapFragment, R.id.contentPanel, getFragmentManager());
        if(is_registration.equals("register_via_ucl")) {
            mConsentFragment = new ConsentFragment();
            Utilities.openFragment(mConsentFragment, R.id.overlayPanel, getFragmentManager());
        } else if((is_registration.equals("register"))) {

        } else if((is_registration.equals("log_in"))) {
            mLogInFragment = new LogInFragment();
            Utilities.openFragment(mLogInFragment, R.id.overlayPanel, getFragmentManager());
        } else {
            mSearchOverlayFragment = new SearchOverlayFragment();
            Utilities.openFragment(mSearchOverlayFragment, R.id.overlayPanel, getFragmentManager());
            mMapFragment.enableLocation(authLogic.getCurrentUserId());
        }
        CountdownFragment mCountdownFragment = new CountdownFragment();
        Utilities.openFragment(mCountdownFragment, R.id.top_bar, getFragmentManager());
    }

    @Override
    public void finishedQuestions() {
        //Either carry on loading or switch to match screen
        questionsFinished = true;

        if(foundGroup) {
            loadMatchScreen();
        }
    }

    public void loadMatchScreen() {
        User mainUser = mMapFragment.getMyUser();
        Log.d("info", "found a match with : " + mainUser.getMatches().size() + " people");

        mConnectionFragment = ConnectionFragment.newInstance(mainUser);

        Utilities.closeFragment(R.id.overlayPanel, getFragmentManager());
        Utilities.openFragment(mConnectionFragment, R.id.overlayPanel, getFragmentManager());
    }

    @Override
    public void signIn(String email, String password) {
        //Here we need to do the sign in logic
        //Make the signing in and database logic be handled externally from this activity
        authLogic.signIn(email, password, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                Log.d("notify", "log in : success");

                mMapFragment.enableLocation(authLogic.getCurrentUserId());
                mMapFragment.signIn();

                Utilities.closeFragment(R.id.overlayPanel, getFragmentManager());
                mSearchOverlayFragment = new SearchOverlayFragment();
                Utilities.openFragment(mSearchOverlayFragment, R.id.overlayPanel, getFragmentManager());

                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                DBInput.refreshUser(db, authLogic);
            } else {
                // If sign in fails, display a message to the user.
                Log.w("error", "log in : failure", task.getException());
                Toast.makeText(HubActivity.this, "Log in failed",
                        Toast.LENGTH_SHORT).show();
            }
            }
        });
    }
    @Override
    public void onPasswordSet(String password) {
        final Intent intent = getIntent();

        //add user
        authLogic.createUser(intent.getStringExtra("first_name").split(" ")[0], intent.getStringExtra("full_name"),
                intent.getStringExtra("email"), intent.getStringExtra("is_student"),
                intent.getStringExtra("department"), password, this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mMapFragment.enableLocation(authLogic.getCurrentUserId());
                    }
                });

        // Go to main screen
        toApp();
    }

    private void toApp() {
        Utilities.closeFragment(R.id.overlayPanel, getFragmentManager());

        mSearchOverlayFragment = new SearchOverlayFragment();
        Utilities.openFragment(mSearchOverlayFragment, R.id.overlayPanel, getFragmentManager());
    }

    @Override
    public void onConsentConfirmed() {
        //When user accepts the policy go from consent to set password screen
        Intent mIntent = getIntent();

        mPasswordFragment = PasswordFragment.newInstance(mIntent.getExtras().getString("email"), mIntent.getExtras().getString("first_name"));

        Utilities.closeFragment(R.id.overlayPanel, getFragmentManager());
        Utilities.openFragment(mPasswordFragment, R.id.overlayPanel, getFragmentManager());
    }

    @Override
    public void onCountdownFinished(double longitude, double latitude) {
        if(foundGroup) {
            //Change to travelling
            Utilities.closeFragment(R.id.overlayPanel, getFragmentManager());

            String query = "google.navigation:q="+latitude + "," +longitude+"&mode=w";
            Log.d("info", query);

            Uri gmmIntentUri = Uri.parse(query);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
    }

    @Override
    public void onSearch(View v) {
        mMapFragment.beginSearch();

        Utilities.closeFragment(R.id.overlayPanel, getFragmentManager());
        this.mQuestionsFragment = new QuestionsFragment();
        Utilities.openFragment(mQuestionsFragment, R.id.overlayPanel, getFragmentManager());
    }

    @Override
    public void sendRequest() {
        if(this.questionsFinished) {
            loadMatchScreen();
        }

        foundGroup = true;
    }

    /*
    Interface that comes from CONNECTION FRAGMENT
     */
    @Override
    public void onFragmentInteraction(Uri uri) {
    }
}
