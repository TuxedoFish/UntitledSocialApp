package co.nf.tuxedofish.socialapp.frontend.hubfragments;

import android.app.ActionBar;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;

import co.nf.tuxedofish.socialapp.R;
import co.nf.tuxedofish.socialapp.utils.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class ConnectionFragment extends Fragment {
    private static final String ARG_MY_USER = "YEKDIRESU-20181114";

    private User mUser;
    private int matchesLoaded = 0;

    private LinearLayout images, names;
    private float scale;
    private int secondaryColor;

    private FirebaseFirestore db;

    private Context mContext;

    private OnFragmentInteractionListener mListener;

    public ConnectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    //Stores the user of the apps current data
    public static ConnectionFragment newInstance(User mUser) {
        ConnectionFragment fragment = new ConnectionFragment();
        Bundle args = new Bundle();

        args.putSerializable(ARG_MY_USER, mUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.mUser = (User) getArguments().getSerializable(ARG_MY_USER);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connection, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        images = view.findViewById(R.id.imagesLayout);
        names = view.findViewById(R.id.namesLayout);

        scale = getContext().getResources().getDisplayMetrics().density;
        secondaryColor = getContext().getResources().getColor(R.color.colorSecondary);

        mContext = getActivity();

        //update the UI for the first time
        updateUI();

        //Update the UI upon any change in group
        final Handler handler;
        handler = new Handler();
        final int delay = 2000; //milliseconds

        handler.postDelayed(new Runnable() {
            public void run() {
                handler.postDelayed(this, delay);

                mUser.update(db);
                if(mUser.getMatches().size() > matchesLoaded) {
                    updateUI();
                }
            }
        }, delay);

//        BlurLayout sampleLayout = (BlurLayout)(getActivity().findViewById(R.id.user_1_blur));
//        View hover = LayoutInflater.from(getContext()).inflate(R.layout.hover_profile, null);
//
//        if(getArguments()!=null) {
//            TextView user_1_name = hover.findViewById(R.id.user_1_name);
//            user_1_name.setText(getArguments().getStringArray(ARG_MATCH_NAMES)[0]);
//        }
//
//        sampleLayout.setHoverView(hover);
    }

    public void updateUI() {
        //First clear all the children
        images.removeAllViews();
        names.removeAllViews();

        //Now we want to add in all of the matches that we have
        ArrayList<User> matches = mUser.getMatches();

        for(int i=0; i<matches.size(); i++) {
            //Define the picture to be added
            CircleImageView picture = new CircleImageView(mContext);
            picture.setBorderColor(secondaryColor);
            picture.setBorderWidth(5);
            LinearLayout.LayoutParams layoutParamsPic = new LinearLayout.LayoutParams((int)(82*scale), (int)(75*scale));
            picture.setBackgroundResource(R.drawable.ic_profile_icon);
            picture.setLayoutParams(layoutParamsPic);

            //Define the name to be added
            TextView name = new TextView(mContext);
            LinearLayout.LayoutParams layoutParamsName = new LinearLayout.LayoutParams((int)(82*scale), ViewGroup.LayoutParams.WRAP_CONTENT);
            name.setLayoutParams(layoutParamsName);
            name.setText(matches.get(i).getFirstName());
            name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            name.setTextColor(secondaryColor);
            name.setTextSize(18);

            //Add the picture
            images.addView(picture);
            //Add the name
            names.addView(name);
        }
    }
}
