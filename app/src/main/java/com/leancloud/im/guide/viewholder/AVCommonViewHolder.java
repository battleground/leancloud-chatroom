package com.leancloud.im.guide.viewholder;

import android.view.View;

import com.abooc.joker.adapter.recyclerview.ViewHolder;


/**
 * Created by wli on 15/8/25.
 * RecyclerView.ViewHolder 的公共类，做了一些封装。目的：
 * 1、ViewHolder 与 Adapter 解耦，和 ViewHolder 相关的逻辑都放到 ViewHolder 里边，避免 Adapter 有相关逻辑
 * 2、使用 ButterKnife，精简代码，提高代码阅读性
 */

public abstract class AVCommonViewHolder<T> extends ViewHolder {

    public AVCommonViewHolder(View itemLayoutView, OnRecyclerItemClickListener listener, OnRecyclerItemChildClickListener childListener) {
        super(itemLayoutView, listener, childListener);
    }

    /**
     * 用给定的 data 对 holder 的 view 进行赋值
     */
    public abstract void bindData(T t);

    public abstract void showTimeView(boolean isShow);
}