package com.tangyang.fribbble.view.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tangyang.fribbble.R;

import java.util.List;

/**
 * Created by YangTang on 10/6/2016.
 */
public abstract class EndlessListAdapter<M> extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;
    private static final int VIEW_TYPE_FOOTER = 2;

    private List<M> data;
    private final Context context;
    private boolean showLoading;

    private final LoadMoreListener loadMoreListener;

    public EndlessListAdapter (@NonNull Context context,
                               @NonNull List<M> data,
                               @NonNull LoadMoreListener loadMoreListener) {
        this.context = context;
        this.data = data;
        this.loadMoreListener = loadMoreListener;
        this.showLoading = true;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_loading,parent, false);
            return new BaseViewHolder(view);
        } else if (viewType == VIEW_TYPE_FOOTER) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_footer, parent, false);
            return new BaseViewHolder(view);
        } else {
            return onCreateItemViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        Log.d("frandblinkc", "binding bucket position: " + position);
        // fire onLoadMore() at the beginning when starting to display last item
        if (data.size() == 0 || position == data.size() - 1) {
            loadMoreListener.onLoadMore();
        }

        if (getItemViewType(position) != VIEW_TYPE_ITEM) {
            return;
        }

        onBindItemViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        // either show loading or footer
        return data.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (showLoading) {
            return position < data.size()? VIEW_TYPE_ITEM: VIEW_TYPE_LOADING;
        } else {
            return position < data.size()? VIEW_TYPE_ITEM: VIEW_TYPE_FOOTER;
        }
    }

    public void append(@NonNull List<M> moreData) {
        Log.d("frandblinkc", "appending loaded items, size: " + moreData.size());
        if (moreData.isEmpty()) {
            return;
        }
        data.addAll(moreData);
        notifyDataSetChanged();
        Log.d("frandblinkc", "after notifying datachanged to append data size: " + moreData.size());
    }

    public void prepend(@NonNull List<M> moreData) {
        if (moreData.isEmpty()) {
            return;
        }
        data.addAll(0, moreData);
        notifyDataSetChanged();
    }

    public void  setData(@NonNull List<M> newData) {
        data.clear();
        data.addAll(newData);
        notifyDataSetChanged();
    }

    public List<M> getData() {
        return data;
    }

    public M removeDataItem(int position) {
        if (position >= 0 && position < data.size()) {
            return data.remove(position);
        }
        return null;
    }

    public void setShowLoading(boolean showLoading) {
        if(showLoading == this.showLoading) {
            return;
        }
        this.showLoading = showLoading;
        notifyDataSetChanged();
    }


    protected Context getContext() {
        return context;
    }

    protected  abstract BaseViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType);

    protected  abstract void onBindItemViewHolder(BaseViewHolder holder, int position);

    public interface LoadMoreListener {
        void onLoadMore();
    }



}
