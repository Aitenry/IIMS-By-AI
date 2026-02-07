package com.toryu.iims.subscriber.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SseMessage implements Serializable {
    private String id;
    private String event;
    private Object data;
    private Long retry;
    private Long timestamp;

    public SseMessage(String event, Object data) {
        this.event = event;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
        this.id = java.util.UUID.randomUUID().toString();
    }
}