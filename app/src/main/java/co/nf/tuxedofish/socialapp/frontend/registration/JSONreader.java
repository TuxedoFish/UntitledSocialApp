package co.nf.tuxedofish.socialapp.frontend.registration;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONreader extends AsyncTask<String, Integer, JSONObject> {
    private JSONreturner mReturnData;
    public interface JSONreturner {
        void onInfoRetrieved(JSONObject mData);
    }

    public JSONreader(JSONreturner returner) {
        this.mReturnData = returner;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    @Override
    protected JSONObject doInBackground(String... params){
        try {
            InputStream is = new URL(params[0]).openStream();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                JSONObject json = new JSONObject(jsonText);
                return json;
            } finally {
                is.close();
            }
        } catch (IOException exception) {
            Log.e("error", "IOException reading Json file : " + exception.getLocalizedMessage());
            return null;
        } catch (JSONException exception) {
            Log.e("error", "JSONexception reading Json file : " + exception.getLocalizedMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        Log.i("Authorize", "Loaded user data : " + result.toString());

        mReturnData.onInfoRetrieved(result);
    }
}
