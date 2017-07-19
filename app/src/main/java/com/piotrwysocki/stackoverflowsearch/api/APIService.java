package com.piotrwysocki.stackoverflowsearch.api;

import com.piotrwysocki.stackoverflowsearch.models.Result;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Piotrek on 2017-07-14.
 */

public interface APIService {

    @GET("2.2/search?order=desc&sort=activity&site=stackoverflow&filter=!-*f(6t9N5Uab")
    Call<Result> getResults(@Query("intitle") String inTitle, @Query("page") int page);
}
