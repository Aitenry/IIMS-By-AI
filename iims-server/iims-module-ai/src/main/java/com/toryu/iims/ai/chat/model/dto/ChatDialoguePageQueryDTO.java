package com.toryu.iims.ai.chat.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ChatDialoguePageQueryDTO implements Serializable {

    @ApiModelProperty("对话话题ID")
    private Long topicId;

    @ApiModelProperty("页码")
    private int page;

    @ApiModelProperty("每页显示记录数")
    private int pageSize;

}
