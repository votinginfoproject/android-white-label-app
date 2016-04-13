package com.votinginfoproject.VotingInformationProject.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.votinginfoproject.VotingInformationProject.R;

/**
 * Created by kathrynkillebrew on 7/14/14.
 * Polling location or early voting site
 * https://developers.google.com/civic-information/docs/v1/voterinfo
 */
public class PollingLocation implements Parcelable {
    /**
     * Static field used to regenerate object, individually or as arrays
     */
    public static final Parcelable.Creator<PollingLocation> CREATOR = new Parcelable.Creator<PollingLocation>() {
        public PollingLocation createFromParcel(Parcel pc) {
            return new PollingLocation(pc);
        }

        public PollingLocation[] newArray(int size) {
            return new PollingLocation[size];
        }
    };

    public final static int POLLING_TYPE_LOCATION = 0x0;
    public final static int POLLING_TYPE_EARLY_VOTE = 0x1;
    public final static int POLLING_TYPE_DROP_BOX = 0x2;

    public CivicApiAddress address;
    public String id;
    public String name;
    public String startDate;
    public String endDate;
    public String pollingHours;
    public String voterServices; // This field is not populated for polling locations.

    public int pollingLocationType = POLLING_TYPE_LOCATION;

    /**
     * Creator from Parcel, reads back fields IN THE ORDER they were written
     */
    public PollingLocation(Parcel parcel) {
        address = parcel.readParcelable(CivicApiAddress.class.getClassLoader());
        id = parcel.readString();
        name = parcel.readString();
        startDate = parcel.readString();
        endDate = parcel.readString();
        pollingHours = parcel.readString();
        voterServices = parcel.readString();
        pollingLocationType = parcel.readInt();
    }

    /**
     * Set the polling location type based on where the information was grabbed from
     * Will default to polling location if a random value is entered
     *
     * @param pollingLocationType
     */
    public void setPollingLocationType(int pollingLocationType) {
        this.pollingLocationType = pollingLocationType;
    }

    public
    @DrawableRes
    int getDrawableDot() {
        switch (pollingLocationType) {
            case POLLING_TYPE_DROP_BOX:
                return R.drawable.ic_dot_drop_box;
            case POLLING_TYPE_EARLY_VOTE:
                return R.drawable.ic_dot_early_vote;
            default:
                return R.drawable.ic_dot_polling_location;
        }
    }

    public
    @StringRes
    int getPollingTypeString() {
        switch (pollingLocationType) {
            case POLLING_TYPE_DROP_BOX:
                return R.string.locations_list_label_drop_off;
            case POLLING_TYPE_EARLY_VOTE:
                return R.string.locations_list_label_early_voting;
            default:
                return R.string.locations_list_label_polling_sites;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(address, 0);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeString(pollingHours);
        dest.writeString(voterServices);
        dest.writeInt(pollingLocationType);
    }
}
