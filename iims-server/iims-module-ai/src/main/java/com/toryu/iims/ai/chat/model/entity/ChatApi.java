package com.toryu.iims.ai.chat.model.entity;

import com.toryu.iims.common.enums.AiModelApiType;
import com.toryu.iims.common.enums.AiModelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatApi {

    private String key;

    private String url;

    private Integer token;

    private String name;

    private AiModelApiType type;

    private AiModelType modelType;

}
