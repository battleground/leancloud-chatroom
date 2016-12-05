package com.leancloud.im.guide.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.abooc.util.Debug;
import com.abooc.widget.Toast;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.leancloud.im.guide.AVImClientManager;
import com.leancloud.im.guide.Constants;
import com.leancloud.im.guide.R;
import com.leancloud.im.guide.event.LeftChatItemClickEvent;
import com.leancloud.im.guide.fragment.ChatFragment;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wli on 15/8/13.
 * 广场页面，即群组聊天页面
 * <p>
 * 1、根据 clientId 获得 AVIMClient 实例
 * 2、根据 conversationId 获得 AVIMConversation 实例
 * 3、必须要加入 conversation 后才能拉取消息
 */
public class AVSquareActivity extends AVBaseActivity {

    private AVIMConversation squareConversation;
    private ChatFragment chatFragment;
    private Toolbar toolbar;


    public static void launch(Activity activity, String conversationId, String name) {
        Intent intent = new Intent(activity, AVSquareActivity.class);
        intent.putExtra(Constants.CONVERSATION_ID, conversationId);
        intent.putExtra(Constants.ACTIVITY_TITLE, name);
        activity.startActivityForResult(intent, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square);

        String conversationId = getIntent().getStringExtra(Constants.CONVERSATION_ID);
        String title = getIntent().getStringExtra(Constants.ACTIVITY_TITLE);
        Debug.error(title);

        chatFragment = (ChatFragment) getFragmentManager().findFragmentById(R.id.fragment_chat);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.btn_navigation_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setTitle(title);

        getSquare(conversationId);
        queryInSquare(conversationId);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        Debug.anchor(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_square, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_square_members:
                String conversationId = getIntent().getStringExtra(Constants.CONVERSATION_ID);
                Intent intent = new Intent(this, AVSquareMembersActivity.class);
                intent.putExtra(Constants.CONVERSATION_ID, conversationId);
                startActivity(intent);
                break;
            case R.id.menu_square_quit:
                quit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 根据 conversationId 查取本地缓存中的 conversation，如若没有缓存，则返回一个新建的 conversaiton
     */
    private void getSquare(String conversationId) {
        if (TextUtils.isEmpty(conversationId)) {
            throw new IllegalArgumentException("conversationId can not be null");
        }

        AVIMClient client = AVImClientManager.getInstance().getClient();
        if (null != client) {
            squareConversation = client.getConversation(conversationId);
        } else {
            finish();
            showToast("Please call AVIMClient.open first!");
        }
    }

    /**
     * 加入 conversation
     */
    private void joinSquare() {
        squareConversation.join(new AVIMConversationCallback() {
            @Override
            public void done(AVIMException e) {
                if (filterException(e)) {
                    chatFragment.setConversation(squareConversation);
                }
            }
        });
    }

    /**
     * 先查询自己是否已经在该 conversation，如果存在则直接给 chatFragment 赋值，否则先加入，再赋值
     */
    private void queryInSquare(String conversationId) {
        final AVIMClient client = AVImClientManager.getInstance().getClient();
        AVIMConversationQuery conversationQuery = client.getQuery();
        conversationQuery.whereEqualTo("objectId", conversationId);
        conversationQuery.containsMembers(Arrays.asList(AVImClientManager.getInstance().getClientId()));
        conversationQuery.findInBackground(new AVIMConversationQueryCallback() {
            @Override
            public void done(List<AVIMConversation> list, AVIMException e) {
                if (filterException(e)) {
                    if (null != list && list.size() > 0) {
                        chatFragment.setConversation(list.get(0));
                    } else {
                        joinSquare();
                    }
                }
            }
        });
    }

    /**
     * 处理聊天 item 点击事件，点击后跳转到相应1对1的对话
     */
    public void onEvent(LeftChatItemClickEvent event) {
        Intent intent = new Intent(this, AVSingleChatActivity.class);
        intent.putExtra(Constants.MEMBER_ID, event.userId);
        startActivity(intent);
    }

    /**
     * 退出会话
     */
    public void quit() {
        if (squareConversation != null) {
            squareConversation.quit(new AVIMConversationCallback() {
                @Override
                public void done(AVIMException e) {
                    if (e == null) {
                        //退出成功
                        setResult(RESULT_OK);
                        AVSquareActivity.super.onBackPressed();
                    } else {
                        Toast.show("未退出会话！");
                    }
                }
            });
        }
    }
}
