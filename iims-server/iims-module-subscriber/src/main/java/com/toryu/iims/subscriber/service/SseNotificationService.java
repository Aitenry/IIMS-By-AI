package com.toryu.iims.subscriber.service;

import com.toryu.iims.subscriber.model.entity.SseMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @Author: Aitenry
 * @Date: 2026/2/5 21:19
 * @Version: v1.0.0
 * @Description: TODO
 **/
public interface SseNotificationService {

    SseEmitter registerClient();

    /**
     * 全局广播消息（任意地方调用）
     */
    void broadcast(SseMessage message);

    /**
     * 定向推送（按 clientId）
     */
    void sendToClient(Long clientId, SseMessage message);

    void removeClient(Long clientId, String reason);

    /**
     * 获取当前活跃连接数（用于监控）
     */
    int getActiveConnectionCount();

}
