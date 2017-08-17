package com.piotrwysocki.stackoverflowsearch;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.piotrwysocki.stackoverflowsearch.fragments.ResultsFragment;
import com.piotrwysocki.stackoverflowsearch.fragments.SearchFragment;

public class MainActivity extends AppCompatActivity implements SearchFragment.OnSearchButtonClickedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searches);

        if (findViewById(R.id.fragment_container) != null) {

            if (savedInstanceState != null) {
                return;
            }

            SearchFragment searchFragment = new SearchFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, searchFragment).commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (findViewById(R.id.fragment_container) != null) {
            if (findViewById(R.id.recycler_view) != null) {
                ActionBar ab = getSupportActionBar();
                if (ab != null) {
                    ab.setDisplayHomeAsUpEnabled(true);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public void onSearchButtonClicked(String query) {

        ResultsFragment resultsFragment = (ResultsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.results_fragment);

        if (resultsFragment != null) {
            resultsFragment.updateResultsView(query);
        } else {
            ResultsFragment resultsFragment2 = new ResultsFragment();
            Bundle args = new Bundle();
            args.putString(ResultsFragment.QUERY, query);
            resultsFragment2.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, resultsFragment2);
            transaction.addToBackStack(null);
            transaction.commit();

            ActionBar ab = getSupportActionBar();
            if (ab != null) {
                ab.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

}
