package com.abooc.test.data;

/**
 * @author zhangjunpu
 * @date 2016/12/6
 */

public class LiveRoom {


    /**
     * roomId : liveroom001
     * title : 聊天室001
     * creatId : 张三
     * tr : true
     * conversationId : 58468c782f301e005c19af3d
     */

    private String roomId;
    private String title;
    private String creatId;
    private boolean tr;
    private String conversationId;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreatId() {
        return creatId;
    }

    public void setCreatId(String creatId) {
        this.creatId = creatId;
    }

    public boolean isTr() {
        return tr;
    }

    public void setTr(boolean tr) {
        this.tr = tr;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

}
