package com.toryu.iims.subscriber.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "iims.subscriber.sse")
public class SseProperties {

    private final long timeout = 3600_000;

    private final long retry = 3000;

}