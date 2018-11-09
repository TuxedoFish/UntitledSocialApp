package co.nf.tuxedofish.socialapp.utils;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import co.nf.tuxedofish.socialapp.R;
import co.nf.tuxedofish.socialapp.utils.databasing.DBUtils;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Utilities {
    /*
    Checks if the current phone has an image capturing bug which will prevent
    data being returned from camera
     */
    public boolean hasImageCaptureBug() {
        // list of known devices that have the bug
        ArrayList<String> devices = new ArrayList<String>();
        devices.add("android-devphone1/dream_devphone/dream");
        devices.add("generic/sdk/generic");
        devices.add("vodafone/vfpioneer/sapphire");
        devices.add("tmobile/kila/dream");
        devices.add("verizon/voles/sholes");
        devices.add("google_ion/google_ion/sapphire");

        return devices.contains(android.os.Build.BRAND + "/" + android.os.Build.PRODUCT + "/"
                + android.os.Build.DEVICE);
    }

    /*
    Creates a new URI for saving the image to the phone
     */
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        SimpleDateFormat m_sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK);
        String m_currentDateAndTime = m_sdf.format(new Date());

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, m_currentDateAndTime,
                "profile picture");

        return Uri.parse(path);
    }

    /*
    Creates the marker for use on a google map which is a combination of the
    marker and the icon overlaid on top
     */
    public BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        double scaling_factor = 0.8f;
        //Setup background of the marker
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_marker_background_48dp);
        background.setBounds(0, 0, (int)(background.getIntrinsicWidth()*scaling_factor),(int)(background.getIntrinsicHeight()*scaling_factor));
        //Draw the icon of the day
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds((int)(30*scaling_factor),  (int)(20*scaling_factor),
                (int)((vectorDrawable.getIntrinsicWidth() + 30)*scaling_factor), (int)((vectorDrawable.getIntrinsicHeight() + 20)*scaling_factor));
        //Create a new bitmap to draw onto
        Bitmap bitmap = Bitmap.createBitmap((int)(scaling_factor*background.getIntrinsicWidth()),
                (int)(scaling_factor*background.getIntrinsicHeight()), Bitmap.Config.ARGB_8888);
        //Create a canvas to enable us to draw onto
        Canvas canvas = new Canvas(bitmap);
        //Draw the 2 images ontop of eachother
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        //Return the output
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }



    public static void closeFragment(int id, FragmentManager fragmentManager) {
        if(fragmentManager.findFragmentById(id) != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.remove(fragmentManager.findFragmentById(id));
            transaction.commit();
        }
    }

    public static void openFragment(Fragment fragment, int id, FragmentManager fragmentManager) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(id, fragment);
        transaction.disallowAddToBackStack();
        transaction.commit();
    }
}
