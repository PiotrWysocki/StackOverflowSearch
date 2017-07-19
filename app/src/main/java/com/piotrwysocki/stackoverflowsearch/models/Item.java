package com.piotrwysocki.stackoverflowsearch.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Piotrek on 2017-07-14.
 */

public class Item {

    @SerializedName("owner")
    @Expose
    private Owner owner;
    @SerializedName("answer_count")
    @Expose
    private int answerCount;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("title")
    @Expose
    private String title;

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

}
