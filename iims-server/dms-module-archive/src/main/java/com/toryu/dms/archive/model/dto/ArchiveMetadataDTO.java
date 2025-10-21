package com.toryu.dms.archive.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class ArchiveMetadataDTO {

    @ApiModelProperty("档号")
    private String archivalCode;

    @ApiModelProperty("题名")
    private String archivalTitle;

    @ApiModelProperty("年度")
    private String archivalYear;

    @ApiModelProperty("密级")
    private String archivalLevel;

    @ApiModelProperty("责任人")
    private String archivalResponsible;

    @ApiModelProperty("档案日期")
    private Timestamp archivalDate;

    @ApiModelProperty("保管期限")
    private String archivalDeadline;

    @ApiModelProperty("档案摘要")
    private String archivalAbstract;

    @ApiModelProperty("不公共属性")
    private String metadataProperty;

}
