package com.toryu.dms.archive.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ArchiveMenuPageQueryDTO implements Serializable {

    @ApiModelProperty("目录ID")
    private Long id;

    @ApiModelProperty("档号")
    private String archivalCode;

    @ApiModelProperty("题名")
    private String archivalTitle;

    @ApiModelProperty("责任人")
    private String archivalResponsible;

    @ApiModelProperty("年度")
    private Integer archivalYear;

    @ApiModelProperty("页码")
    private int page;

    @ApiModelProperty("每页显示记录数")
    private int pageSize;
}
