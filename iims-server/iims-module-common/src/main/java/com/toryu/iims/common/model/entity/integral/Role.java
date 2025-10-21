package com.toryu.iims.common.model.entity.integral;

import com.toryu.iims.common.model.entity.base.BaseTable;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * @TableName tb_role
 */
@Data
@Builder /*@Builder可以让你类链式的调用你的代码，来初始化你的实例对象*/
@NoArgsConstructor /*注解在类上；为类提供一个无参的构造方法*/
@AllArgsConstructor /*注解在类上；为类提供一个全参的构造方法*/
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseTable implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String roleName;

    private String roleEn;

    private String menus;

    private String info;

    private Integer systemic;

}