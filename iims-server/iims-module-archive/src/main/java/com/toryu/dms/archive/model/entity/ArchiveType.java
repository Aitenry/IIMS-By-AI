package com.toryu.dms.archive.model.entity;

import com.toryu.iims.common.model.entity.base.BaseTable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Author: Aitenry
 * @Date: 2025/11/16 01:30
 * @Version: v1.0.0
 * @Description: 档案类型实体
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class ArchiveType extends BaseTable implements Serializable {

    private Long id;

    /**
     * 档案名称
     */
    private String name;

    /**
     * 新增档案页组件名
     */
    private String operateComponent;

    /**
     * 档案详情页组件名
     */
    private String detailComponent;

    /**
     * 全宗
     */
    private String code;

}