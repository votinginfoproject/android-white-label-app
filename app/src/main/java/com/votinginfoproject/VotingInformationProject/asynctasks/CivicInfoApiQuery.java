package com.votinginfoproject.VotingInformationProject.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.CivicApiError;

import org.apache.http.HttpResponse;
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
public class CivicInfoApiQuery<T> extends AsyncTask<String, CivicApiError, T> {

    public interface CallBackListener {
        public void callback(Object ob);
    }

    private Object returnObject;
    private CallBackListener callbackListener;
    private CallBackListener errorCallbackListener;
    private Class<T> returnClass;
    private String apiKey;
    private String baseQueryUrl;
    private HttpContext httpContext;
    private HttpClient httpClient;

    /**
     * Constructor
     * @param context Application context
     * @param clazz Class of object expected to be returned by query for gson to parse into
     * @param callBack Callback method that takes returned object of type clazz
     * @param errorCallback Callback method that takes returned CivicApiError object
     */
    public CivicInfoApiQuery (Context context, Class clazz, CallBackListener callBack, CallBackListener errorCallback) {
        try {
            this.returnClass = clazz;
            this.callbackListener = callBack;
            this.errorCallbackListener = errorCallback;
            this.apiKey = context.getString(R.string.google_api_browser_key);
            this.baseQueryUrl = context.getString(R.string.civic_info_api_url);

            if (this.apiKey.isEmpty() || this.baseQueryUrl.isEmpty()) {
                throw new CivicInfoApiQueryException("Civic Info API key or base query URL not found!");
            }

            this.httpContext = new BasicHttpContext();
            this.httpClient = new DefaultHttpClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param urls  method/parameter portion of query url, ending with "key=" (API key will be added here)
     * @return returns object of type of the class passed into the constructor
     */
    @Override
    protected T doInBackground(String... urls) {
        String url = this.baseQueryUrl + urls[0] + this.apiKey;
        Log.d("CivicInfoApiQuery", "Url: " + url);

        returnObject = null;
        try {
            if (!urls[0].endsWith("key=")) {
                throw new CivicInfoApiQueryException("Query URL should end with 'key='");
            }

            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet, httpContext);
            int status = response.getStatusLine().getStatusCode();
            InputStream in = response.getEntity().getContent();
            BufferedReader ir = new BufferedReader(new InputStreamReader(in));

            Log.d("CivicInfoApiQuery", "GOT RESPONSE STATUS: " + status);

            Gson gson = new GsonBuilder().create();
            if (status == 200) {
                return gson.fromJson(ir, returnClass);
            } else {
                // error
                CivicApiErrorResponse myError = gson.fromJson(ir, CivicApiErrorResponse.class);
                publishProgress(myError.error);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
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
     * When an error response is received, invoke error handler callback with error object,
     * and return null for expected response object in onPostExecute.
     * (There are no true progress updates.  This is being done to return the error object.)
     *
     * @param errors Contains error object with descriptive message from API
     */
    @Override
    protected void onProgressUpdate(CivicApiError... errors) {
        Log.d("CivicInfoApiQuery", "GOT API ERROR RESPONSE:");
        Log.d("CivicInfoApiQuery", errors[0].code + ": " + errors[0].message);

        errorCallbackListener.callback(errors[0]);
    }
}