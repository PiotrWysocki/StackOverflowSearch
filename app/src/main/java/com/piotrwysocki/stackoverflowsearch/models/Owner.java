package com.piotrwysocki.stackoverflowsearch.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Owner implements Parcelable {

    public static final Parcelable.Creator<Owner> CREATOR = new Parcelable.Creator<Owner>() {

        @Override
        public Owner createFromParcel(Parcel source) {
            return new Owner(source);
        }

        @Override
        public Owner[] newArray(int size) {
            return new Owner[size];
        }
    };
    @SerializedName("profile_image")
    private String profileImage;
    @SerializedName("display_name")
    private String displayName;

    public Owner() {
    }

    private Owner(Parcel in) {
        this.profileImage = in.readString();
        this.displayName = in.readString();
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.profileImage);
        dest.writeString(this.displayName);
    }
}
