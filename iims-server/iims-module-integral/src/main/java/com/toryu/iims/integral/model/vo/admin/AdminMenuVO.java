package com.toryu.iims.integral.model.vo.admin;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class AdminMenuVO {

    @ApiModelProperty("菜单id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty("父级菜单")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long parentId;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("显示顺序")
    private Integer orderNum;

    @ApiModelProperty("路径")
    private String path;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("组件")
    private String component;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("菜单类型（M目录 C菜单 F按钮）")
    private Character menuType;

    @ApiModelProperty("权限标识")
    private String perms;

    @ApiModelProperty("是否隐藏（0否，1是）")
    private Integer visible;

    @ApiModelProperty("是否为外链（0是 1否）")
    private Integer isFrame;

    @ApiModelProperty("重定向")
    private String redirect;

    @ApiModelProperty("子菜单集合")
    private List<AdminMenuVO> children;
}