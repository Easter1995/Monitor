package com.glimmer.webSocket;

import com.alibaba.fastjson.JSONObject;
import com.glimmer.config.WebSocketConfiguration;
import com.glimmer.vo.GetAlertVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/*
AlertWebsocketServer 使用websocket来实现报警信息的实时推送，这里是使用spring-boot-starter-websocket中集成的webSocket
通过注入AlertWebsocketServer类从AlertTransactionServiceImpl中获取报警信息，并且这里保持和每个客户端的连接，然后将报警信息推送给每个客户端
 */
@Slf4j//使用log日志的功能
@Service
@Component
@ServerEndpoint(value = "/ws/alert" , configurator = WebSocketConfiguration.class , encoders = WebSocketCustomEncoding.class)
public class AlertWebsocketServer {
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static AtomicInteger onlineCount = new AtomicInteger(0);

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<AlertWebsocketServer> webSocketSet = new CopyOnWriteArraySet<AlertWebsocketServer>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;


    /**
     * 用来实现服务器主动向客户端推送
     * 发送文本信息
     * @param message
     * @throws IOException
     */
    public void sendMessageText(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    /**
     * 发送json格式的信息
     * @param getAlertVO
     * @throws IOException
     * @throws EncodeException
     */
    public void sendMessageObj(GetAlertVO getAlertVO) throws IOException, EncodeException {
        // 将 Java 对象转成 JSON 字符串
        this.session.getBasicRemote().sendObject(getAlertVO);
    }


    /**
     * 收到一个客户端发过来的消息调用的方法
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message,Session session) throws IOException {
        log.info("收到一条新的消息" + message);
        for (AlertWebsocketServer item : webSocketSet) {
            item.sendMessageText("收到一条新的消息:" + message);
        }
    }


    /**
     * 连接建立成功调用的方法
     * 获取session
     */
    @OnOpen
    public void onOpen(Session session,EndpointConfig config) {
        HttpSession httpSession = (HttpSession)config.getUserProperties().get(HttpSession.class.getName());
        String httpSessionId = httpSession.getId();
        this.session = session;
        webSocketSet.add(this);
        onlineCountAdd();
        log.info("有新连接加入");
        try {
            log.info("当前在线人数为:" + getOnlineCount());
            sendMessageText("连接成功");
        } catch (IOException e) {
            log.error("连接失败 WebSocket IO异常");
        }
    }


    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);//把当前用户从set中删除
        onlineCountSub();
        log.info("有一连接关闭");
        log.info("当前在线人数为:" + getOnlineCount());
    }


    /**
     * 发生错误
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session,Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }


    /**
     * 把新的报警信息发送给所有客户端(广播)
     * @param getAlertVO
     */
    public void sendInfo(GetAlertVO getAlertVO) {
        log.info("发送新的报警信息");
        for (AlertWebsocketServer item : webSocketSet) {
            try {
                item.sendMessageText("收到一条新的报警信息:");
                item.sendMessageObj(getAlertVO);
            }
            catch (EncodeException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                continue;
            }
        }
    }


    /**
     * 发送文本信息(广播)
     * @param message
     */
    public void sendText(String message) {
        log.info("发送消息:");
        for (AlertWebsocketServer item : webSocketSet) {
            try {
                item.sendMessageText(message);
            } catch (IOException e) {
               continue;
            }
        }
    }


    /**
     * 获取当前在线人数
     * @return
     */
    public static int getOnlineCount() {
        return onlineCount.get();
    }


    /**
     * 在线人数+1
     */
    public static void onlineCountAdd() {
        onlineCount.getAndIncrement();
    }


    public static void onlineCountSub() {
        onlineCount.getAndDecrement();
    }

}
