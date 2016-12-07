package com.leancloud.im.guide;

import android.text.TextUtils;

import com.abooc.util.Debug;
import com.abooc.widget.Toast;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationEventHandler;
import com.leancloud.im.guide.event.ConversationStatusEvent;
import com.leancloud.im.guide.event.ConversationStatusEvent.EventAction;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * @author zhangjunpu
 * @date 2016/12/7
 */

public class ConversationEventHandler extends AVIMConversationEventHandler {
    @Override
    public void onMemberLeft(AVIMClient client, AVIMConversation conversation, List<String> members, String invitedBy) {
        // 有其他成员离开时，执行此处逻辑
        Debug.out("---------有成员离开-----------");
        Debug.out(client.getClientId() + ", " + members + " 离开了 " + conversation.getConversationId() + "；操作者为：" + invitedBy);

        if (!TextUtils.equals(client.getClientId(), AVIMClientManager.getInstance().getClientId())) return;
        EventBus.getDefault().post(new ConversationStatusEvent(EventAction.QUIT, client, conversation, members, invitedBy));
    }

    @Override
    public void onMemberJoined(AVIMClient client, AVIMConversation conversation, List<String> members, String invitedBy) {
        // 手机屏幕上会显示一小段文字：Tom 加入到 551260efe4b01608686c3e0f ；操作者为：Tom
        Debug.out("---------有成员加入-----------");
        Debug.out(client.getClientId() + ", " + members + " 加入到 " + conversation.getConversationId() + "；操作者为： " + invitedBy);

        if (!TextUtils.equals(client.getClientId(), AVIMClientManager.getInstance().getClientId())) return;
        EventBus.getDefault().post(new ConversationStatusEvent(EventAction.JOIN, client, conversation, members, invitedBy));
    }

    @Override
    public void onKicked(AVIMClient client, AVIMConversation conversation, String kickedBy) {
        // 当前 ClientId(Bob) 被踢出对话，执行此处逻辑
        Debug.out("---------你被踢出-----------");
        Debug.out(client.getClientId() + ", " + " 被踢出 " + conversation.getConversationId() + "；操作者为：" + kickedBy);

        EventBus.getDefault().post(new ConversationStatusEvent(EventAction.REMOVE, client, conversation, kickedBy));
        Toast.show(EventAction.REMOVE.getValue());
    }

    @Override
    public void onInvited(AVIMClient client, AVIMConversation conversation, String invitedBy) {
        // 当前 ClientId(Bob) 被邀请到对话，执行此处逻辑
        Debug.out("---------你被邀请-----------");
        Debug.out(client.getClientId() + ", " + " 被邀请到 " + conversation.getConversationId() + "；操作者为：" + invitedBy);

        EventBus.getDefault().post(new ConversationStatusEvent(EventAction.ADD, client, conversation, invitedBy));
        Toast.show(EventAction.ADD.getValue());
    }
}
