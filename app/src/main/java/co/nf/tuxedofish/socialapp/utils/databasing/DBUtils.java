package co.nf.tuxedofish.socialapp.utils.databasing;

import com.google.firebase.firestore.GeoPoint;

import co.nf.tuxedofish.socialapp.utils.Constants;

public class DBUtils {
    /*
    Returns the bounds of a location based query based on a singular point and constants
    defined in the Constants Class
     */
    public static GeoPoint[] getLocationBounds(double latitude, double longitude) {
        //all in miles
        double degree_lat = 69;
        double degree_long = Math.cos(Math.toRadians(latitude)) * 69.172;

        //bounds of query
        double lower_long = longitude - (Constants.QUERY_RADIUS/degree_long);
        double upper_long = longitude + (Constants.QUERY_RADIUS/degree_long);

        double lower_lat = latitude - (Constants.QUERY_RADIUS/degree_lat);
        double upper_lat = latitude + (Constants.QUERY_RADIUS/degree_lat);

        GeoPoint lower_point = new GeoPoint(lower_lat, lower_long);
        GeoPoint upper_point = new GeoPoint(upper_lat, upper_long);

        return new GeoPoint[]{lower_point, upper_point};
    }
}
