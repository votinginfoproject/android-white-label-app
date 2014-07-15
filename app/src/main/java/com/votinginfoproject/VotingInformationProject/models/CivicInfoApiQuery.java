package com.votinginfoproject.VotingInformationProject.models;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.votinginfoproject.VotingInformationProject.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by kathrynkillebrew on 7/14/14.
 *
 * Generalized class for querying the API.  Instantiation parameters:
 * ctx -> application context
 * ret -> class type of object returned by query
 * cb -> callback function on view to handle receiving the return object
 *
 * Execution parameter:
 * url -> method/parameter portion of query url, ending with "key=" (API key will be added here)
 */
public class CivicInfoApiQuery<T> extends AsyncTask<String, Void, T> {

    public interface CallBackListener {
        public void callback(Object ob);
    }

    private Object returnObject;
    private CallBackListener callbackListener;
    private Class<T> returnClass;
    private String apiKey;
    private String baseQueryUrl;

    public CivicInfoApiQuery (Context context, Class clazz, CallBackListener callBack) {
        try {
            this.returnClass = clazz;
            this.callbackListener = callBack;
            this.apiKey = context.getString(R.string.google_api_browser_key);
            this.baseQueryUrl = context.getString(R.string.civic_info_api_url);

            if (this.apiKey.isEmpty() || this.baseQueryUrl.isEmpty()) {
                throw new CivicInfoApiQueryException("Civic Info API key or base query URL not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected T doInBackground(String... urls) {
        URL url = null;
        returnObject = null;
        try {
            if (!urls[0].endsWith("key=")) {
                throw new CivicInfoApiQueryException("Query URL should end with 'key='");
            }
            url = new URL(this.baseQueryUrl + urls[0] + this.apiKey);
            HttpURLConnection myConnection = (HttpURLConnection) url.openConnection();
            InputStream in = myConnection.getInputStream();
            BufferedReader ir = new BufferedReader(new InputStreamReader(in));
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(ir, returnClass);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(T thing) {
        callbackListener.callback(thing);
    }
}
