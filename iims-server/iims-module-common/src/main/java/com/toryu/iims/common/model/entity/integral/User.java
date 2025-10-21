package com.toryu.iims.common.model.entity.integral;

import com.toryu.iims.common.model.entity.base.BaseTable;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户
 */
@Data
@Builder /*@Builder可以让你类链式的调用你的代码，来初始化你的实例对象*/
@NoArgsConstructor /*注解在类上；为类提供一个无参的构造方法*/
@AllArgsConstructor /*注解在类上；为类提供一个全参的构造方法*/
@EqualsAndHashCode(callSuper = true)
public class User extends BaseTable implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    //主键
    @ApiModelProperty("用户ID")
    private Long id;

    @ApiModelProperty("微信唯一标识")
    private String openid;

    //真实姓名
    @ApiModelProperty("真实姓名")
    private String name;

    //用户名
    @ApiModelProperty("用户名")
    private String username;

    //用户邮箱
    @ApiModelProperty("用户邮箱")
    private String email;

    //密码
    @ApiModelProperty("密码")
    private String password;

    //手机号
    @ApiModelProperty("手机号")
    private String phone;

    //性别
    @ApiModelProperty("性别")
    private String sex;

    //身份证号码
    @ApiModelProperty("身份证号码")
    private String idNumber;

    //账户状态（1正常0锁定）
    @ApiModelProperty("账户状态（1 禁用、0 正常）")
    private Boolean isDisable;

    @ApiModelProperty("账户状态（1 删除、0 正常）")
    private Boolean isDeleted;

    //在线状态（1在线 0离线）
    @ApiModelProperty("在线状态（1在线 0离线）")
    private Integer online;

    //头像
    @ApiModelProperty("头像")
    private String avatar;

}
