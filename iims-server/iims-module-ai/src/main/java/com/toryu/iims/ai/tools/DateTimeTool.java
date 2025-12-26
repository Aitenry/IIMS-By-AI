package com.toryu.iims.ai.tools;

import com.toryu.iims.ai.tools.factory.AITool;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class DateTimeTool implements AITool {

    @Override
    public Object getToolInstance() {
        return this;
    }

    @Override
    public String getToolName() {
        return "datetime";
    }

    @Tool(description = "获取当前时间戳")
    public String getCurrentTime() {
        return String.valueOf(System.currentTimeMillis());
    }

    @Tool(description = "格式化指定时间")
    public String formatDateTime(@ToolParam(description = "时间戳") long timestamp,
                                @ToolParam(description = "格式化模式，如yyyy-MM-dd HH:mm:ss") String pattern) {
        try {
            LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
            return dateTime.format(DateTimeFormatter.ofPattern(pattern));
        } catch (Exception e) {
            return "时间格式错误: " + e.getMessage();
        }
    }
}