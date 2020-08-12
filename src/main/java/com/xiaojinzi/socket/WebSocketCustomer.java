package com.xiaojinzi.socket;


import com.xiaojinzi.NetworkCustomer;
import com.xiaojinzi.NetworkLog;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Vector;

@Component
@ServerEndpoint("/networkConsume")
public class WebSocketCustomer implements NetworkCustomer {

    /**
     * 所有的回话
     */
    private static Vector<Session> sessions = new Vector<>();

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
    @OnMessage
    public void onMessage(Session session, String message) {
        // empty
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
        NetworkLog.getInstance().addCustomer(this);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public synchronized void onClose() {
        destroy();
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

    private void destroy(){
        NetworkLog.getInstance().removeCustomer(this);
        if (mSession != null) {
            sessions.remove(mSession);
            try {
                mSession.close();
            } catch (Exception ignore) {
                // ignore
            }
        }
    }

}
