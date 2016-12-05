package com.abooc.chatroom;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.abooc.util.Debug;
import com.alibaba.fastjson.JSONArray;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.leancloud.im.guide.Constants;
import com.leancloud.im.guide.R;
import com.leancloud.im.guide.activity.AVSquareActivity;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomListFragment extends Fragment {


    String memberId;

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

        mListView = (ListView) view.findViewById(R.id.ListView);
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

        queryConversations(memberId);

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


    class ListAdapter extends BaseAdapter {

        List<AVIMConversation> convs = new ArrayList<AVIMConversation>();

        public void update(List<AVIMConversation> convs) {
            this.convs = convs;
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
            textId.setText(conversation.getConversationId() + "  人数：" + size);
            return view;
        }
    }

}
