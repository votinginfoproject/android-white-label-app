package com.votinginfoproject.VotingInformationProject.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.CivicApiError;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by kathrynkillebrew on 7/14/14.
 *
 * Generalized class for querying the API
 */
@SuppressWarnings("unchecked")
public class CivicInfoApiQuery<T> extends AsyncTask<String, CivicApiError, T> {

    public interface CallBackListener {
        public void callback(Object ob);
    }

    CallBackListener callbackListener;
    CallBackListener errorCallbackListener;
    Class<T> returnClass;
    HttpContext httpContext;
    HttpClient httpClient;

    SharedPreferences preferences;
    String last_election_key;

    /**
     * Constructor
     *
     * @param clazz Class of object expected to be returned by query for gson to parse into
     * @param callBack Callback method that takes returned object of type clazz
     * @param errorCallback Callback method that takes returned CivicApiError object
     * @param preferences SharedPreferences for calling activity for saving response string
     * @param lastElectionKey key for SharedPreferences on which to save response string
     */
    public CivicInfoApiQuery (Class clazz, CallBackListener callBack, CallBackListener errorCallback,
                              SharedPreferences preferences, String lastElectionKey) {
        try {
            this.returnClass = clazz;
            this.callbackListener = callBack;
            this.errorCallbackListener = errorCallback;
            this.preferences = preferences;
            this.last_election_key = lastElectionKey;

            this.httpContext = new BasicHttpContext();
            this.httpClient = new DefaultHttpClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param urls  query url
     * @return returns object of type of the class passed into the constructor
     */
    @Override
    protected T doInBackground(String... urls) {
        String url = urls[0];
        InputStream inputStream = null;
        HttpGet httpGet = null;

        Log.d("CivicInfoApiQuery", "Url: " + url);

        try {
            httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet, httpContext);
            int status = response.getStatusLine().getStatusCode();
            inputStream = response.getEntity().getContent();
            BufferedReader ir = new BufferedReader(new InputStreamReader(inputStream));

            Log.d("CivicInfoApiQuery", "GOT RESPONSE STATUS: " + status);

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = ir.readLine()) != null) {
                stringBuilder.append(line);
            }
            ir.close();
            inputStream.close();
            String responseStr = stringBuilder.toString();

            Gson gson = new GsonBuilder().create();
            if (status == HttpStatus.SC_OK) {
                T gsonObj = gson.fromJson(responseStr, returnClass);
                // store response to SharedPreferences
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(last_election_key, responseStr);
                editor.apply();
                return gsonObj;
            } else {
                // error
                CivicApiErrorResponse myError = gson.fromJson(responseStr, CivicApiErrorResponse.class);
                publishProgress(myError.error);
            }
        } catch (IOException e) {
            e.printStackTrace();
            clearLastElection();
        } catch (JsonSyntaxException e) {
            Log.e("CivicInfoApiQuery", "Could not parse JSON response!");
            e.printStackTrace();
            clearLastElection();
        } catch (Exception e) {
            e.printStackTrace();
            clearLastElection();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    Log.e("CivicInfoApiQuery", "Error closing input stream");
                    ex.printStackTrace();
                }
            }

            if (httpGet != null) {
                try {
                    httpGet.abort();
                } catch (Exception ex) {
                    Log.e("CivicInfoApiQuery", "Error aborting HTTP Get");
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     *
     * @param thing Object of type of class passed to constructor; returned to callback method
     */
    protected void onPostExecute(T thing) {
        callbackListener.callback(thing);
    }

    /**
     * When an error response is received, invoke error handler polylineCallback with error object,
     * and return null for expected response object in onPostExecute.
     * (There are no true progress updates.  This is being done to return the error object.)
     *
     * @param errors Contains error object with descriptive message from API
     */
    @Override
    protected void onProgressUpdate(CivicApiError... errors) {
        Log.d("CivicInfoApiQuery", "GOT API ERROR RESPONSE:");
        Log.d("CivicInfoApiQuery", errors[0].code + ": " + errors[0].message);

        clearLastElection();
        errorCallbackListener.callback(errors[0]);
    }

    /**
     * Clear last election from shared preferences on error.
     */
    private void clearLastElection() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(last_election_key, "");
        editor.apply();
    }
}
