package com.toryu.iims.ai.chat.model.entity;

import com.toryu.iims.ai.rag.enums.ModelTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatApi {

    private ModelTypeEnum type;

    private String key;

    private String url;

    private Integer token;

    private String name;

}
