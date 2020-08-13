package com.xiaojinzi;

import com.google.gson.Gson;
import com.xiaojinzi.anno.NotEmpty;
import com.xiaojinzi.anno.NotNull;
import com.xiaojinzi.anno.ThreadSafe;
import com.xiaojinzi.anno.ThreadUnSafe;
import com.xiaojinzi.bean.Message;
import com.xiaojinzi.util.Strings;
import org.json.JSONObject;

import javax.json.Json;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Server {

    public static final String TAG = "Server";

    private static Server instance = new Server();

    private final Gson g = new Gson();

    @ThreadSafe
    private final CopyOnWriteArrayList<Client> clientList = new CopyOnWriteArrayList<>();

    @ThreadSafe
    private final List<String> messageQueue = Collections.synchronizedList(new LinkedList());

    private Server() {
    }

    @ThreadSafe
    public static Server getInstance() {
        return instance;
    }

    /**
     * 转发消息
     */
    @ThreadUnSafe
    public boolean forward(@NotEmpty String message) {

        JSONObject jb = new JSONObject(message);
        String type = jb.optString(Message.ATTR_TYPE);
        String selfTag = jb.optString(Message.ATTR_SELF_TAG);
        // 如果消息的必须的参数不足, 则被忽略
        if (Strings.isEmpty(type) || Strings.isEmpty(selfTag)) {
            return false;
        }
        // 如果是心跳包, 则忽略
        if (Message.TYPE_HEARTBEAT.equals(type)) {
            return false;
        }

        messageQueue.add(message);
        // 转发给感兴趣的 Client
        return true;

    }

    @ThreadUnSafe
    private void doForward(@NotEmpty String message) {
        JSONObject jb = new JSONObject(message);
        String type = jb.optString(Message.ATTR_TYPE);
        List<Client> clients = filterClientBySubscribeType(type);
        clients.forEach(client -> {
            client.send(message);
        });
    }

    /**
     * 过滤出想要某个数据类型的所有的 Client
     */
    @NotEmpty
    @ThreadSafe
    private synchronized List<Client> filterClientBySubscribeType(@NotEmpty String type) {
        return clientList.stream()
                .filter(client -> client.isSubscribe(type))
                .collect(Collectors.toList());
    }

    /**
     * 添加了一个 Client
     */
    @ThreadSafe
    public synchronized void addClient(@NotNull Client client) {
        if (!clientList.contains(client)) {
            clientList.add(client);
        }
    }

    /**
     * 每一个 Client 都可能有感兴趣的数据类型和想要订阅的数据类型
     * 这个方法会把 Client 感兴趣的数据类型的提供者的所有 Client 的信息给发送过去
     */
    @ThreadSafe
    public synchronized void sendClientInfo() {

        // key 为某一个数据类型的提供, 比如 network
        // value 为提供 key 这种数据类型的 Client 的信息
        Map<String, List<Client.Info>> map = new HashMap<>();
        clientList.forEach(client -> {
            Set<String> providerTypes = client.getProviderTypes();
            for (String providerType : providerTypes) {
                List<Client.Info> clients = map.getOrDefault(providerType, new ArrayList<>());
                clients.add(client.toInfo());
                map.put(providerType, clients);
            }
        });

        Set<Map.Entry<String, List<Client.Info>>> entries = map.entrySet();

        for (Map.Entry<String, List<Client.Info>> entry : entries) {
            Message message = new Message();
            message.setType(Message.TYPE_PROVIDER_LIST + Message.AI_TE + entry.getKey());
            message.setSelfTag(TAG);
            message.setData(entry.getValue());
            messageQueue.add(g.toJson(message));
        }

    }

    /**
     * 移除了一个 Client
     */
    @ThreadSafe
    public synchronized void removeClient(@NotNull Client client) {
        clientList.remove(client);
    }

}
