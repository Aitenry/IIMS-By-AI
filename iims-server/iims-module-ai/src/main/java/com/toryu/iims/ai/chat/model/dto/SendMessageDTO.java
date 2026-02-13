package com.toryu.iims.ai.chat.model.dto;

import com.toryu.iims.common.enums.AiApiType;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SendMessageDTO implements Serializable {

    private Long topicId;

    private Long lastId;

    private Long modelId;

    private AiApiType apiType;

    private List<Long> wikiIds;

    private Long fileId;

    private String question;

}
