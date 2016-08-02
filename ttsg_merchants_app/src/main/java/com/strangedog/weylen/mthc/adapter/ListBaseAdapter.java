package com.strangedog.weylen.mthc.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class ListBaseAdapter<T> extends RecyclerView.Adapter {

    protected ArrayList<T> mDataList = new ArrayList<>();

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public List<T> getDataList() {
        return mDataList;
    }

    public void setDataList(Collection<T> list) {
        this.mDataList.clear();
        this.mDataList.addAll(list);
        notifyDataSetChanged();
    }

    public void addAll(Collection<T> list) {
        int lastIndex = this.mDataList.size();
        if (this.mDataList.addAll(list)) {
            notifyItemRangeInserted(lastIndex, list.size());
        }
    }

    public void delete(int position) {
        mDataList.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    public T getItem(int position){
        return mDataList.get(position);
    }
}
