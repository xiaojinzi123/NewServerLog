package com.xiaojinzi.socket;


import com.google.gson.Gson;
import com.xiaojinzi.NetworkLog;
import com.xiaojinzi.NetworkProvider;
import com.xiaojinzi.bean.Message;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@ServerEndpoint("/networkProvide")
public class WebSocketProvider implements NetworkProvider {

    private static AtomicInteger counter = new AtomicInteger();

    // 不重复并且剪短
    private final String TAG = "device_" + counter.incrementAndGet();

    private Gson gson = new Gson();

    /**
     * 所有的回话
     */
    private static Vector<Session> sessions = new Vector<>();

    private String deviceName = "unKnow";

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
    @OnMessage
    public void onMessage(Session session, String message) {
        try {
            Message messageBean = gson.fromJson(message, Message.class);
            // 如果是设置名字
            if (Message.DEVICE_NAME_FLAG.equals(messageBean.getAction())) {
                deviceName = messageBean.getData().toString();
                NetworkLog.getInstance().sendDevicesInfoToConsumer();
            } else {
                NetworkLog.getInstance().sendNetworkLog(messageBean);
            }
        } catch (Exception ignore) {
            // ignore
        }
    }

    /**
     * 当前的会话
     */
    private Session mSession;

    /**
     * 连接建立成功调用的方法
     *
     * @param session 可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen(Session session) {
        mSession = session;
        sessions.add(session);
        Message message = Message.tagBuild(TAG);
        try {
            synchronized (WebSocketProvider.class) {
                session.getBasicRemote().sendText(gson.toJson(message));
                NetworkLog.getInstance().addProvider(this);
            }
        } catch (IOException e) {
            destroy();
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public synchronized void onClose() {
        if (mSession != null) {
            sessions.remove(mSession);
        }
        NetworkLog.getInstance().removeProvider(this);
    }

    @Override
    public String getDeviceName() {
        return deviceName;
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getCombineDeviceName() {
        return getTag() + "_" + getDeviceName();
    }

    @Override
    public synchronized void send(String data) {
        if (mSession == null) {
            return;
        }
        try {
            mSession.getBasicRemote().sendText(data);
        } catch (Exception e) {
            destroy();
        }
    }

    private void destroy() {
        if (mSession != null) {
            try {
                mSession.close();
            } catch (Exception ignore) {
                // ignore
            }
            sessions.remove(mSession);
        }
        NetworkLog.getInstance().removeProvider(this);
    }

}
