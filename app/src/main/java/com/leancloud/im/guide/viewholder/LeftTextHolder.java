package com.leancloud.im.guide.viewholder;

import android.view.View;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.leancloud.im.guide.R;

import java.text.SimpleDateFormat;

/**
 * Created by wli on 15/8/13.
 * 聊天时居左的文本 holder
 */

public class LeftTextHolder extends AVCommonViewHolder<AVIMMessage>  {

    private TextView time;
    private TextView name;
    private TextView content;

    public LeftTextHolder(View itemLayoutView, OnRecyclerItemClickListener listener, OnRecyclerItemChildClickListener childListener) {
        super(itemLayoutView, listener, childListener);
    }

    @Override
    public void onBindedView(View itemLayoutView) {
        time = (TextView) itemLayoutView.findViewById(R.id.chat_left_text_tv_time);
        name = (TextView) itemLayoutView.findViewById(R.id.chat_left_text_tv_name);
        content = (TextView) itemLayoutView.findViewById(R.id.chat_left_text_tv_content);
        name.setOnClickListener(this);
        content.setOnClickListener(this);

    }

    @Override
    public void bindData(AVIMMessage message) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String time = dateFormat.format(message.getTimestamp());

        String content = getContext().getString(R.string.unspport_message_type);
        if (message instanceof AVIMTextMessage) {
            content = ((AVIMTextMessage) message).getText();
        }

        this.content.setText(content);
        this.time.setText(time);
        name.setText(message.getFrom());
    }

    @Override
    public void showTimeView(boolean isShow) {
        time.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

}