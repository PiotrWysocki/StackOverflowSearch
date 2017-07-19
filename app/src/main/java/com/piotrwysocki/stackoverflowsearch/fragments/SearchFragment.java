package com.piotrwysocki.stackoverflowsearch.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.piotrwysocki.stackoverflowsearch.R;

/**
 * Created by Piotrek on 2017-07-13.
 */

public class SearchFragment extends Fragment {
    OnSearchButtonClickedListener mCallback;

    private EditText mSearchEditText;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (OnSearchButtonClickedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnSearchButtonClickedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_view, container, false);

        Button mSearchButton = (Button) view.findViewById(R.id.search_button);
        mSearchEditText = (EditText) view.findViewById(R.id.search_edit_text);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSearchRequest();
            }
        });

        return view;

    }

    private void handleSearchRequest() {
        String mQuery = mSearchEditText.getText().toString().trim();
        if (TextUtils.isEmpty(mQuery)) {
            mSearchEditText.setError("Fill this field");
        } else {
            mSearchEditText.setText("");
            hideKeyboard();
            mCallback.onSearchButtonClicked(mQuery);
        }
    }

    private void hideKeyboard() {
        View view = getView().findFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public interface OnSearchButtonClickedListener {
        void onSearchButtonClicked(String query);
    }


}
