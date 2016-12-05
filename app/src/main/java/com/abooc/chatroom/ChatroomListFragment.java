package com.abooc.chatroom;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.abooc.util.Debug;
import com.abooc.widget.Toast;
import com.alibaba.fastjson.JSONArray;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.leancloud.im.guide.Constants;
import com.leancloud.im.guide.R;
import com.leancloud.im.guide.activity.AVSquareActivity;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomListFragment extends Fragment {


    String memberId;

    TextView mEmptyView;
    ListView mListView;
    final ListAdapter mListAdapter = new ListAdapter();

    public ChatRoomListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        memberId = getArguments().getString(Constants.MEMBER_ID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chatroom_list, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mEmptyView = (TextView) view.findViewById(R.id.Empty);
        mListView = (ListView) view.findViewById(R.id.ListView);
        mListView.setEmptyView(mEmptyView);
        final EditText mJoinEdit = (EditText) view.findViewById(R.id.EditText);


        view.findViewById(R.id.Join).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Editable conversationId = mJoinEdit.getText();
                Debug.error(conversationId);
                if (TextUtils.isEmpty(conversationId)) {
                    return;
                } else {
                    join(conversationId.toString());
                }
            }
        });
        mListView.setAdapter(mListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AVIMConversation conversation = mListAdapter.getItem(i);

                Intent intent = new Intent(getActivity(), AVSquareActivity.class);
                intent.putExtra(Constants.CONVERSATION_ID, conversation.getConversationId());
                intent.putExtra(Constants.ACTIVITY_TITLE, conversation.getName());
                startActivity(intent);
            }
        });

        clear();
        queryConversations(memberId);

    }

    public void clear() {
        mEmptyView.setText("Loading...");
        mListAdapter.clear();
    }

    public void queryConversations(String memberId) {
        AVIMClient tom = AVIMClient.getInstance(memberId);
        tom.open(new AVIMClientCallback() {

            @Override
            public void done(AVIMClient client, AVIMException e) {
                if (e == null) {
                    //登录成功
                    AVIMConversationQuery query = client.getQuery();
                    query.findInBackground(new AVIMConversationQueryCallback() {
                        @Override
                        public void done(final List<AVIMConversation> convs, AVIMException e) {
                            if (e == null) {
                                //convs就是获取到的conversation列表
                                //注意：按每个对话的最后更新日期（收到最后一条消息的时间）倒序排列

                                final Object toJSON = JSONArray.toJSON(convs);

                                Debug.anchor(toJSON);

                                new Handler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mListAdapter.update(convs);
                                    }
                                });


                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 加入会话
     *
     * @param conversationId
     */
    public void join(String conversationId) {
        AVIMClient client = AVIMClient.getInstance(memberId);
        AVIMConversationQuery query = client.getQuery();
        query.whereEqualTo("objectId", conversationId);
        query.findInBackground(new AVIMConversationQueryCallback() {
            @Override
            public void done(List<AVIMConversation> convs, AVIMException e) {
                if (e == null) {
                    if (convs != null && !convs.isEmpty()) {
                        final AVIMConversation conversation = convs.get(0); //就是想要的conversation
                        conversation.join(new AVIMConversationCallback() {
                            @Override
                            public void done(AVIMException e) {
                                if (e == null) {
                                    //加入成功
                                    AVSquareActivity.launch(getActivity(),
                                            conversation.getConversationId(),
                                            conversation.getName());
                                } else {
                                    Toast.show("加入失败！");
                                }
                            }
                        });
                    } else {
                        Toast.show("未找到聊天室！");
                    }
                } else {
                    Toast.show("加入聊天室失败！");
                }
            }
        });
    }


    class ListAdapter extends BaseAdapter {

        List<AVIMConversation> convs = new ArrayList<AVIMConversation>();

        public void update(List<AVIMConversation> convs) {
            this.convs = convs;
            notifyDataSetChanged();
        }

        public void clear() {
            this.convs.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return convs.size();
        }

        @Override
        public AVIMConversation getItem(int i) {
            return convs.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_chatroom_list_item, viewGroup, false);
            }

            AVIMConversation conversation = getItem(i);

            TextView textId = (TextView) view.findViewById(R.id.chatroom_list_item_id);
            TextView textName = (TextView) view.findViewById(R.id.chatroom_list_item_name);

            int size = conversation.getMembers().size();
            textName.setText(conversation.getName());
            textId.setText(conversation.getConversationId() + "  在线成员：" + size);
            return view;
        }
    }

}
