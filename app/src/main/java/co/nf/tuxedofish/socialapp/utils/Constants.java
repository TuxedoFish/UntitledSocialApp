package co.nf.tuxedofish.socialapp.utils;

public final class Constants {
    //DEBUG CONSTANTS PLEASE REMOVE
    public static final boolean debugging = true;
    public static final int NUMBER_FAKE_USERS = 4;
    //IN MILES
    //IN FINAL VERSION SHOULD BE EDITABLE
    public static final double QUERY_RADIUS = 0.5;

    //permission codes
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1000;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 3;

    //activity results
    public static final int RESULT_LOAD_IMAGE_TAKEN = 1;
    public static final int RESULT_LOAD_IMAGE_GALLERY = 2;

    public static final int STATUS_ONLINE = 1;
    public static final int STATUS_SEARCHING = 2;
    public static final int STATUS_PENDING = 3;
    public static final int STATUS_MATCHED = 4;
    public static final int STATUS_ERROR = 5;
    public static final int STATUS_OFFLINE = 6;

    public static final String REQUEST_NULL = "null";
    public static final String REQUEST_FAILED = "fail";

    //Defining the max group size for later logic
    public static int MAX_GROUP_SIZE = 5;
    public static int MAX_ACCEPTED = MAX_GROUP_SIZE - 1;

    public static final int NO_REPLY = 10;
    public static final int POSITIVE_REPLY = 11;
    public static final int NEGATIVE_REPLY = 12;

    public static final int REQUEST_DECISION_FROM_USER = 13;

    //gender for use in user class
    public enum Gender {
        MALE,
        FEMALE,
        UNKNOWN
    }
}

