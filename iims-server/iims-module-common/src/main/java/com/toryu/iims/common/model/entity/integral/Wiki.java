package com.toryu.iims.common.model.entity.integral;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.toryu.iims.common.enums.TaskStatusEnum;
import com.toryu.iims.common.enums.WikiTypeEnum;
import com.toryu.iims.common.model.entity.base.BaseTable;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Wiki extends BaseTable implements Serializable {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long id;

    private String title;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long cover;

    private String summary;

    private Boolean isDeleted;

    private TaskStatusEnum taskStatus;

    private Integer weight;

    private WikiTypeEnum type;

    private Boolean isPublish;

}
