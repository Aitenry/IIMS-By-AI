package com.toryu.iims.integral.model.dto.role;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "角色传递的数据模型")
public class RoleDTO implements Serializable {

    //主键
    @ApiModelProperty("角色ID")
    private Long id;

    @ApiModelProperty("角色名称")
    private String roleName;

    @ApiModelProperty("权限字符")
    private String roleEn;

    @ApiModelProperty("菜单id集合")
    private String menus;

    @ApiModelProperty("角色描述")
    private String info;

    private Integer systemic;
}
