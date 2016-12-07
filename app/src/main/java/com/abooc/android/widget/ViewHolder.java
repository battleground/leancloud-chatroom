package com.abooc.android.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewParent;

/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 15-7-23.
 * <p/>
 * RecyclerView统一使用的ViewHolder
 */
public abstract class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public interface OnRecyclerItemClickListener {
        /**
         * @param recyclerView RecyclerView
         * @param itemView     元素根布局
         * @param position     the adapter position that the given child view corresponds to.
         */
        void onItemClick(RecyclerView recyclerView, View itemView, int position);
    }

    public interface OnRecyclerItemChildClickListener {
        /**
         * @param recyclerView RecyclerView
         * @param itemView  元素根布局
         * @param clickView 响应点击事件的View
         * @param position  the adapter position that the given child view corresponds to.
         */
        void onItemChildClick(RecyclerView recyclerView, View itemView, View clickView, int position);
    }


    private Context mContext;
    public OnRecyclerItemClickListener mListener;
    public OnRecyclerItemChildClickListener mChildClickListener;

    public ViewHolder(View itemLayoutView, OnRecyclerItemClickListener listener) {
        this(itemLayoutView, listener, null);
    }

    public ViewHolder(View itemLayoutView, OnRecyclerItemClickListener listener, OnRecyclerItemChildClickListener childListener) {
        super(itemLayoutView);
        mListener = listener;
        mChildClickListener = childListener;
        mContext = itemLayoutView.getContext();
        if (listener != null)
            itemLayoutView.setOnClickListener(this);
        onBindedView(itemLayoutView);
    }

    public Context getContext() {
        return mContext;
    }

    public abstract void onBindedView(View itemLayoutView);

    @Override
    public void onClick(View v) {
        if (mListener != null && v.getId() == itemView.getId()) {
            mListener.onItemClick(findTarget(itemView.getParent()), v, getAdapterPosition());
        } else if (mChildClickListener != null) {
            mChildClickListener.onItemChildClick(findTarget(itemView.getParent()), itemView, v, getAdapterPosition());
        }
    }

    private RecyclerView findTarget(ViewParent parent) {
        if (parent instanceof RecyclerView)
            return (RecyclerView) parent;
        else
            return findTarget(parent);
    }
}