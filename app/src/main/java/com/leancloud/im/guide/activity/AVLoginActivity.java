package com.leancloud.im.guide.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.abooc.test.data.User;
import com.abooc.test.data.UserLoader;
import com.abooc.util.Debug;
import com.abooc.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.leancloud.im.guide.AVIMClientManager;
import com.leancloud.im.guide.ConversationEventHandler;
import com.leancloud.im.guide.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by wli on 15/8/13.
 * 登陆页面，暂时未做自动登陆，每次重新进入都要再登陆一次
 */
public class AVLoginActivity extends AVBaseActivity {

    /**
     * 此处 xml 里限制了长度为 30，汉字算一个
     */
    @Bind(R.id.activity_login_et_username)
    protected EditText userNameView;

    @Bind(R.id.activity_login_btn_login)
    protected Button loginButton;

    @Bind(R.id.activity_login_text_log)
    protected TextView logText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        /**
         * 调试获取用户列表
         */
        UserLoader.loadUsers(new UserLoader.OnGetUsersListener() {
            @Override
            public void onGet(ArrayList<User> users) {
//                Object toJSON = JSONArray.toJSON(users);
                String text = JSON.toJSONString(users);
                Debug.out("content:\n" + text);
                logText.setText(text);
            }
        });

    }

    @OnClick(R.id.activity_login_btn_login)
    public void onLoginClick(View view) {
        openClient(userNameView.getText().toString().trim());
    }

    private void openClient(String selfId) {
        if (TextUtils.isEmpty(selfId)) {
            showToast(R.string.login_null_name_tip);
            return;
        }

        loginButton.setEnabled(false);
        userNameView.setEnabled(false);
        AVIMClientManager.getInstance().open(selfId, new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                loginButton.setEnabled(true);
                userNameView.setEnabled(true);
                if (Debug.printStackTrace(e)) {
                    Toast.show("登录失败");
                    return;
                }
                AVIMMessageManager.setConversationEventHandler(new ConversationEventHandler());
                ChatRoomsActivity.launch(AVLoginActivity.this);
                finish();
            }
        });
    }
}
