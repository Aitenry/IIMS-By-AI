package com.toryu.iims.ai.chat.model.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.toryu.iims.common.enums.AiApiType;
import com.toryu.iims.common.enums.AiModelType;
import com.toryu.iims.common.model.vo.BaseAdminInfoVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2025/11/23 14:26
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModelVO {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long id;

    private String rename;

    private String name;

    private String url;

    private String token;

    private String description;

    private AiApiType type;

    private AiModelType modelType;

    private Boolean isOnline;

    private LocalDateTime detectionTime;

    private List<BaseAdminInfoVO> users;

}
