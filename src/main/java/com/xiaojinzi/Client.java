package com.xiaojinzi;

import com.google.gson.Gson;
import com.xiaojinzi.anno.AnyThread;
import com.xiaojinzi.anno.NotEmpty;
import com.xiaojinzi.anno.NotNull;
import com.xiaojinzi.bean.Message;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * 表示一个客户端
 */
public abstract class Client {

    private final Gson g = new Gson();

    /**
     * 每一个 Client 的唯一标志
     */
    private final String UID = "Client_" + UUID.randomUUID().toString();

    /**
     * 提供的数据类型
     */
    private final Set<String> providerTypes = new HashSet<>();

    /**
     * 订阅的数据类型
     */
    private final Set<String> subscribeTypes = new HashSet<>();

    /**
     * Client 的名称
     */
    private String name = "UnKnow";

    @NotNull
    public Set<String> getProviderTypes() {
        return providerTypes;
    }

    @NotNull
    public Set<String> getSubscribeTypes() {
        return subscribeTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return UID.equals(client.UID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(UID);
    }

    /**
     * 获取唯一标志
     */
    @NotEmpty
    public String getUID() {
        return UID;
    }

    public String getName() {
        return name;
    }

    @AnyThread
    public abstract void send(@NotEmpty String message);

    /**
     * 接收到 Client 发送上来的消息, 让 Server 进行转发
     */
    protected void onAcceptMessage(@NotEmpty String message) {
        JSONObject jb = new JSONObject(message);
        // 获取类型
        String type = jb.getString(Message.ATTR_TYPE);
        // 如果是设置名称
        if (Message.TYPE_SET_CLIENT_NAME.equals(type)) {
            name = jb.getString(Message.ATTR_DATA);
        } else if (Message.TYPE_SET_PROVIDER_TYPES.equals(type)) {
            JSONArray jsonArray1 = jb.getJSONArray(Message.ATTR_DATA);
            int length = jsonArray1.length();
            providerTypes.clear();
            for (int i = 0; i < length; i++) {
                providerTypes.add(jsonArray1.getString(i));
            }
        }  else if (Message.TYPE_SET_SUBSCRIBE_TYPES.equals(type)) {
            JSONArray jsonArray2 = jb.getJSONArray(Message.ATTR_DATA);
            int length = jsonArray2.length();
            subscribeTypes.clear();
            for (int i = 0; i < length; i++) {
                providerTypes.add(jsonArray2.getString(i));
            }
        } else {
            Server.getInstance().forward(message);
        }
    }

    /**
     * 打开了一个 Client
     */
    protected void onOpenClient() {
        Server.getInstance().addClient(this);
        Message message = Message.clientTagMessage(getUID());
        send(g.toJson(message));
    }

    /**
     * 关闭了一个 Client
     */
    protected void onCloseClient() {
        Server.getInstance().removeClient(this);
    }

    public boolean isSubscribe(@NotEmpty String type) {
        return subscribeTypes.contains(type);
    }

    @NotNull
    public Info toInfo() {
        return new Info(getUID(), getName());
    }

    public static class Info {

        private String uid;

        private String name;

        public Info(String uid, String name) {
            this.uid = uid;
            this.name = name;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

}
