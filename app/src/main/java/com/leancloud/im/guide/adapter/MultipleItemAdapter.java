package com.leancloud.im.guide.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abooc.android.widget.BaseRecyclerAdapter;
import com.abooc.android.widget.ViewHolder;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.leancloud.im.guide.AVIMClientManager;
import com.leancloud.im.guide.R;
import com.leancloud.im.guide.viewholder.AVCommonViewHolder;
import com.leancloud.im.guide.viewholder.LeftTextHolder;
import com.leancloud.im.guide.viewholder.RightTextHolder;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wli on 15/8/13.
 * 聊天的 Adapter，此处还有可优化的地方，稍后考虑一下提取出公共的 adapter
 */
public class MultipleItemAdapter extends BaseRecyclerAdapter<AVIMMessage> {

    private final int ITEM_LEFT_TEXT = 0;
    private final int ITEM_RIGHT_TEXT = 1;

    // 时间间隔最小为十分钟
    private final long TIME_INTERVAL = 10 * 60 * 1000;

    public MultipleItemAdapter(Context context) {
        super(context);
    }

    public void addFirst(List<AVIMMessage> messages) {
        getCollection().addAll(0, messages);
        notifyDataSetChanged();
    }

    public void addMessage(AVIMMessage message) {
        getCollection().addAll(Arrays.asList(message));
        notifyDataSetChanged();
    }

    public AVIMMessage getFirstMessage() {
        if (getCollection().isEmpty()) return null;
        return getCollection().get(0);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_LEFT_TEXT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_left_text_view, parent, false);
            return new LeftTextHolder(view, mListener, mChildListener);
        } else if (viewType == ITEM_RIGHT_TEXT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_right_text_view, parent, false);
            return new RightTextHolder(view, null, mChildListener);
        } else {
            //TODO
            return null;
        }
    }


    @Override
    public void onBindViewHolder(ViewHolder h, int position) {
        AVCommonViewHolder holder = (AVCommonViewHolder) h;
        AVIMMessage message = getItem(position);
        if (message == null) return;
        holder.bindData(message);
        holder.showTimeView(shouldShowTime(position));
    }

    @Override
    public int getItemViewType(int position) {
        AVIMMessage message = getItem(position);
        if (message.getFrom().equals(AVIMClientManager.getInstance().getClientId())) {
            return ITEM_RIGHT_TEXT;
        } else {
            return ITEM_LEFT_TEXT;
        }
    }

    private boolean shouldShowTime(int position) {
        if (position == 0) {
            return true;
        }
        long lastTime = getItem(position - 1).getTimestamp();
        long curTime = getItem(position).getTimestamp();
        return curTime - lastTime > TIME_INTERVAL;
    }
}