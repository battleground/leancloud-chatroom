package com.abooc.android.widget;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *
 * private class Adapter extends RecyclerViewAdapter<Bean_Class> {
 *
 *  @Override
 *  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
 *      View v = LayoutInflater.from(parent.getContext())
 *      .inflate(R.layout.fragment_comment_list_item, parent, false);
 *      return new CommentViewHolder(v, CommentFragment.this);
 *  }
 *
 *  @Override
 * public void onBindViewHolder(ViewHolder h, int position) {
 *      Review notice = getItem(position);
 *      CommentViewHolder holder = (CommentViewHolder) h;
 * <p/>
 *      handle(notice, holder);
 *      holder.iVideoName.setText(notice.video.name);
 *      holder.iTime.setText(TimeUtils.getDiffTime(notice.ctime));
 *  }
 * <p/>
 * }
 * </pre>
 *
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 15-7-23.
 */
public abstract class RecyclerViewAdapter<T> extends RecyclerView.Adapter<ViewHolder> {

    private Collection<T> mArray = new Collection<T>();

    public Collection<T> getCollection() {
        return mArray;
    }

    public T getItem(int position) {
        if (mArray == null || position < 0 || position >= getItemCount())
            return null;
        return mArray.get(position);
    }

    public boolean isEmpty() {
        return mArray.isEmpty();
    }

    @Override
    public int getItemCount() {
        return mArray.size();
    }


    public static class Collection<T> extends ArrayList<T>{
        public void update(List<T> array){
            this.clear();
            this.addAll(array);
        }
    }

}