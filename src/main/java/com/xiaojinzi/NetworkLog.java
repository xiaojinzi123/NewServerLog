package com.xiaojinzi;

import com.google.gson.Gson;
import com.xiaojinzi.bean.Message;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

/**
 * 核心类
 */
public class NetworkLog implements Runnable {

    private static NetworkLog instance = new NetworkLog();

    private final Gson g = new Gson();

    private NetworkLog() {
        // 心跳数据
        new Thread(this).start();
    }

    /**
     * 用于发送心跳包
     */
    @Override
    public void run() {
        executorService.submit((Runnable) () -> {
            Message message = Message.heartbeatBuild();
            String heartbeat = g.toJson(message);
            while (true) {
                try {
                    Thread.sleep(5000);
                    for (NetworkProvider provider : providers) {
                        provider.send(heartbeat);
                    }
                    for (NetworkCustomer consumer : consumers) {
                        consumer.send(heartbeat);
                    }
                } catch (Exception ignore) {
                    // ignore
                }
            }
        });
    }

    /**
     * 获取实例对象
     */
    public static NetworkLog getInstance() {
        return instance;
    }

    private List<NetworkProvider> providers = Collections.synchronizedList(new LinkedList<>());
    private List<NetworkCustomer> consumers = Collections.synchronizedList(new LinkedList<>());

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

    /**
     * 发送设备信息给消费者
     */
    public void sendDevicesInfoToConsumer() {
        executorService.submit(() -> {
            List<String> list = providers.stream()
                    // 转化为设备名称, 网页根据 deviceName 显示信息的
                    .map(obj -> obj.getDeviceName())
                    .collect(Collectors.toList());
            Message message = new Message();
            message.setAction(Message.DEVICES_FLAG);
            message.setData(list);
            sendNetworkLog(message);
        });
    }

    public void addProvider(NetworkProvider networkProvider) {
        providers.add(networkProvider);
        sendDevicesInfoToConsumer();
    }

    public void removeProvider(NetworkProvider networkProvider) {
        providers.remove(networkProvider);
        sendDevicesInfoToConsumer();
    }

    public void addCustomer(NetworkCustomer networkCustomer) {
        consumers.add(networkCustomer);
        sendDevicesInfoToConsumer();
    }

    public void sendNetworkLog(Message message) {
        executorService.submit(() -> {
            for (NetworkCustomer networkCustomer : consumers) {
                networkCustomer.send(g.toJson(message));
            }
        });
    }

    public void removeCustomer(NetworkCustomer networkCustomer) {
        consumers.remove(networkCustomer);
    }

    public String getDeviceName(String tag) {
        if (tag == null || tag.length() == 0) {
            return null;
        }
        for (NetworkProvider provider : providers) {
            if (tag.equals(provider.getTag())) {
                return provider.getDeviceName();
            }
        }
        return null;
    }

}
