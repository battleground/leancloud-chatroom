package com.leancloud.im.guide.event;

import android.text.TextUtils;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;

import java.util.List;

/**
 * AVIMConversationEventHandler 传递Event
 * @author zhangjunpu
 * @date 2016/12/7
 */

public class ConversationStatusEvent {

    public EventAction action;
    public AVIMClient client;
    public AVIMConversation conversation;
    public List<String>  members;
    public String operator;

    public ConversationStatusEvent(EventAction action, AVIMClient client, AVIMConversation conversation, String operator) {
        this.action = action;
        this.client = client;
        this.conversation = conversation;
        this.operator = operator;
    }

    public ConversationStatusEvent(EventAction action, AVIMClient client, AVIMConversation conversation, List<String> members, String operator) {
        this.action = action;
        this.client = client;
        this.conversation = conversation;
        this.members = members;
        this.operator = operator;
    }

    public boolean isOK(AVIMConversation conversation) {
        if (this.conversation == null || conversation == null)
            return false;
        return TextUtils.equals(conversation.getConversationId(), conversation.getConversationId());
    }

    public enum EventAction {
        JOIN("加入了聊天室"),
        QUIT("退出了聊天室"),
        ADD("你被邀请加入聊天室"),
        REMOVE("你被踢出聊天室");

        String value;

        EventAction(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
