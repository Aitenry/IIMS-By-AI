package com.toryu.iims.auth.config;

import com.toryu.iims.common.utils.SnowFlakeIdWorker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SnowFlakeConfig {

    @Bean
    public SnowFlakeIdWorker adminSnowFlakeIdWorker() {
        return new SnowFlakeIdWorker(1, 1);
    }

    @Bean
    public SnowFlakeIdWorker archiveSnowFlakeIdWorker() {
        return new SnowFlakeIdWorker(2, 2);
    }

    @Bean
    public SnowFlakeIdWorker aiSnowFlakeIdWorker() {
        return new SnowFlakeIdWorker(3, 3);
    }

    @Bean
    public SnowFlakeIdWorker commonSnowFlakeIdWorker() {
        return new SnowFlakeIdWorker(6, 6);
    }
}