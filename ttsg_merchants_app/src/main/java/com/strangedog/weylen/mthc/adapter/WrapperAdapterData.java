package com.strangedog.weylen.mthc.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.strangedog.weylen.mthc.iinter.AdapterData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016-07-02.
 */
public abstract class WrapperAdapterData<T, E extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<E> implements AdapterData<T> {

    private List<T> data;
    private LayoutInflater layoutInflater;
    protected WrapperAdapterData(LayoutInflater layoutInflater, List<T> data){
        this.data = data;
        this.layoutInflater = layoutInflater;
    }

    @Override
    public void setData(List<T> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public List<T> getData() {
        return data;
    }

    @Override
    public void addData(List<T> newData) {
        if (data == null){
            data = new ArrayList<>();
        }
        data.addAll(newData);
    }

    @Override
    public void addData(T entity) {
        if (data == null){
            data = new ArrayList<>();
        }
        data.add(entity);
        notifyItemInserted(getItemCount());
    }

    @Override
    public void updateData(int position, T entity) {
        if (validatePosition(position)){
            data.set(position, entity);
        }
        notifyItemChanged(position);
    }

    @Override
    public void removeData(int position) {
        if (validatePosition(position)){
            data.remove(position);
        }
        notifyItemRemoved(position);
    }

    @Override
    public T getItem(int position) {
        if (!validatePosition(position)){return null;}
        return data.get(position);
    }

    private boolean validatePosition(int position){
        int size = getItemCount();
        return position >= 0 && position < size;
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public LayoutInflater getLayoutInflater(){
        return layoutInflater;
    }


}
