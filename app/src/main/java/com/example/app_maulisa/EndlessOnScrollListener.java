package com.example.app_maulisa;
import androidx.recyclerview.widget.*;

public abstract class EndlessOnScrollListener extends RecyclerView.OnScrollListener {
    private int mPreviousTotal = 0;
    private boolean mLoading = true;
    private LinearLayoutManager mlayoutmanager ;

    public EndlessOnScrollListener (LinearLayoutManager mlayoutmanager){
        this.mlayoutmanager = mlayoutmanager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int visibleItemCount = mlayoutmanager.getChildCount();
        int totalItemCount = mlayoutmanager.getItemCount();
        int firstVisibleItem = mlayoutmanager.findFirstVisibleItemPosition();
        if (mLoading) {
            if (totalItemCount > mPreviousTotal) {
                mLoading = false;
                mPreviousTotal = totalItemCount;
            }
        }
        int visibleThreshold = 5;
        if (!mLoading && (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + visibleThreshold)) {
            onLoadMore();
            mLoading = true;
        }
    }

    public abstract void onLoadMore();

}