package com.abooc.android.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.abooc.android.widget.ViewHolder.OnRecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 多选列表
 * @author zhangjunpu
 * @date 15/8/10
 */
public abstract class MultipleChoiceRecyclerAdapter<T> extends BaseRecyclerAdapter<T> implements OnRecyclerItemClickListener {

    protected List<T> mCheckedDatas = new ArrayList<T>();

    public MultipleChoiceRecyclerAdapter(Context context) {
        super(context);
    }

    public void setCheckedPosition(T object) {
        if (exist(object)) {
            mCheckedDatas.remove(object);
        } else {
            mCheckedDatas.add(object);
        }
        notifyDataSetChanged();
    }

    /**
     * 判断是否存在
     */
    protected boolean exist(T object) {
        return mCheckedDatas.contains(object);
    }

    public List<T> getCheckedDatas() {
        return mCheckedDatas;
    }

    /**
     * 清空所有已选择的数据
     */
    public void clearCheckedDatas() {
        mCheckedDatas.clear();
    }

    /**
     * 获取已选择的数量
     */
    public int getCheckedCount() {
        return mCheckedDatas.size();
    }

    /**
     * 选择或取消选择全部
     */
    public void checkedAll(boolean flag) {
        clearCheckedDatas();
        if (flag) {
            for (T t : getCollection()) {
                mCheckedDatas.add(t);
            }
        }
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        setCheckedPosition(getItem(position));

        if (mListener != null) {
            mListener.onItemClick(recyclerView, itemView, position);
        }
    }

}
