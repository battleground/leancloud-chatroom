package com.abooc.android.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.abooc.android.widget.ViewHolder.OnRecyclerItemClickListener;

/**
 * 单选列表
 * @author zhangjunpu
 * @date 15/8/10
 */
public abstract class SingleChoiceRecyclerAdapter<T> extends BaseRecyclerAdapter<T> implements OnRecyclerItemClickListener {

    protected int mCheckedPosition = -1;

    public SingleChoiceRecyclerAdapter(Context context) {
        super(context);
    }

    public void setCheckedPosition(int position) {
        this.mCheckedPosition = position;
        notifyDataSetChanged();
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        setCheckedPosition(position);

        if (mListener != null) {
            mListener.onItemClick(recyclerView, itemView, position);
        }
    }

}
