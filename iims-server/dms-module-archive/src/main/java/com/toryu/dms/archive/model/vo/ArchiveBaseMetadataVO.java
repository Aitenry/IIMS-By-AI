package com.toryu.dms.archive.model.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.toryu.iims.common.model.entity.base.BaseAdminInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "分页查询返回的数据格式")
@TableName(autoResultMap = true)
public class ArchiveBaseMetadataVO implements Serializable {

    @ApiModelProperty("档案ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty("档号")
    private String archivalCode;

    @ApiModelProperty("题名")
    private String archivalTitle;

    @ApiModelProperty("年度")
    private String archivalYear;

    @ApiModelProperty("密级")
    private String archivalLevel;

    @ApiModelProperty("责任人")
    private BaseAdminInfo archivalResponsible;

    @ApiModelProperty("档案日期")
    private Timestamp archivalDate;

    @ApiModelProperty("保管期限")
    private String archivalDeadline;
}
