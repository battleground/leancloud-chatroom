package com.leancloud.im.guide.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.abooc.joker.adapter.recyclerview.ViewHolder.OnRecyclerItemClickListener;
import com.abooc.test.data.LiveRoom;
import com.abooc.util.Debug;
import com.alibaba.fastjson.JSON;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.leancloud.im.guide.AVIMClientManager;
import com.leancloud.im.guide.Constants;
import com.leancloud.im.guide.R;
import com.leancloud.im.guide.activity.AVSquareActivity;
import com.leancloud.im.guide.adapter.LiveRoomAdapter;
import com.leancloud.im.guide.utils.AssetsUtils;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomListFragment extends Fragment implements OnRecyclerItemClickListener {


//    String memberId;
    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRoomList;
    private LiveRoomAdapter mAdapter;

    TextView mEmptyView;
    ListView mListView;
    ListAdapter mListAdapter = new ListAdapter();

    public ChatRoomListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        memberId = getArguments().getString(Constants.MEMBER_ID);
//        memberId = AVIMClientManager.getInstance().getClientId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chatroom_list, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.setRefreshing(false);
                        getLiveRooms();
                    }
                }, 800);
            }
        });
        mRoomList = (RecyclerView) view.findViewById(R.id.roomList);
        mRoomList.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new LiveRoomAdapter(getContext());
        mAdapter.setOnRecyclerItemClickListener(this);
        mRoomList.setAdapter(mAdapter);

        mEmptyView = (TextView) view.findViewById(R.id.Empty);
        mListView = (ListView) view.findViewById(R.id.ListView);
        mListView.setEmptyView(mEmptyView);
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
        queryConversations();
        getLiveRooms();
    }

    @Override
    public void onResume() {
        super.onResume();
        clear();
        queryConversations();
    }

    /**
     * 获取直播间列表
     */
    public void getLiveRooms() {
        String json = AssetsUtils.getFromAssets("LiveRoom.json", getContext());
        List<LiveRoom> list = JSON.parseArray(json, LiveRoom.class);
        mAdapter.update(list);
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        LiveRoom room = mAdapter.getItem(position);
        if (room == null) return;
        AVSquareActivity.launch(getActivity(), room.getConversationId(), room.getTitle());
    }


    public void clear() {
//        mEmptyView.setText("Loading...");
        mListAdapter.clear();
    }

    /**
     * 查询回话列表
     */
    public void queryConversations() {
        final AVIMClient client = AVIMClientManager.getInstance().getClient();
        AVIMConversationQuery query = client.getQuery();
        query.setQueryPolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findInBackground(new AVIMConversationQueryCallback() {
            @Override
            public void done(final List<AVIMConversation> convs, AVIMException e) {
                if (Debug.printStackTrace(e)) return;
                //convs就是获取到的conversation列表
                //注意：按每个对话的最后更新日期（收到最后一条消息的时间）倒序排列
//                final Object toJSON = JSONArray.toJSON(convs);
                String json = JSON.toJSONString(convs);
                Debug.anchor(json);


                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        mListAdapter.update(convs);
                    }
                });
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
