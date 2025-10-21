package com.toryu.iims.common.utils;

import com.toryu.iims.common.mapper.LogsMapper;
import com.toryu.iims.common.model.entity.log.Logs;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LogDatabaseUtil implements ApplicationContextAware, ApplicationListener<ContextClosedEvent> {

    private static LogsMapper logsMapper;
    private static SnowFlakeIdWorker commonSnowFlakeIdWorker;
    private static volatile boolean isApplicationClosing = false;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext context) throws BeansException {
        try {
            logsMapper = context.getBean(LogsMapper.class);
            commonSnowFlakeIdWorker = context.getBean("commonSnowFlakeIdWorker", SnowFlakeIdWorker.class);
        } catch (Exception e) {
            System.err.println("Failed to initialize LogDatabaseUtil: " + e.getMessage());
        }
    }

    @Override
    public void onApplicationEvent(@NotNull ContextClosedEvent event) {
        isApplicationClosing = true;
    }

    public static void saveLog(String level, String message, String loggerName, String threadName) {
        if (isApplicationClosing) {
            return;
        }

        if (logsMapper == null || commonSnowFlakeIdWorker == null) {
            return;
        }

        Logs log = Logs.builder()
                .id(commonSnowFlakeIdWorker.nextId())
                .level(level)
                .message(message)
                .loggerName(loggerName)
                .threadName(threadName)
                .createTime(LocalDateTime.now())
                .build();
        logsMapper.insert(log);
    }
}