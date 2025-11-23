package com.toryu.dms.archive.model.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArchiveMenuVO {

    @ApiModelProperty("菜单ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty("父级菜单ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long parentId;

    @ApiModelProperty("类目图标")
    private Integer type;

    @ApiModelProperty("类目名称")
    private String label;

    @ApiModelProperty("类目缩写编码")
    private String labelCode;

    @ApiModelProperty("档案类型名")
    private String typeLabel;

    @ApiModelProperty("操作档案页组件名")
    private String operateComponent;

    @ApiModelProperty("档案详情页组件名")
    private String detailComponent;

    @ApiModelProperty("子菜单集合")
    private List<ArchiveMenuVO> children;

}
