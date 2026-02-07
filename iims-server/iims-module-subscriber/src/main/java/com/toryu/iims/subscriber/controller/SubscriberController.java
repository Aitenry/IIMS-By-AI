package com.toryu.iims.subscriber.controller;

import com.toryu.iims.subscriber.service.SseNotificationService;
import com.toryu.iims.subscriber.service.impl.SseNotificationServiceImpl;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @Author: Aitenry
 * @Date: 2026/2/5 21:12
 * @Version: v1.0.0
 * @Description: TODO
 **/
@RestController
@RequestMapping("/iims/subscriber")
public class SubscriberController {

    private final SseNotificationService notificationService;

    public SubscriberController(SseNotificationServiceImpl notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect() {
        return notificationService.registerClient();
    }

    @GetMapping("/completed/{id}")
    public void completed(@PathVariable Long id) {
        notificationService.removeClient(id, "completed");
    }

}
