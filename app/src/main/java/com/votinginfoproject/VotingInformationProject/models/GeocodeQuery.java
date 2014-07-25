package com.votinginfoproject.VotingInformationProject.models;

import android.content.Context;
import android.os.AsyncTask;
import android.location.Geocoder;
import android.location.Address;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Asynchronously query the geocoding service with an address (must not be done on the UI thread!)
 * Returns first co-ordinates found to callback function passed to constructor.
 *
 * The callback returns the ID passed in, and the co-ordinates.
 * It will return negative co-ordinates in case of error, or no results found.
 *
 * Created by kathrynkillebrew on 7/25/14.
 */
public class GeocodeQuery extends AsyncTask<String, String, HashMap<String, ArrayList<Double>>> {

    Geocoder geocoder;
    GeocodeCallBackListener callBackListener;
    String key;
    String geocodeAddress;


    public interface GeocodeCallBackListener {
        public void callback(String label, double latitude, double longitude);
    }

    /**
     *
     * @param context application context
     * @param callBack function to be called with geocode results
     * @param id string identifier for the address that will also be returned
     * @param address string containing the address to geocode
     */
    public GeocodeQuery(Context context, GeocodeCallBackListener callBack, String id, String address) {
        super();
        this.geocoder = new Geocoder(context);
        this.callBackListener = callBack;
        this.key = id;
        this.geocodeAddress = address;
    }

    @Override
    protected HashMap<String, ArrayList<Double>> doInBackground(String... addresses) {

        HashMap returnMap = new HashMap(1);
        ArrayList<Double> coords = new ArrayList<Double>(2);

        if (!geocoder.isPresent()) {
            Log.e("GeocodeQuery", "No geocoder service available!");
            coords.add(-2.0);
            coords.add(-2.0);
            returnMap.put("error", coords);
            return returnMap;
        }

        try {
            if (geocodeAddress == null || geocodeAddress.isEmpty()) {
                Log.e("GeocodeQuery", "No address to geocode!");
            }

            List<Address> results = geocoder.getFromLocationName(geocodeAddress, 1);
            if (results != null && !results.isEmpty()) {
                Address gotAddress = results.get(0);
                coords.add(gotAddress.getLatitude());
                coords.add(gotAddress.getLongitude());
                returnMap.put(key, coords);
            } else {
                Log.d("GeocodeQuery", "No result found for address " + geocodeAddress);
                coords.add(-1.0);
                coords.add(-1.0);
                returnMap.put("error", coords);
            }
        } catch (IOException e) {
            Log.e("GeocodeQuery", "IOException geocoding address " + geocodeAddress);
            e.printStackTrace();
            coords.add(-3.0);
            coords.add(-3.0);
            returnMap.put("error", coords);
        }

        return returnMap;
    }

    @Override
    protected void onPostExecute(HashMap<String, ArrayList<Double>> addressMap) {
        if (addressMap != null && !addressMap.isEmpty()) {
            String key = addressMap.keySet().iterator().next();
            ArrayList<Double> coords = addressMap.get(key);
            callBackListener.callback(key, coords.get(0), coords.get(1));
        } else {
            callBackListener.callback("error", -4, -4);
        }
    }
}
