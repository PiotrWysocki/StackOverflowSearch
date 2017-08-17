package com.piotrwysocki.stackoverflowsearch.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Item implements Parcelable {

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {

        @Override
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
    @SerializedName("owner")
    private Owner owner;
    @SerializedName("answer_count")
    private int answerCount;
    @SerializedName("link")
    private String link;
    @SerializedName("title")
    private String title;

    public Item() {
    }

    private Item(Parcel in) {
        this.owner = in.readParcelable(Owner.class.getClassLoader());
        this.answerCount = in.readInt();
        this.link = in.readString();
        this.title = in.readString();
    }

    public Owner getOwner() {
        return owner;
    }

    public int getAnswerCount() {
        return answerCount;
    }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.owner, flags);
        dest.writeInt(this.answerCount);
        dest.writeString(this.link);
        dest.writeString(this.title);
    }
}
