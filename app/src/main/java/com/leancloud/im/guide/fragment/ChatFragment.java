package com.leancloud.im.guide.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abooc.joker.adapter.recyclerview.ViewHolder.OnRecyclerItemChildClickListener;
import com.abooc.util.Debug;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationMemberCountCallback;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.leancloud.im.guide.AVIMClientManager;
import com.leancloud.im.guide.AVInputBottomBar;
import com.leancloud.im.guide.NotificationUtils;
import com.leancloud.im.guide.R;
import com.leancloud.im.guide.activity.AVSingleChatActivity;
import com.leancloud.im.guide.adapter.MultipleItemAdapter;
import com.leancloud.im.guide.event.ConversationStatusEvent;
import com.leancloud.im.guide.event.ConversationStatusEvent.EventAction;
import com.leancloud.im.guide.event.ImTypeMessageEvent;
import com.leancloud.im.guide.event.InputBottomBarTextEvent;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func2;

/**
 * Created by wli on 15/8/27.
 * 将聊天相关的封装到此 Fragment 里边，只需要通过 setConversation 传入 Conversation 即可
 */
public class ChatFragment extends Fragment implements OnRecyclerItemChildClickListener {

    public static final int COUNT_SIZE = 20; // 消息数量

    protected AVIMConversation mConversation;

    protected MultipleItemAdapter mAdapter;
    protected RecyclerView mRecyclerView;
    protected LinearLayoutManager mLayoutManager;
    protected SwipeRefreshLayout mRefreshLayout;
    protected AVInputBottomBar mInputBottomBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        mInputBottomBar = (AVInputBottomBar) view.findViewById(R.id.fragment_chat_inputbottombar);
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_chat_srl_pullrefresh);
        mRefreshLayout.setEnabled(false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_chat_rv_chat);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MultipleItemAdapter(getContext());
        mAdapter.setOnRecyclerItemChildClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AVIMMessage message = mAdapter.getFirstMessage();
                if (mConversation == null || message == null) {
                    mRefreshLayout.setRefreshing(false);
                    return;
                }
                mConversation.queryMessages(message.getMessageId(), message.getTimestamp(), COUNT_SIZE, new AVIMMessagesQueryCallback() {
                    @Override
                    public void done(List<AVIMMessage> list, AVIMException e) {
                        mRefreshLayout.setRefreshing(false);
                        if (Debug.printStackTrace(e)) return;
                        if (null != list && !list.isEmpty()) {
                            mAdapter.addFirst(list);
                            mLayoutManager.scrollToPositionWithOffset(list.size() - 1, 0);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != mConversation) {
            NotificationUtils.addTag(mConversation.getConversationId());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        NotificationUtils.removeTag(mConversation.getConversationId());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    public void setConversation(AVIMConversation conversation) {
        if (conversation == null) return;
        mConversation = conversation;
        mRefreshLayout.setEnabled(true);
        mInputBottomBar.setTag(mConversation.getConversationId());
        fetchMessages();
        NotificationUtils.addTag(conversation.getConversationId());
        getMemberCount();
    }

    /**
     * 获取在线人数
     */
    private void getMemberCount() {
        mConversation.getMemberCount(new AVIMConversationMemberCountCallback() {
            @Override
            public void done(Integer count, AVIMException e) {
                if (Debug.printStackTrace(e)) return;
                Debug.error("conversation got " + count + " members");
                AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
                appCompatActivity.getSupportActionBar().setSubtitle("在线成员：" + count);
            }
        });
    }

    /**
     * 拉取消息，必须加入 conversation 后才能拉取消息，默认20条
     */
    private void fetchMessages() {
        if (mConversation == null) return;
        mConversation.queryMessages(new AVIMMessagesQueryCallback() {
            @Override
            public void done(List<AVIMMessage> list, AVIMException e) {
                if (Debug.printStackTrace(e)) return;
                mAdapter.update(list);
                scrollToBottom();
            }
        });
    }

    @Override
    public void onItemChildClick(RecyclerView recyclerView, View itemView, View clickView, int position) {
        AVIMMessage message = mAdapter.getItem(position);
        if (message == null) return;
        switch (clickView.getId()) {
            // 单独会话
            case R.id.chat_left_text_tv_content:
            case R.id.chat_left_text_tv_name:
                AVSingleChatActivity.launch(getActivity(), message.getFrom());
                break;
            // 错误重发
            case R.id.chat_right_text_tv_error:
                resend(message);
                break;
        }
    }

    /**
     * 重新发送已经发送失败的消息
     */
    public void resend(AVIMMessage message) {
        if (mConversation == null) return;
        if (AVIMMessage.AVIMMessageStatus.AVIMMessageStatusFailed == message.getMessageStatus()
                && mConversation.getConversationId().equals(message.getConversationId())) {
            mConversation.sendMessage(message, new AVIMConversationCallback() {
                @Override
                public void done(AVIMException e) {
                    mAdapter.notifyDataSetChanged();
                }
            });
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 输入事件处理，接收后构造成 AVIMTextMessage 然后发送
     * 因为不排除某些特殊情况会受到其他页面过来的无效消息，所以此处加了 tag 判断
     */
    public void onEvent(InputBottomBarTextEvent textEvent) {
        if (mConversation == null || textEvent == null) return;
        if (!TextUtils.isEmpty(textEvent.sendContent) && mConversation.getConversationId().equals(textEvent.tag)) {
            AVIMTextMessage message = new AVIMTextMessage();
            message.setText(textEvent.sendContent);
            mAdapter.add(message);
            scrollToBottom();
            mConversation.sendMessage(message, new AVIMConversationCallback() {
                @Override
                public void done(AVIMException e) {
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    /**
     * 处理推送过来的消息
     * 同理，避免无效消息，此处加了 conversation id 判断
     */
    public void onEvent(ImTypeMessageEvent event) {
        if (mConversation == null || event == null) return;
        if (mConversation.getConversationId().equals(event.conversation.getConversationId())) {
            mAdapter.add(event.message);
            scrollToBottom();
        }
    }

    /**
     * 当有成员加入、退出、被踢、被邀请时调用
     */
    public void onEvent(ConversationStatusEvent event) {
        if (mConversation == null || event == null || !event.isOK(mConversation))
            return;
        // 加入、退出
        if (event.action == EventAction.JOIN || event.action == EventAction.QUIT) {
            List<String> members = new ArrayList<String>(event.members);
            // 过滤自己
            if (members.contains(AVIMClientManager.getInstance().getClientId())) {
                members.remove(AVIMClientManager.getInstance().getClientId());
                Debug.out("过滤自己");
            }
            if (members.isEmpty()) return;
            // 合成消息
            final StringBuilder sb = new StringBuilder();
            Observable.from(members)
                    .reduce(new Func2<String, String, String>() {
                        @Override
                        public String call(String s, String s2) {
                            return s + ", " + s2;
                        }
                    })
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            Debug.out(s);
                            sb.append(s);
                        }
                    });
            sb.append(event.action.getValue());
            AVIMMessage message = new AVIMMessage();
            message.setContent(sb.toString());
            mAdapter.add(message);
            scrollToBottom();
            getMemberCount();
        } else if (event.action == EventAction.REMOVE) { // 自己被踢出
            getActivity().finish();
        }
    }

    private void scrollToBottom() {
        mLayoutManager.scrollToPositionWithOffset(mAdapter.getItemCount() - 1, 0);
    }

}
