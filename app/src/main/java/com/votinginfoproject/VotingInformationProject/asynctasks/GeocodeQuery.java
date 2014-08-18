package com.votinginfoproject.VotingInformationProject.asynctasks;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.location.Geocoder;
import android.location.Address;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    private final static double MILES_IN_METER = 0.000621371192;
    private final static double KILOMETERS_IN_METER = 0.001;

    Geocoder geocoder;
    GeocodeCallBackListener callBackListener;
    String key;
    String geocodeAddress;
    Location home;
    boolean useMetric;
    private final WeakReference<TextView> distanceView;
    DecimalFormat distanceFormat;


    public interface GeocodeCallBackListener {
        /**
         * Callback for geocoder response.
         *
         * @param label Identifier for address sent; same as ID sent into constructor.
         * @param latitude Coordinate found in geocoding respnse (negative value indicates error)
         * @param longitude Coordinate found in geocoding respnse (negative value indicates error)
         * @param distance Distance from given address to user-entered location (zero if this is user's address)
         */
        public void callback(String label, double latitude, double longitude, double distance);
    }

    /**
     * Constructor.  Set parameters for geocoding job here.
     *
     * @param context application context
     * @param callBack function to be called with geocode results
     * @param id string identifier for the address that will also be returned
     * @param address string containing the address to geocode
     * @param home Android Location object for user-entered address; used for distance calculation
     * @param useMetric boolean for units of measurement; false -> imperial units, true -> metric
     */
    public GeocodeQuery(Context context, GeocodeCallBackListener callBack, String id, String address,
                        Location home, boolean useMetric, TextView distanceView) {
        super();
        this.geocoder = new Geocoder(context);
        this.callBackListener = callBack;
        this.key = id;
        this.geocodeAddress = address;
        this.home = home;
        this.useMetric = useMetric;
        if (distanceView != null) {
            this.distanceView = new WeakReference(distanceView);
            distanceFormat =  new DecimalFormat("0.00 ");
        } else {
            this.distanceView = null;
        }
    }

    @Override
    protected HashMap<String, ArrayList<Double>> doInBackground(String... addresses) {

        HashMap<String, ArrayList<Double>> returnMap = new HashMap(1);
        ArrayList<Double> returnVals = new ArrayList<Double>(3);

        if (!Geocoder.isPresent()) {
            Log.e("GeocodeQuery", "No geocoder service available!");
            returnVals.add(-2.0);
            returnVals.add(-2.0);
            returnVals.add(0.0);
            returnMap.put("error", returnVals);
            return returnMap;
        }

        try {
            if (geocodeAddress == null || geocodeAddress.isEmpty()) {
                Log.e("GeocodeQuery", "No address to geocode!");
            }

            List<Address> results = geocoder.getFromLocationName(geocodeAddress, 1);
            if (results != null && !results.isEmpty()) {
                Address gotAddress = results.get(0);
                double lat = gotAddress.getLatitude();
                double lon = gotAddress.getLongitude();
                returnVals.add(lat);
                returnVals.add(lon);

                // calculate distance from this location to home location, if this isn't home
                if (!key.equals("home")) {
                    // distance calculation
                    Location pollingLocation = new Location("polling");
                    pollingLocation.setLatitude(lat);
                    pollingLocation.setLongitude(lon);

                    if (useMetric) {
                        // convert meters to kilometers
                        returnVals.add(pollingLocation.distanceTo(home) * KILOMETERS_IN_METER);
                    } else {
                        // convert result from meters to miles
                        returnVals.add(pollingLocation.distanceTo(home) * MILES_IN_METER);
                    }
                } else {
                    returnVals.add(0.0);
                }
                returnMap.put(key, returnVals);
            } else {
                Log.d("GeocodeQuery", "No result found for address " + geocodeAddress);
                returnVals.add(-1.0);
                returnVals.add(-1.0);
                returnVals.add(0.0);
                returnMap.put("error", returnVals);
            }
        } catch (IOException e) {
            Log.e("GeocodeQuery", "IOException geocoding address " + geocodeAddress);
            e.printStackTrace();
            returnVals.add(-3.0);
            returnVals.add(-3.0);
            returnVals.add(0.0);
            returnMap.put("error", returnVals);
        }

        return returnMap;
    }

    @Override
    protected void onPostExecute(HashMap<String, ArrayList<Double>> addressMap) {
        if (addressMap != null && !addressMap.isEmpty()) {
            String key = addressMap.keySet().iterator().next();
            ArrayList<Double> returnedVals = addressMap.get(key);

            // set distance text view, if given one
            double distance = returnedVals.get(2);
            try {
                if (distanceView != null && distance > 0) {
                    TextView view = distanceView.get();
                    Log.d("GeocodeQuery:onPostExecute", "Setting distance TextView");
                    if (useMetric) {
                        view.setText(distanceFormat.format(distance) + " km");
                    } else {
                        view.setText(distanceFormat.format(distance) + " mi.");
                    }
                }
            } catch (Exception e) {
                Log.e("GeocodeQuery:onPostExecute", "Failed to set distance label");
                e.printStackTrace();
            }

            if (callBackListener != null) {
                callBackListener.callback(key, returnedVals.get(0), returnedVals.get(1), distance);
            }
        } else {
            if (callBackListener != null) {
                callBackListener.callback("error", -4, -4, 0);
            }
        }
    }
}
