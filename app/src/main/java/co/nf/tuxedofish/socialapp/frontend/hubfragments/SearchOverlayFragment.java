package co.nf.tuxedofish.socialapp.frontend.hubfragments;

import android.app.Fragment;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import co.nf.tuxedofish.socialapp.R;

public class SearchOverlayFragment extends Fragment {
    public ImageButton mSearchButton;
    public RippleDrawable mRippleRegister;

    public clickListener mlistener;
    public interface clickListener {
        public void onSearch(View v);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_overlay, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mSearchButton = (ImageButton)view.findViewById(R.id.searchButton);
        mRippleRegister = (RippleDrawable)mSearchButton.getBackground();

        mSearchButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mRippleRegister.setHotspot(event.getX(), event.getY());
                searchForUsers();
                return false;
            }
        });
    }

    public void searchForUsers() {
        //Change the status of user

    }
}
