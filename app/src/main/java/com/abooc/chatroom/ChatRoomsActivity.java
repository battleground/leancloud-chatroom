package com.abooc.chatroom;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String memberId = getIntent().getStringExtra(Constants.MEMBER_ID);

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
        ChatRoomListFragment fragment = new ChatRoomListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.MEMBER_ID, memberId);
        fragment.setArguments(bundle);
        transaction.add(R.id.FrameLayout, fragment).commit();
    }
}
