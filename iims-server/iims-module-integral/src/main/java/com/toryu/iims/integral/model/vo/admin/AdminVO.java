package com.toryu.iims.integral.model.vo.admin;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "管理员登录返回的数据格式")
@Builder
public class AdminVO implements Serializable {
    //主键
    @ApiModelProperty("用户ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long id;

    //真实姓名
    @ApiModelProperty("真实姓名")
    private String name;

    //用户名
    @ApiModelProperty("用户名")
    private String username;

    //用户邮箱
    @ApiModelProperty("用户邮箱")
    private String email;

    //手机号
    @ApiModelProperty("手机号")
    private String phone;

    //性别
    @ApiModelProperty("性别")
    private String sex;

    @ApiModelProperty("账户状态（1 禁用、0 正常）")
    private Boolean isDisable;

    //头像
    @ApiModelProperty("头像：文件ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long avatar;

    @ApiModelProperty("头像地址")
    private String imageUrl;

    //令牌
    @ApiModelProperty("令牌")
    private String token;

    //超级管理员
    @ApiModelProperty("超级管理员")
    private Integer root;

    //权限集合
    @ApiModelProperty("权限集合")
    private List<String> permissions;

    //角色集合
    @ApiModelProperty("角色集合")
    private List<String> roles;

    @ApiModelProperty("角色")
    private String role;

    //创建时间
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    //创建人ID
    @ApiModelProperty("创建人ID")
    private Long createBy;
}
