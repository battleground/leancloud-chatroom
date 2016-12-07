package com.leancloud.im.guide.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.abooc.android.widget.ViewHolder.OnRecyclerItemClickListener;
import com.abooc.util.Debug;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.leancloud.im.guide.AVIMClientManager;
import com.leancloud.im.guide.Constants;
import com.leancloud.im.guide.LetterView;
import com.leancloud.im.guide.R;
import com.leancloud.im.guide.adapter.MembersAdapter;
import com.leancloud.im.guide.adapter.MembersAdapter.MemberItem;
import com.leancloud.im.guide.event.MemberLetterEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by wli on 15/8/14.
 * 在线成员列表
 * 当前版本因为暂态回话不能查询成员而导致此页面的入口被注释掉
 */
public class AVSquareMembersActivity extends AVBaseActivity implements OnRecyclerItemClickListener {

    public static void launch(Context context, String conversationId) {
        Intent intent = new Intent(context, AVSquareMembersActivity.class);
        intent.putExtra(Constants.CONVERSATION_ID, conversationId);
        context.startActivity(intent);
    }

    @Bind(R.id.toolbar)
    protected Toolbar mToolbar;

    @Bind(R.id.activity_square_members_srl_list)
    protected SwipeRefreshLayout mRefreshLayout;

    @Bind(R.id.activity_square_members_letterview)
    protected LetterView mLetterView;

    @Bind(R.id.activity_square_members_rv_list)
    protected RecyclerView mRecyclerView;

    private SearchView mSearchView;

    private MembersAdapter mAdapter;
    private AVIMConversation mConversation;
    LinearLayoutManager mLayoutManager;
    private List<String> mMemberList;

    String mConversationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mConversationId = getIntent().getStringExtra(Constants.CONVERSATION_ID);

        setContentView(R.layout.activity_square_members);

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.btn_navigation_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setTitle(R.string.square_member_title);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MembersAdapter(this);
        mAdapter.setOnRecyclerItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mConversation.fetchInfoInBackground(new AVIMConversationCallback() {
                    @Override
                    public void done(AVIMException e) {
                        if (filterException(e)) {
                            getMembers();
                        }
                        mRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
        getMembers();
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        MemberItem member = mAdapter.getItem(position);
        if (member == null) return;
        AVSingleChatActivity.launch(this, member.content);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_member_menu, menu);

        mSearchView = (SearchView) menu.findItem(R.id.activity_member_menu_search).getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.setMemberList(filterMembers(newText));
                mAdapter.notifyDataSetChanged();
                return false;
            }
        });
        return true;
    }

    /**
     * 在 mMemberList 里匹配搜索结果
     */
    private List<String> filterMembers(String content) {
        List<String> members = new ArrayList<String>();
        for (String name : mMemberList) {
            if (name.contains(content)) {
                members.add(name);
            }
        }
        return members;
    }

    /**
     * 从 AVIMConversation 获取 member，如果本地没有则做拉取请求，然后更新 UI
     */
    private void getMembers() {
        mConversation = AVIMClientManager.getInstance().getClient().getConversation(mConversationId);
        mMemberList = mConversation.getMembers();
        if (null != mMemberList && mMemberList.size() > 0) {
            mAdapter.setMemberList(mMemberList);
            mAdapter.notifyDataSetChanged();
        } else {
            mConversation.fetchInfoInBackground(new AVIMConversationCallback() {
                @Override
                public void done(AVIMException e) {
                    if (Debug.printStackTrace(e)) return;
                    mMemberList = mConversation.getMembers();
                    mAdapter.setMemberList(mMemberList);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    /**
     * 处理 LetterView 发送过来的 MemberLetterEvent
     * 会通过 MembersAdapter 获取应该要跳转到的位置，然后跳转
     */
    public void onEvent(MemberLetterEvent event) {
        Character targetChar = Character.toLowerCase(event.letter);
        if (mAdapter.getIndexMap().containsKey(targetChar)) {
            int index = mAdapter.getIndexMap().get(targetChar);
            if (index > 0 && index < mAdapter.getItemCount()) {
                mLayoutManager.scrollToPositionWithOffset(index, 0);
            }
        }
    }

}
