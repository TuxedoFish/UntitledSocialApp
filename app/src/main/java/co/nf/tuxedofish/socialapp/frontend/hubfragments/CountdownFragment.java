package co.nf.tuxedofish.socialapp.frontend.hubfragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import co.nf.tuxedofish.socialapp.R;

public class CountdownFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    private FirebaseFirestore mDB;

    private TextView mCountdown;

    private Timestamp eventStart = null;

    private boolean countdownFinished = false;

    private Double longitude, latitude;

    public CountdownFragment() {

    }

    public void updateCountdown() {
        if(eventStart!=null) {
            if(eventStart.toDate().getTime() - Calendar.getInstance().getTime().getTime() <= 0) {
                if(!countdownFinished) {
                    onCountdownFinished();
                    countdownFinished=true;
                }
            } else {
                Long milliseconds = eventStart.toDate().getTime() - Calendar.getInstance().getTime().getTime();
                Date c = new Date(milliseconds);

                SimpleDateFormat df = new SimpleDateFormat("mm : ss");
                String formattedDate = Long.toString(Math.floorDiv(milliseconds, 60*60*1000)) + " : " + df.format(c);

                mCountdown.setText(formattedDate);
            }
        }
    }

    public void getEventTime() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yy");
        String formattedDate = df.format(c);

        DocumentReference docref = mDB.collection("MEETUP_OF_THE_DAY").document(formattedDate);
        docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()) {
                    Log.d("success", "succesfully loaded the meetup : " + task.getResult().exists());

                    GeoPoint meetupLocation = task.getResult().getGeoPoint("location");
                    longitude = meetupLocation.getLongitude();
                    latitude = meetupLocation.getLatitude();
                    eventStart = task.getResult().getTimestamp("start_time");
                    Log.d("info", "the date and time of the event is : " + eventStart);
                } else {
                    Log.d("info", "failed to load the current meetup : " + task.getException());
                    //Logic here to display a :( sorry no meetup today page
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_countdown, container, false);
    }

    public void onCountdownFinished() {
        if (mListener != null) {
            mListener.onCountdownFinished(longitude, latitude);
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

    @Override
    public void onActivityCreated(Bundle savedInstaneSettings) {
        super.onActivityCreated(savedInstaneSettings);
        mCountdown = (getActivity().findViewById(R.id.bar_countdown));

        mDB = FirebaseFirestore.getInstance();

        getEventTime();

        //Update countdown every second
        final Handler handler = new Handler();
        final int delay = 1000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                updateCountdown();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    public interface OnFragmentInteractionListener {
        void onCountdownFinished(double longitude, double latitude);
    }
}
