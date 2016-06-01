package com.example.android.housebillsplitter;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableBillSharing implements Parcelable {
    static final String KEY_BILLSHARING_ID = "billsharing_id";
    static final String KEY_HOUSEMATE_ID= "housemate_id";
    static final String KEY_HOUSEMATE_NAME = "housemate_name";
    static final String KEY_PERCENTAGE = "percentage";

    private String billsharing_id;
    private String housemate_id;
    private String housemate_name;
    private int percentage;

    public ParcelableBillSharing(String billsharing_id, String housemate_id, String housemate_name, int percentage) {
        this.billsharing_id = billsharing_id;
        this.housemate_id = housemate_id;
        this.housemate_name = housemate_name;
        this.percentage = percentage;
    }

    public String getBillsharing_id() {
        return billsharing_id;
    }

    public void setBillsharing_id(String billsharing_id) {
        this.billsharing_id = billsharing_id;
    }

    public String getHousemate_id() {
        return housemate_id;
    }

    public void setHousemate_id(String housemate_id) {
        this.housemate_id = housemate_id;
    }

    public String getHousemate_name() {
        return housemate_name;
    }

    public void setHousemate_name(String housemate_name) {
        this.housemate_name = housemate_name;
    }

    public int getPercentage() {
        return percentage;
    }

    public String getPercentageAsString() {
        return String.valueOf(percentage);
    }

    public void setPercentage(String percentage) {
        this.percentage = Integer.parseInt(percentage);
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();

        bundle.putString(KEY_BILLSHARING_ID, billsharing_id);
        bundle.putString(KEY_HOUSEMATE_ID, housemate_id);
        bundle.putString(KEY_HOUSEMATE_NAME, housemate_name);
        bundle.putInt(KEY_PERCENTAGE, percentage);

        dest.writeBundle(bundle);
    }

    /**
     * Creator required for class implementing the parcelable interface.
     */
    public static final Creator<ParcelableBillSharing> CREATOR = new Creator<ParcelableBillSharing>() {

        @Override
        public ParcelableBillSharing createFromParcel(Parcel source) {
            // read the bundle containing key value pairs from the parcel
            Bundle bundle = source.readBundle();

            // instantiate a person using values from the bundle
            return new ParcelableBillSharing(bundle.getString(KEY_BILLSHARING_ID),bundle.getString(KEY_HOUSEMATE_ID), bundle.getString(KEY_HOUSEMATE_NAME), bundle.getInt(KEY_PERCENTAGE));
        }

        @Override
        public ParcelableBillSharing[] newArray(int size) {
            return new ParcelableBillSharing[size];
        }

    };
}