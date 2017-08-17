package com.piotrwysocki.stackoverflowsearch.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.piotrwysocki.stackoverflowsearch.R;
import com.piotrwysocki.stackoverflowsearch.listeners.ItemClickListener;
import com.piotrwysocki.stackoverflowsearch.models.Item;

import java.util.List;

public class ResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<Item> mItems;
    private Context mContext;
    private int mRowLayout;

    private boolean hasLoadingAdded;
    private boolean hasRetryPageLoad;

    private String mErrorMessage;

    private ResultsAdapterCallback mResultsAdapterCallback;

    private ItemClickListener mItemClickListener;

    public ResultsAdapter(List<Item> items, int rowLayout, Context context, ResultsAdapterCallback resultsAdapterCallback,
                          ItemClickListener itemClickListener) {
        mItems = items;
        mRowLayout = rowLayout;
        mContext = context;
        mResultsAdapterCallback = resultsAdapterCallback;
        mItemClickListener = itemClickListener;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.progress_item, parent, false);
                viewHolder = new LoadingViewHolder(viewLoading);
                break;
        }
        return viewHolder;
    }

    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        final RecyclerView.ViewHolder viewHolder;
        View viewResult = inflater.inflate(mRowLayout, parent, false);
        viewHolder = new ResultViewHolder(viewResult);
        viewResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.onItemClick(view, viewHolder.getAdapterPosition());
            }
        });
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Item item = mItems.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                ResultViewHolder resultViewHolder = (ResultViewHolder) holder;

                int answerCount = item.getAnswerCount();
                String stringAnswerCount;
                if (answerCount == 1) {
                    stringAnswerCount = Integer.toString(answerCount) + mContext.getResources().getString(R.string.answer);
                } else {
                    stringAnswerCount = Integer.toString(answerCount) + mContext.getResources().getString(R.string.answers);
                }
                resultViewHolder.answer.setText(stringAnswerCount);

                String title = mContext.getResources().getString(R.string.question) + item.getTitle();
                resultViewHolder.title.setText(title);

                String displayName = "";
                if (item.getOwner().getDisplayName() != null) {
                    displayName = item.getOwner().getDisplayName();
                }
                resultViewHolder.name.setText(displayName);

                if (item.getOwner().getProfileImage() != null) {
                    String stringProfileImage = item.getOwner().getProfileImage();
                    if (stringProfileImage.isEmpty() || stringProfileImage.startsWith(mContext.getResources().getString(R.string.gravatar))) {
                        resultViewHolder.image.setVisibility(View.GONE);
                    } else {
                        resultViewHolder.image.setVisibility(View.VISIBLE);
                        Glide.with(mContext)
                                .load(stringProfileImage)
                                .into(resultViewHolder.image);
                    }
                }

                break;

            case LOADING:
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;

                if (hasRetryPageLoad) {
                    loadingViewHolder.loadingProgressBar.setVisibility(View.GONE);
                    loadingViewHolder.loadingLinearLayout.setVisibility(View.VISIBLE);
                    loadingViewHolder.loadingTextView.setText(mErrorMessage);
                } else {
                    loadingViewHolder.loadingLinearLayout.setVisibility(View.GONE);
                    loadingViewHolder.loadingProgressBar.setVisibility(View.VISIBLE);
                }

                break;
        }
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    public int getItemViewType(int position) {
        return (position == mItems.size() - 1 && hasLoadingAdded) ? LOADING : ITEM;
    }

    private void add(Item item) {
        mItems.add(item);
        notifyItemInserted(mItems.size() - 1);
    }

    public void addAll(List<Item> items) {
        for (Item item : items) {
            add(item);
        }
    }

    public void removeAll() {
        mItems.clear();
    }

    public boolean isHasLoadingAdded() {
        return hasLoadingAdded;
    }

    public void setHasLoadingAdded(boolean hasLoadingAdded) {
        this.hasLoadingAdded = hasLoadingAdded;
    }

    public boolean isHasRetryPageLoad() {
        return hasRetryPageLoad;
    }

    public void setHasRetryPageLoad(boolean hasRetryPageLoad) {
        this.hasRetryPageLoad = hasRetryPageLoad;
    }

    public void addLoadingFooter() {
        hasLoadingAdded = true;
        add(new Item());
    }

    public void removeLoadingFooter() {
        hasLoadingAdded = false;

        int position = mItems.size() - 1;
        Item item = mItems.get(position);

        if (item != null) {
            mItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void showRetry(boolean show) {
        hasRetryPageLoad = show;
        mErrorMessage = "No internet connection";
        notifyItemChanged(mItems.size() - 1);
    }

    private class ResultViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView answer;
        TextView name;
        TextView title;

        ResultViewHolder(View view) {
            super(view);

            image = (ImageView) view.findViewById(R.id.result_image);
            answer = (TextView) view.findViewById(R.id.result_answers);
            name = (TextView) view.findViewById(R.id.result_name);
            title = (TextView) view.findViewById(R.id.result_title);

        }

    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        ImageButton loadingImageButton;
        LinearLayout loadingLinearLayout;
        ProgressBar loadingProgressBar;
        TextView loadingTextView;

        LoadingViewHolder(View itemView) {
            super(itemView);

            loadingImageButton = (ImageButton) itemView.findViewById(R.id.load_more_image_button);
            loadingLinearLayout = (LinearLayout) itemView.findViewById(R.id.load_more_linear_layout);
            loadingProgressBar = (ProgressBar) itemView.findViewById(R.id.load_more_progress_bar);
            loadingTextView = (TextView) itemView.findViewById(R.id.load_more_text_view);

            loadingImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showRetry(false);
                    mResultsAdapterCallback.retryPageLoad();
                }
            });
        }

    }


}
