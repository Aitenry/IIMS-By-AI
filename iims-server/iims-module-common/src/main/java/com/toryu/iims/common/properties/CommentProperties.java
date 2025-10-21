package com.toryu.iims.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "iims.setting.comment")
public class CommentProperties {

    private Boolean examine;

    private Boolean sensitive;

}