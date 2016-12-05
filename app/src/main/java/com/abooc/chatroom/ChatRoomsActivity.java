package com.abooc.chatroom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.leancloud.im.guide.AVImClientManager;
import com.leancloud.im.guide.Constants;
import com.leancloud.im.guide.R;

/**
 * Created by dayu on 2016/12/5.
 */

public class ChatRoomsActivity extends BlankActivity {

    public static void launch(Context ctx, String memberId) {
        Intent intent = new Intent(ctx, ChatRoomsActivity.class);
        intent.putExtra(Constants.MEMBER_ID, memberId);
        ctx.startActivity(intent);
    }

    String memberId;
    ChatRoomListFragment fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        memberId = getIntent().getStringExtra(Constants.MEMBER_ID);

        AVImClientManager.getInstance().open(memberId, new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (e == null) {
                    getSupportActionBar().setSubtitle(memberId + " - " + "在线");
                } else {
                    getSupportActionBar().setSubtitle(memberId + " - " + "登录失败：" + e);
                }
            }
        });


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fragment = new ChatRoomListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.MEMBER_ID, memberId);
        fragment.setArguments(bundle);
        transaction.add(R.id.FrameLayout, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat_rooms, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_chat_rooms_refresh:
                fragment.clear();
                fragment.queryConversations(memberId);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            fragment.queryConversations(memberId);
        }

    }
}
