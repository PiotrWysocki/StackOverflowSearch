package com.piotrwysocki.stackoverflowsearch.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {

    @SerializedName("items")
    private List<Item> items = null;
    @SerializedName("has_more")
    private boolean hasMore;
    @SerializedName("page")
    private int page;
    @SerializedName("total")
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
