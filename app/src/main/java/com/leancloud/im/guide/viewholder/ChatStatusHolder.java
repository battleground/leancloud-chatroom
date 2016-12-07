package com.leancloud.im.guide.viewholder;

import android.view.View;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.leancloud.im.guide.R;

/**
 * Created by wli on 15/8/13.
 * * 聊天时居右的文本 holder
 */
public class ChatStatusHolder extends AVCommonViewHolder<AVIMMessage> {

    private TextView text;

    public ChatStatusHolder(View itemLayoutView, OnRecyclerItemClickListener listener, OnRecyclerItemChildClickListener childListener) {
        super(itemLayoutView, listener, childListener);
    }

    @Override
    public void onBindedView(View itemLayoutView) {
        text = (TextView) itemLayoutView.findViewById(R.id.text_status);
    }

    @Override
    public void bindData(AVIMMessage message) {
        text.setText(message.getContent());
    }

    @Override
    public void showTimeView(boolean isShow) {
    }

}
