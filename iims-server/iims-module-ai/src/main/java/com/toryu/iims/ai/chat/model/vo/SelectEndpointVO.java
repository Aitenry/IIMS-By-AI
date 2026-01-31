package com.toryu.iims.ai.chat.model.vo;


import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.toryu.iims.common.enums.AiModelApiType;
import com.toryu.iims.common.enums.AiModelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Author: Aitenry
 * @Date: 2026/1/31 9:07
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SelectEndpointVO {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long id;

    private String name;

    private String description;

    private AiModelApiType apiType;

    private AiModelType modelType;


}


