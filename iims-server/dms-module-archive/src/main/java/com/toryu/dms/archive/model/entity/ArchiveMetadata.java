package com.toryu.dms.archive.model.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.toryu.iims.common.model.entity.base.BaseTable;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Builder /*@Builder可以让你类链式的调用你的代码，来初始化你的实例对象*/
@NoArgsConstructor /*注解在类上；为类提供一个无参的构造方法*/
@AllArgsConstructor /*注解在类上；为类提供一个全参的构造方法*/
@EqualsAndHashCode(callSuper = true)
public class ArchiveMetadata extends BaseTable implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long id;

    private String archivalCode;

    private String archivalTitle;

    private String archivalNum;

    private String archivalYear;

    private Timestamp archivalDate;

    private String archivalLevel;

    private String archivalDeadline;

    private String archivalAbstract;

    private String archivalResponsible;

    private String archiveSite;

    private Long archivePackage;

    private Integer archiveStatus;

    private String metadataProperty;

    private Integer metadataOwnership;

    private Integer fileNum;

    private String fileIds;

    private Boolean isDeleted;

}
