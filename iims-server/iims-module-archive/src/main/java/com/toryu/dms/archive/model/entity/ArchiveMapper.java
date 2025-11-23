package com.toryu.dms.archive.model.entity;

import com.toryu.iims.common.model.entity.base.BaseTable;
import lombok.*;

/**
 * @Author: Aitenry
 * @Date: 2025/11/16 12:56
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Data
@Builder /*@Builder可以让你类链式的调用你的代码，来初始化你的实例对象*/
@NoArgsConstructor /*注解在类上；为类提供一个无参的构造方法*/
@AllArgsConstructor /*注解在类上；为类提供一个全参的构造方法*/
@EqualsAndHashCode(callSuper = true)
public class ArchiveMapper extends BaseTable {

    private Long organizationFinalId;

    private String organizationIds;

    private String archiveTypeIds;

}
