package com.toryu.iims.subscriber.service.impl;

import com.toryu.iims.common.context.BaseContext;
import com.toryu.iims.subscriber.config.SseProperties;
import com.toryu.iims.subscriber.model.entity.SseMessage;
import com.toryu.iims.subscriber.service.SseNotificationService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseNotificationServiceImpl implements SseNotificationService {

    private final SseProperties sseProperties;
    private final Map<Long, SseEmitter> clients = new ConcurrentHashMap<>();

    @Override
    public SseEmitter registerClient() {
        Long clientId = BaseContext.getCurrentId();
        SseEmitter emitter = new SseEmitter(sseProperties.getTimeout());

        emitter.onCompletion(() -> removeClient(clientId, "completed"));
        emitter.onTimeout(() -> removeClient(clientId, "timeout"));
        emitter.onError(ex -> removeClient(clientId, "error: " + ex.getMessage()));

        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data(Map.of("clientId", clientId, "message", "Connected"))
                    .reconnectTime(sseProperties.getRetry()));
        } catch (IOException e) {
            log.warn("初始化事件发送失败: {}", clientId, e);
            emitter.complete();
            return registerClient();
        }

        clients.put(clientId, emitter);
        log.info("SSE客户端注册: {}, 当前连接数: {}", clientId, clients.size());
        return emitter;
    }

    @Override
    public void broadcast(SseMessage message) {
        sendToClients(new CopyOnWriteArrayList<>(clients.keySet()), message);
    }

    @Override
    public void sendToClient(Long clientId, SseMessage message) {
        sendToClients(Collections.singletonList(clientId), message);
    }

    @Override
    public int getActiveConnectionCount() {
        return clients.size();
    }

    @PreDestroy
    public void shutdown() {
        log.info("SSE服务关闭，清理 {} 个连接", clients.size());
        clients.keySet().forEach(id -> removeClient(id, "shutdown"));
    }

    private void sendToClients(List<Long> targetIds, SseMessage msg) {
        if (targetIds.isEmpty() || msg == null) return;

        targetIds.forEach(clientId -> {
            SseEmitter emitter = clients.get(clientId);
            if (emitter == null) {
                return; // 已被移除
            }

            try {
                emitter.send(SseEmitter.event()
                        .id(msg.getId())
                        .name(msg.getEvent() != null ? msg.getEvent() : "message")
                        .data(msg.getData())
                        .reconnectTime(msg.getRetry() != null ? msg.getRetry() : sseProperties.getRetry()));
            } catch (IOException e) {
                log.warn("SSE消息发送失败 [clientId={}]: {}", clientId, e.getMessage());
                removeClient(clientId, "send-failed");
            }
        });
    }

    @Override
    public void removeClient(Long clientId, String reason) {
        SseEmitter emitter = clients.remove(clientId);
        if (emitter != null) {
            try {
                emitter.complete();
            } catch (IllegalStateException ex) {
                log.warn("移除SSE失败 [clientId={}]: {}", clientId, ex.getMessage());
            }
            log.info("SSE客户端移除 [{}]: {}, 剩余连接数: {}", reason, clientId, clients.size());
        }
    }
}