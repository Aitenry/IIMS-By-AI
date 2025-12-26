package com.toryu.iims.ai.chat.model.entity;

import com.toryu.iims.common.model.entity.base.BaseTable;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AiChatDialogue extends BaseTable implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long lastId;

    private Long topicId;

    private String sender;

    private String content;

    private String metadata;

    private String tools;

    private List<Long> fileIds;

    private Boolean isDeleted;

    private Integer feedbackStatus;

    private Boolean isStar;

}
