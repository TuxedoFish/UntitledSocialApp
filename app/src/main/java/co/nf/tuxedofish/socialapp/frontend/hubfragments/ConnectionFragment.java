package co.nf.tuxedofish.socialapp.frontend.hubfragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import co.nf.tuxedofish.socialapp.R;
import co.nf.tuxedofish.socialapp.utils.User;

public class ConnectionFragment extends Fragment {
    private static final String ARG_MATCH_NAMES = "user_match_names_key";
    private String[] matchNames;

    private OnFragmentInteractionListener mListener;

    public ConnectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param matches user matches.
     * @return A new instance of fragment ConnectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConnectionFragment newInstance(ArrayList<User> matches) {
        ConnectionFragment fragment = new ConnectionFragment();
        Bundle args = new Bundle();
        //Fill a string array with the names
        ArrayList<String> matchNames = new ArrayList<>();
        for(int i=0; i<matches.size(); i++) { matchNames.add(matches.get(i).getFirstName()); }

        args.putStringArray(ARG_MATCH_NAMES, matchNames.toArray(new String[0]));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.matchNames = getArguments().getStringArray(ARG_MATCH_NAMES);
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

        Log.d("info", "The name of one of the matches is : " + matchNames[0]);

        TextView userName0 = view.findViewById(R.id.userName0);
        TextView userName1 = view.findViewById(R.id.userName1);
        TextView userName2 = view.findViewById(R.id.userName2);
        TextView userName3 = view.findViewById(R.id.userName3);

        userName0.setText(matchNames[0]); userName1.setText(matchNames[1]);
        userName2.setText(matchNames[2]); userName3.setText(matchNames[3]);

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
}
