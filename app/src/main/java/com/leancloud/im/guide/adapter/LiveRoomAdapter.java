package com.leancloud.im.guide.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abooc.joker.adapter.recyclerview.BaseRecyclerAdapter;
import com.abooc.joker.adapter.recyclerview.ViewHolder;
import com.abooc.test.data.LiveRoom;
import com.leancloud.im.guide.R;

/**
 * 直播列表
 * @author zhangjunpu
 * @date 16/8/22
 */
public class LiveRoomAdapter extends BaseRecyclerAdapter<LiveRoom> {

    public LiveRoomAdapter(Context context) {
        super(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.liveroom_item, parent, false);
        return new LiveRoomHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int position) {
        LiveRoom room = getItem(position);
        if (room == null) return;
        LiveRoomHolder holder = (LiveRoomHolder) h;
        holder.bindData(room);
    }

    static class LiveRoomHolder extends ViewHolder {

        TextView title;
        TextView userName;
        TextView tr;

        public LiveRoomHolder(View itemLayoutView, OnRecyclerItemClickListener listener) {
            super(itemLayoutView, listener);
        }

        @Override
        public void onBindedView(View itemLayoutView) {
            title = (TextView) itemLayoutView.findViewById(R.id.title);
            userName = (TextView) itemLayoutView.findViewById(R.id.userName);
            tr = (TextView) itemLayoutView.findViewById(R.id.tr);
        }

        public void bindData(LiveRoom room) {
            title.setText(room.getTitle());
            userName.setText(room.getCreatId());
            tr.setText(room.isTr() ? "暂态" : null);
            tr.setVisibility(room.isTr() ? View.VISIBLE : View.INVISIBLE);
        }

    }
}
