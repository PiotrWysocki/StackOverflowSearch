package com.piotrwysocki.stackoverflowsearch.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.piotrwysocki.stackoverflowsearch.R;
import com.piotrwysocki.stackoverflowsearch.adapters.ResultsAdapter;
import com.piotrwysocki.stackoverflowsearch.adapters.ResultsAdapterCallback;
import com.piotrwysocki.stackoverflowsearch.api.APIClient;
import com.piotrwysocki.stackoverflowsearch.api.APIService;
import com.piotrwysocki.stackoverflowsearch.listeners.ItemClickListener;
import com.piotrwysocki.stackoverflowsearch.listeners.ScrollListener;
import com.piotrwysocki.stackoverflowsearch.models.Item;
import com.piotrwysocki.stackoverflowsearch.models.Result;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Piotrek on 2017-07-13.
 */

public class ResultsFragment extends Fragment implements ResultsAdapterCallback {

    public final static String QUERY = "query";
    private static final int PAGE_START = 1;
    String mCurrentQuery = "";
    private APIService mApiService;
    private Button mButton;
    private LinearLayout mLinearLayout;
    private List<Item> mItems = new ArrayList<>();
    private ProgressBar mProgressBar;
    private ResultsAdapter mResultsAdapter;
    private TextView mTextView;
    private boolean hasLoading = false;
    private boolean hasLastPage = false;
    private int mCurrentPage = PAGE_START;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.results_view, container, false);

        setHasOptionsMenu(true);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mProgressBar = (ProgressBar) view.findViewById(R.id.main_progress);
        mLinearLayout = (LinearLayout) view.findViewById(R.id.error_layout);
        mButton = (Button) view.findViewById(R.id.error_retry_button);
        mTextView = (TextView) view.findViewById(R.id.error_text_view);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mResultsAdapter = new ResultsAdapter(mItems, R.layout.result_item, getContext(), this, new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String url = mItems.get(position).getLink();
                openWebPage(url);
            }
        });

        mRecyclerView.setAdapter(mResultsAdapter);

        Bundle args = getArguments();
        if (args != null) {
            mCurrentQuery = args.getString(QUERY);
        }

        mRecyclerView.addOnScrollListener(new ScrollListener(mLinearLayoutManager) {
            @Override
            public void loadMoreItems() {
                hasLoading = true;
                mCurrentPage += 1;

                loadNextPage();
            }

            @Override
            public boolean isLastPage() {
                return hasLastPage;
            }

            @Override
            public boolean isLoading() {
                return hasLoading;
            }
        });

        mApiService = APIClient.getClient().create(APIService.class);

        if (mCurrentQuery.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
        } else {
            loadFirstPage();
        }

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFirstPage();
            }
        });

        return view;

    }

    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.item_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.refresh) {
            if (mCurrentQuery.isEmpty()) {
                showNoQueryView();
            } else {
                mCurrentPage = 1;
                hasLastPage = false;
                mResultsAdapter.removeAll();
                mResultsAdapter.notifyDataSetChanged();
                loadFirstPage();
            }
            return true;
        }
        return false;
    }

    private void loadFirstPage() {

        hideErrorView();

        callResult().enqueue(new Callback<Result>() {
            @Override
            public void onResponse(@NonNull Call<Result> call, @NonNull Response<Result> response) {

                List<Item> items = fetchItems(response);
                if (items.size() == 0) {
                    showNoResultView();
                } else {
                    mProgressBar.setVisibility(View.GONE);
                    mResultsAdapter.addAll(items);

                    if (response.body().isHasMore()) mResultsAdapter.addLoadingFooter();
                    else hasLastPage = true;
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result> call, @NonNull Throwable t) {
                showErrorView();
            }
        });
    }

    private void loadNextPage() {
        callResult().enqueue(new Callback<Result>() {
            @Override
            public void onResponse(@NonNull Call<Result> call, @NonNull Response<Result> response) {
                mResultsAdapter.removeLoadingFooter();
                hasLoading = false;

                List<Item> items = fetchItems(response);
                mResultsAdapter.addAll(items);

                if (response.body().isHasMore()) mResultsAdapter.addLoadingFooter();
                else hasLastPage = true;

            }

            @Override
            public void onFailure(@NonNull Call<Result> call, @NonNull Throwable t) {
                mResultsAdapter.showRetry(true);
            }
        });
    }

    private List<Item> fetchItems(Response<Result> response) {
        Result result = response.body();
        return result.getItems();
    }

    private Call<Result> callResult() {
        return mApiService.getResults(mCurrentQuery, mCurrentPage);
    }

    @Override
    public void retryPageLoad() {
        loadNextPage();
    }

    private void showNoQueryView() {
        mLinearLayout.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mButton.setVisibility(View.GONE);
        String noQueryString = "No query to search";
        mTextView.setText(noQueryString);

    }

    private void showNoResultView() {
        mLinearLayout.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mButton.setVisibility(View.GONE);
        String noResultString = "No results found";
        mTextView.setText(noResultString);

    }

    private void showErrorView() {
        if (mLinearLayout.getVisibility() == View.GONE) {
            mLinearLayout.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mButton.setVisibility(View.VISIBLE);
            String errorString = "No internet connection";
            mTextView.setText(errorString);
        }
    }

    private void hideErrorView() {
        if (mLinearLayout.getVisibility() == View.VISIBLE) {
            mLinearLayout.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    public void updateResultsView(String query) {
        mResultsAdapter.removeAll();
        mResultsAdapter.notifyDataSetChanged();
        mCurrentQuery = query;
        loadFirstPage();
    }
}
