package com.votinginfoproject.VotingInformationProject.models.api.interactors;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.votinginfoproject.VotingInformationProject.models.CivicApiAddress;
import com.votinginfoproject.VotingInformationProject.models.ElectionAdministrationBody;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Location;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;
import com.votinginfoproject.VotingInformationProject.models.VoterInfoResponse;
import com.votinginfoproject.VotingInformationProject.models.api.requests.GeocodeRequest;
import com.votinginfoproject.VotingInformationProject.models.api.requests.GeocodeVoterInfoRequest;
import com.votinginfoproject.VotingInformationProject.models.api.requests.RequestType;
import com.votinginfoproject.VotingInformationProject.models.geocode.GeocodeLocationResult;
import com.votinginfoproject.VotingInformationProject.models.geocode.Result;
import com.votinginfoproject.VotingInformationProject.models.singletons.VoterInformation;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by marcvandehey on 4/15/16.
 */
public class GeocodeInteractor extends BaseInteractor<GeocodeVoterInfoRequest, GeocodeInteractor.GeocodeCallback> {
    private static final String TAG = GeocodeInteractor.class.getSimpleName();
    private final static float MILES_IN_METER = 0.000621371192f;
    private final static float KILOMETERS_IN_METER = 0.001f;

    @Override
    protected GeocodeVoterInfoRequest doInBackground(RequestType... params) {
        GeocodeVoterInfoRequest geocodeVoterInfoRequest = null;
        if (params.length > 0 && params[0] instanceof GeocodeVoterInfoRequest) {
            geocodeVoterInfoRequest = (GeocodeVoterInfoRequest) params[0];

            VoterInfoResponse voterInfoResponse = geocodeVoterInfoRequest.getVoterInfoResponse();

            OkHttpClient client = new OkHttpClient();
            Gson gson = new GsonBuilder().create();

            GeocodeRequest homeGeocodeRequest = new GeocodeRequest(geocodeVoterInfoRequest.getGeocodeKey(), voterInfoResponse.normalizedInput.toGeocodeString());

            Request homeAddressRequest = new Request.Builder().url(homeGeocodeRequest.buildQueryString()).build();
            GeocodeLocationResult homeAddressResponse;

            try {
                Response okHttpResponse = client.newCall(homeAddressRequest).execute();
                homeAddressResponse = gson.fromJson(okHttpResponse.body().string(), GeocodeLocationResult.class);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Unexpected error in Geocoding Home Location");
                homeAddressResponse = new GeocodeLocationResult();
            }

            Location homeLocation = null;

            if (!homeAddressResponse.getResults().isEmpty()) {
                Result homeGeocode = homeAddressResponse.getResults().get(0);

                homeLocation = homeGeocode.getGeometry().getLocation();
            }

            if (homeLocation == null) {
                //Error occurred with getting home location, cannot proceed
                return geocodeVoterInfoRequest;
            }

            //TODO pass use metric in correctly
            ArrayList<PollingLocation> geocodedPollingLocations = getGeocodedLocationForList(client, gson, geocodeVoterInfoRequest.getGeocodeKey(), voterInfoResponse.getPollingLocations(), homeLocation, VoterInformation.useMetric());
            ArrayList<PollingLocation> geocodedEarlyVotingLocations = getGeocodedLocationForList(client, gson, geocodeVoterInfoRequest.getGeocodeKey(), voterInfoResponse.getOpenEarlyVoteSites(), homeLocation, VoterInformation.useMetric());
            ArrayList<PollingLocation> geocodedDropBoxLocations = getGeocodedLocationForList(client, gson, geocodeVoterInfoRequest.getGeocodeKey(), voterInfoResponse.getOpenDropOffLocations(), homeLocation, VoterInformation.useMetric());

            CivicApiAddress stateAdminAddress = voterInfoResponse.getAdminAddress(ElectionAdministrationBody.AdminBody.STATE);

            if (stateAdminAddress != null) {
                Location stateAdminLocation = getGeocodedLocation(client, gson, geocodeVoterInfoRequest.getGeocodeKey(), stateAdminAddress);
                float stateAdminDistance = getDistance(homeLocation, stateAdminLocation, VoterInformation.useMetric());

                stateAdminAddress.latitude = stateAdminLocation.lat;
                stateAdminAddress.longitude = stateAdminLocation.lng;
                stateAdminAddress.distance = stateAdminDistance;
            }

            //Set up Local Administration Body
            ElectionAdministrationBody localAdministrationBody = voterInfoResponse.getLocalAdmin();

            if (localAdministrationBody != null) {
                CivicApiAddress localPhysicalAddress = getLocationForApiAddress(localAdministrationBody.getPhysicalAddress(), client, gson, geocodeVoterInfoRequest.getGeocodeKey(), homeLocation);
                CivicApiAddress localCorrespondenceAddress = getLocationForApiAddress(localAdministrationBody.getCorrespondenceAddress(), client, gson, geocodeVoterInfoRequest.getGeocodeKey(), homeLocation);

                localAdministrationBody.setPhysicalAddress(localPhysicalAddress);
                localAdministrationBody.setCorrespondenceAddress(localCorrespondenceAddress);

                VoterInformation.setLocalAdministrationBody(localAdministrationBody);
            }

            //Setup State Administration Body
            ElectionAdministrationBody stateAdministrationBody = voterInfoResponse.getStateAdmin();

            if (stateAdministrationBody != null) {
                CivicApiAddress statePhysicalAddress = getLocationForApiAddress(stateAdministrationBody.getPhysicalAddress(), client, gson, geocodeVoterInfoRequest.getGeocodeKey(), homeLocation);
                CivicApiAddress stateCorrespondenceAddress = getLocationForApiAddress(stateAdministrationBody.getCorrespondenceAddress(), client, gson, geocodeVoterInfoRequest.getGeocodeKey(), homeLocation);

                stateAdministrationBody.setPhysicalAddress(statePhysicalAddress);
                stateAdministrationBody.setCorrespondenceAddress(stateCorrespondenceAddress);

                VoterInformation.setStateAdministrationBody(stateAdministrationBody);
            }

            //Setup Home address
            CivicApiAddress homeAddress = voterInfoResponse.normalizedInput;
            homeAddress.latitude = homeLocation.lat;
            homeAddress.longitude = homeLocation.lng;

            VoterInformation.setHomeAddress(homeAddress);

            //Polling Locations
            VoterInformation.setPollingLocations(geocodedPollingLocations, geocodedEarlyVotingLocations, geocodedDropBoxLocations);
        }

        return geocodeVoterInfoRequest;
    }

