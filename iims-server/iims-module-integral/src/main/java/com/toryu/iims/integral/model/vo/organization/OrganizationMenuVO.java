package com.toryu.iims.integral.model.vo.organization;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2025/11/15 23:45 (示例时间)
 * @Version: v1.0.0
 * @Description: 组织架构树形菜单视图对象
 */
@Data
public class OrganizationMenuVO {

    @ApiModelProperty("组织id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty("父级组织id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long parentId;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("类型（0 公司、1 部门、2 职位）")
    private Integer type;

    @ApiModelProperty("子组织集合")
    private List<OrganizationMenuVO> children;
}