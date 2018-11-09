  package co.nf.tuxedofish.socialapp.frontend;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONException;
import org.json.JSONObject;

import co.nf.tuxedofish.socialapp.R;
import co.nf.tuxedofish.socialapp.frontend.registration.JSONreader;

  public class LogInActivity extends AppCompatActivity implements JSONreader.JSONreturner{

    private Button mRegister;
    private Button mUCLSignIn;
    private Button mLogIn;

    private RippleDrawable mRegisterRipple;
    private RippleDrawable mLogInRipple;

    private TextView mLogoText;

    private WebView mSignInOverlay;

    private CallbackManager mCallBackManager;

    private String USER_NAME = "null";
    private String mStateIdentifier;

    private FirebaseFirestore mDB;

    private static final String REDIRECT_URI = "https://pure-wildwood-78042.herokuapp.com/oauth/complete";
    private static final String AUTHORIZATION_URL = "https://pure-wildwood-78042.herokuapp.com/oauth/authorise";
    private static final String USER_ID_KEY = "id";
    private static final String USER_AUTH_TOKEN = "key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mRegister = findViewById(R.id.registerButton);
        mRegisterRipple = (RippleDrawable) mRegister.getBackground();
        mRegister.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mRegisterRipple.setHotspot(event.getX(), event.getY());
                return false;
            }
        });
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle mUserData = new Bundle();
                mUserData.putString("is_registration", "register");

                Intent toHub = new Intent(LogInActivity.this, HubActivity.class);
                toHub.putExtras(mUserData);

                startActivity(toHub);
            }
        });

        mLogIn = findViewById(R.id.logInButton);
        mLogInRipple = (RippleDrawable) mLogIn.getBackground();
        mLogIn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mLogInRipple.setHotspot(event.getX(), event.getY());
                return false;
            }
        });
        mLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle mUserData = new Bundle();
                mUserData.putString("is_registration", "log_in");

                Intent toHub = new Intent(LogInActivity.this, HubActivity.class);
                toHub.putExtras(mUserData);

                startActivity(toHub);
            }
        });

        mLogoText = (TextView)findViewById(R.id.logoText);
        mLogoText.bringToFront();

        mCallBackManager = CallbackManager.Factory.create();

        mSignInOverlay = findViewById(R.id.sign_in_pop_up);
        mSignInOverlay.setBackgroundColor(Color.TRANSPARENT);

        mUCLSignIn = findViewById(R.id.uclRegisterButton);
        mUCLSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UCLsignIn();
            }
        });

//        mLogIn = (LoginButton) findViewById(R.id.facebookButton);
//
//        String PUBLIC_PROFILE = "public_profile";
//
//        mLogIn.setReadPermissions(Arrays.asList(PUBLIC_PROFILE));
//        mLogIn.registerCallback(mCallBackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                GraphRequest request = GraphRequest.newMeRequest(
//                        loginResult.getAccessToken(),
//                        new GraphRequest.GraphJSONObjectCallback() {
//                            @Override
//                            public void onCompleted(
//                                    JSONObject object,
//                                    GraphResponse response) {
//                                // Application code
//                                final String NAME = "name";
//
//                                try {
//                                    USER_NAME = object.getString(NAME);
//                                } catch (JSONException e) {
//                                    Log.e("error", "JSON Exception loaing data : " + e.getLocalizedMessage());
//                                }
//
//                                if(USER_NAME != null) {
//                                    beginRegistration(USER_NAME);
//                                }
//                            }
//                        });
//                Bundle parameters = new Bundle();
//                parameters.putString("fields", "id,name,birthday, gender");
//                request.setParameters(parameters);
//                request.executeAsync();
//            }
//
//            @Override
//            public void onCancel() {
//                // App code
//            }
//
//            @Override
//            public void onError(FacebookException exception) {
//                // App code
//            }
//        });

        FirebaseApp.initializeApp(this);
        mDB = FirebaseFirestore.getInstance();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            //FOR TESTING LOG IN PROCESSES
            FirebaseAuth.getInstance().signOut();

//            Intent toHub = new Intent(LogInActivity.this, HubActivity.class);
//            startActivity(toHub);
        }
    }

    @Override
    public void onActivityResult(int requestcode, int resultcode, Intent data) {
        super.onActivityResult(requestcode, resultcode, data);
        mCallBackManager.onActivityResult(requestcode, resultcode, data);
    }

    @Override
    public void onInfoRetrieved(JSONObject mData) {
        //Called when the users data has been found from the restful call

        Bundle mUserData = new Bundle();
        try {
            mUserData.putString("full_name", mData.getString("full_name"));
            mUserData.putString("first_name", mData.getString("first_name"));
            mUserData.putString("email", mData.getString("email"));
            mUserData.putString("is_student", mData.getString("is_student"));
            mUserData.putString("department", mData.getString("department"));

            mUserData.putString("is_registration", "register_via_ucl");

            Intent toHub = new Intent(LogInActivity.this, HubActivity.class);
            toHub.putExtras(mUserData);

            startActivity(toHub);
        } catch(JSONException exception) {
            Log.e("error", "JSON exception whilst trying to send data to HubActivity : " + exception.getLocalizedMessage());
        }
    }

    public void UCLsignIn() {
        //Request focus for the webview
        mSignInOverlay.bringToFront();
        mSignInOverlay.requestFocus(View.FOCUS_DOWN);
        mSignInOverlay.getSettings().setJavaScriptEnabled(true);
        mSignInOverlay.getSettings().setDomStorageEnabled(true);
        mSignInOverlay.setBackgroundColor(Color.WHITE);

        //Set a custom web view client
        mSignInOverlay.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                //This method will be executed each time a page finished loading.
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String authorizationUrl) {
                //Default behaviour
                Log.i("Authorize", "Redirecting to: " + authorizationUrl);
                mSignInOverlay.loadUrl(authorizationUrl);

                //This method will be called when the Auth proccess redirect to our RedirectUri.
                //We will check the url looking for our RedirectUri.
                if(authorizationUrl.startsWith(REDIRECT_URI)){
                    Log.i("Authorize", "Authorization successful! : " + authorizationUrl);
                    Uri uri = Uri.parse(authorizationUrl);
                    //We take from the url the token for getting future information on the user
                    String user_id = uri.getQueryParameter(USER_ID_KEY);
                    String user_key = uri.getQueryParameter(USER_AUTH_TOKEN);
                    Log.i("Authorize", "Authorization sucessful! Key Registered : " + USER_ID_KEY);

                    String getUserDataURL = "https://pure-wildwood-78042.herokuapp.com/oauth/userdata/" + user_id + "/" + user_key;
                    handleData(getUserDataURL);
                }
                return true;
            }
        });

        Log.i("Authorize","Loading Auth Url: "+AUTHORIZATION_URL);
        //Load the authorization URL into the webView
        mSignInOverlay.loadUrl(AUTHORIZATION_URL);
    }

    public void handleData(String dataURL) {
        new JSONreader(this).execute(dataURL);
    }

    //JUST FOR NO SCANNER FOR NOW
    public String generateBarcode() {
        int number = 0;
        String barcode = "";

        for(int i=0; i<10; i++) {
            String toadd = Integer.toString((int)Math.round(Math.random()*9));

            barcode = barcode.concat(toadd);
        }

        return barcode;
    }

    public void addNameToDatabase(String name, String barcode) {
        Map<String, String> uploadData = new HashMap<>();

        uploadData.put("name", name);

        mDB.collection("users").document(barcode).set(uploadData);
    }
}
