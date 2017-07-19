package com.piotrwysocki.stackoverflowsearch.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Piotrek on 2017-07-14.
 */

public class Result {

    @SerializedName("items")
    @Expose
    private List<Item> items = null;
    @SerializedName("has_more")
    @Expose
    private boolean hasMore;
    @SerializedName("page")
    @Expose
    private int page;
    @SerializedName("total")
    @Expose
    private int total;

    public List<Item> getItems() {
        return items;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public int getPage() {
        return page;
    }

    public int getTotal() {
        return total;
    }

}
