package com.toryu.iims.ai.chat.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ChatTopicPageQueryDTO implements Serializable {

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("页码")
    private int page;

    @ApiModelProperty("每页显示记录数")
    private int pageSize;

}
