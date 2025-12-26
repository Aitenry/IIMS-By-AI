package com.toryu.iims.ai.chat.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SendMessageDTO implements Serializable {

    private Long topicId;

    private Long lastId;

    private Boolean isUseAgent;

    private List<Long> wikiIds;

    private Long fileId;

    private String question;

}
