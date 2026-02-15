package com.toryu.iims.ai.chat.model.dto;

import com.toryu.iims.common.enums.AiApiType;
import com.toryu.iims.common.enums.AiModelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Aitenry
 * @Date: 2026/2/14 16:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateModelDTO {

    private Long id;

    private String name;

    private String rename;

    private AiApiType type;

    private AiModelType modelType;

    private String url;

    private String key;

    private String token;

    private String description;

}