    /**
     * Helper Function to Geocode a Civic Api Address and update its latitude, longitude and distance from Home
     *
     * @param civicApiAddress
     * @param client
     * @param gson
     * @param key
     * @param homeLocation
     * @return
     */
    private CivicApiAddress getLocationForApiAddress(CivicApiAddress civicApiAddress, OkHttpClient client, Gson gson, String key, Location homeLocation) {
        if (civicApiAddress != null) {
            Location location = getGeocodedLocation(client, gson, key, civicApiAddress);

            float localAdminDistance = getDistance(homeLocation, location, VoterInformation.useMetric());

            civicApiAddress.latitude = location.lat;
            civicApiAddress.longitude = location.lng;
            civicApiAddress.distance = localAdminDistance;
        }

        return civicApiAddress;
    }

    private ArrayList<PollingLocation> getGeocodedLocationForList(OkHttpClient client, Gson gson, String geocodeKey, ArrayList<PollingLocation> locations, Location homeLocation, boolean useMetric) {
        android.location.Location home = new android.location.Location("home");
        home.setLatitude(homeLocation.lat);
        home.setLongitude(homeLocation.lng);
        ArrayList<PollingLocation> geocodedPollingLocations = new ArrayList<>();

        //Loop through list and geocode address
        for (PollingLocation pollingLocation : locations) {
            Location foundLocation = getGeocodedLocation(client, gson, geocodeKey, pollingLocation.address);
            float distance = getDistance(homeLocation, foundLocation, useMetric);

            pollingLocation.location = foundLocation;
            pollingLocation.distance = distance;

            //Add locations even if there is a geocode failure
            geocodedPollingLocations.add(pollingLocation);
        }

        return geocodedPollingLocations;
    }

    private Location getGeocodedLocation(OkHttpClient client, Gson gson, String geocodeKey, CivicApiAddress address) {
        //Setup temp home location to calculate distance
        Location location = new Location();

        if (address != null) {
            GeocodeRequest pollingLocationGeocodeRequest = new GeocodeRequest(geocodeKey, address.toGeocodeString());

            Request pollingAddressRequest = new Request.Builder().url(pollingLocationGeocodeRequest.buildQueryString()).build();
            GeocodeLocationResult pollingAddressResult = null;

            try {
                Response okHttpResponse = client.newCall(pollingAddressRequest).execute();
                pollingAddressResult = gson.fromJson(okHttpResponse.body().string(), GeocodeLocationResult.class);

                if (pollingAddressResult.getResults() != null && !pollingAddressResult.getResults().isEmpty()) {
                    location = pollingAddressResult.getResults().get(0).getGeometry().getLocation();

                    Log.v(TAG, "Success! - " + address.locationName);
                } else {
                    Log.e(TAG, "No result Found in polling location: " + address.locationName);
                }
            } catch (IOException e) {
                e.printStackTrace();

                Log.e(TAG, "Unexpected error in geocoding polling location: " + address.locationName);
            }
        }

        return location;
    }

    private float getDistance(Location homeLocation, Location otherLocation, boolean useMetric) {
        //Create a temp Android Location to calculate distance
        android.location.Location tempLocation = new android.location.Location("polling");
        tempLocation.setLatitude(otherLocation.lat);
        tempLocation.setLongitude(otherLocation.lng);

        android.location.Location home = new android.location.Location("home");
        home.setLatitude(homeLocation.lat);
        home.setLongitude(homeLocation.lng);

        if (homeLocation.lat == 0 && otherLocation.lat == 0) {
            //Error occurred with location. Set as -1
            return -1;
        } else if (useMetric) {
            // convert meters to kilometers
            return home.distanceTo(tempLocation) * KILOMETERS_IN_METER;
        } else {
            // convert result from meters to miles
            return home.distanceTo(tempLocation) * MILES_IN_METER;
        }
    }

    @Override
    protected void onPostExecute(GeocodeVoterInfoRequest geocodeVoterInfoRequest) {
        super.onPostExecute(geocodeVoterInfoRequest);

        if (getCallback() != null) {
            getCallback().onGeocodeResults(geocodeVoterInfoRequest.getVoterInfoResponse());
        }
    }

    public interface GeocodeCallback {
        void onGeocodeResults(VoterInfoResponse voterInfoResponse);
    }
}
