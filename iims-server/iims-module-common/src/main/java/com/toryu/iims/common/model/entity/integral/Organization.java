package com.toryu.iims.common.model.entity.integral;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.toryu.iims.common.model.entity.base.BaseTable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Author: Aitenry
 * @Date: 2025/11/15 22:59
 * @Version: v1.0.0
 * @Description: 组织表实体
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class Organization extends BaseTable implements Serializable {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long id;

    /**
     * 父级ID
     */
    private Long parentId;

    private Long jobId;

    /**
     * 名称
     */
    private String name;

    /**
     * 类型：0 公司、1 部门、2 职位
     */
    private Integer type;

    /**
     * 描述
     */
    private String description;

    private String code;

    /**
     * 是否删除：0 存在、1 删除
     */
    private Integer isDeleted;

}
