package com.votinginfoproject.VotingInformationProject.asynctasks;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.votinginfoproject.VotingInformationProject.models.VIPAppContext;

import java.io.IOException;
import java.util.List;

/**
 * Asynchronously query the geocoding service with a location (must not be done on the UI thread!)
 * Returns first address found to callback function passed to constructor.
 *
 * The callback returns the address, or null for no result.
 *
 * Created by kathrynkillebrew on 8/14/14.
 */
public class ReverseGeocodeQuery extends AsyncTask<Location, String, String> {

    Geocoder geocoder;
    ReverseGeocodeCallBackListener callBackListener;
    Location location;

    public interface ReverseGeocodeCallBackListener {
        /**
         * Callback for geocoder response.
         */
        public void callback(String address);
    }

    /**
     * Constructor.  Set parameters for geocoding job here.
     *
     * @param callBack function to be called with geocode results
     */
    public ReverseGeocodeQuery(ReverseGeocodeCallBackListener callBack) {
        super();
        this.geocoder = new Geocoder(VIPAppContext.getContext());
        this.callBackListener = callBack;
    }

    @Override
    protected String doInBackground(Location... locations) {

        location = locations[0];

        if (!Geocoder.isPresent()) {
            Log.e("GeocodeQuery", "No geocoder service available!");
            return "";
        }

        try {
            if (location == null) {
                Log.e("GeocodeQuery", "No location to geocode!");
                return "";
            }

            List<Address> results = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (results != null && !results.isEmpty()) {
                Address gotAddress = results.get(0);
                Log.d("ReverseGeocodeQuery:doInBackground", "Got address result " + gotAddress.toString());
                return  formatAddress(gotAddress);
            } else {
                Log.d("GeocodeQuery", "No result found for location");
            }
        } catch (IOException e) {
            Log.e("GeocodeQuery", "IOException reverse-geocoding location");
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Helper function to parse Address object into formatted String.
     *
     * @param address Address object returned by geocoder
     * @return String representation of address
     */
    private String formatAddress(Address address) {
        StringBuilder formattedAddress = new StringBuilder();
        int maxAddrLine = address.getMaxAddressLineIndex();
        for (int i = 0; i < maxAddrLine; i++) {
            formattedAddress.append(address.getAddressLine(i));
            formattedAddress.append(" ");
        }

        // strip trailing space
        formattedAddress.deleteCharAt(formattedAddress.length() -1);
        return formattedAddress.toString();
    }

    @Override
    protected void onPostExecute(String address) {
        if (callBackListener != null) {
            callBackListener.callback(address);
        } else {
            Log.e("ReverseGeocodeQuery:onPostExecute", "Callback listener is null!  Cannot return geocoded address.");
        }
    }
}
