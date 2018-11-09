package co.nf.tuxedofish.socialapp.frontend.registration;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import co.nf.tuxedofish.socialapp.R;

public class PasswordFragment extends Fragment {
    private static final String ARG_EMAIL = "email";
    private static final String ARG_NAME = "name";

    private String mEmail;
    private String mName;

    private PasswordCheck mListener;

    private EditText mEmailTextView;
    private TextView mWelcomeText;
    private Button mNextStage;
    private EditText mPassword, mConfirmPassword;

    public PasswordFragment() {
        // Required empty public constructor
    }

    public static PasswordFragment newInstance(String email, String name) {
        PasswordFragment fragment = new PasswordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, email);
        args.putString(ARG_NAME, name);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEmail = getArguments().getString(ARG_EMAIL);
            mName = getArguments().getString(ARG_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_password, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PasswordCheck) {
            mListener = (PasswordCheck) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface PasswordCheck{
        void onPasswordSet(String password);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEmailTextView = view.findViewById(R.id.email_preset);
        mEmailTextView.setText(mEmail);
        mEmailTextView.setEnabled(false);

        String mWelcome = "Hi, " + mName.split(" ")[0] + "!";

        mWelcomeText = view.findViewById(R.id.password_welcome_text);
        mWelcomeText.setText(mWelcome);

        mPassword = view.findViewById(R.id.editPassword);
        mConfirmPassword = view.findViewById(R.id.editConfirmPassword);

        mNextStage = view.findViewById(R.id.next_password);
        mNextStage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkData(mConfirmPassword.getText().toString(), mPassword.getText().toString())) {
                    mListener.onPasswordSet(mPassword.getText().toString());
                }
            }
        });
    }

    public static void alert(String message, Context context) {
        Log.w("failed", "register : failure, \n" + message);
        Toast.makeText(context, "Registration failed, " + message,
                Toast.LENGTH_SHORT).show();
    }

    public boolean checkData(String confirmPassword, String password) {
        if(password.equals("")) {
            //Please fill in password
            //NEED A STRENGTH CHECKER
            alert("please fill in a password", getContext());
            return false;
        } else if (confirmPassword.equals("") || (!password.equals(confirmPassword))) {
            //Passwords do not match
            alert("passwords do not match", getContext());
            return false;
        }

        return true;
    }
}
