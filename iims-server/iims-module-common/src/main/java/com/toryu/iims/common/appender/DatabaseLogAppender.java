package com.toryu.iims.common.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.toryu.iims.common.utils.LogDatabaseUtil;
import org.springframework.stereotype.Component;

@Component
public class DatabaseLogAppender extends AppenderBase<ILoggingEvent> {

    public DatabaseLogAppender() {
        super();
    }

    @Override
    protected void append(ILoggingEvent event) {
        LogDatabaseUtil.saveLog(
                String.valueOf(event.getLevel()),
                event.getFormattedMessage(),
                event.getLoggerName(),
                event.getThreadName()
        );
    }
}