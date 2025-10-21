package com.toryu.iims.ai.chat.model.entity;

import com.toryu.iims.common.model.entity.base.BaseTable;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * iims_aigc_topic 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AiChatTopic extends BaseTable implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private Integer top;

    private String title;

    private Boolean isDeleted;

}