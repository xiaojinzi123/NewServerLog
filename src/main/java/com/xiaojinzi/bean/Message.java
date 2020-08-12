package com.xiaojinzi.bean;

public class Message<T> {

    /**
     * 设备列表 key
     */
    public static final String DEVICES_FLAG = "deviceList";

    /**
     * 设置设备名称的 key
     */
    public static final String DEVICE_NAME_FLAG = "deviceName";

    public static final String TAG_FLAG = "tag";

    public static final String HEARTBEAT_FLAG = "heartbeat";

    // 表示自身的 Tag
    private String selfTag;

    // 表示目标的 Tag
    private String targetTag;

    // 标记消息的类型
    private String action;

    // 真正发送出去的数据,最终会转化成 json 数据传出去
    private T data;

    public Message() {
    }

    public Message(String selfTag, String action, T data) {
        this.selfTag = selfTag;
        this.action = action;
        this.data = data;
    }

    public static Message tagBuild(String tag) {
        Message result = new Message();
        result.setAction(TAG_FLAG);
        result.setData(tag);
        return result;
    }

    public static Message heartbeatBuild() {
        Message result = new Message();
        result.setAction(HEARTBEAT_FLAG);
        return result;
    }

    public String getSelfTag() {
        return selfTag;
    }

    public void setSelfTag(String selfTag) {
        this.selfTag = selfTag;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}