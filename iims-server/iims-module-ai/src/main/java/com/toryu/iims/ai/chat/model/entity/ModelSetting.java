package com.toryu.iims.ai.chat.model.entity;

import com.toryu.iims.common.model.entity.base.BaseTable;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ModelSetting extends BaseTable {

    /**
     * 大语言模型
     */
    private Long languageModel;

    /**
     * 视觉大模型
     */
    private Long visionModel;

    /**
     * 多模态模型
     */
    private Long multimodalModel;

    /**
     * 嵌入向量模型
     */
    private Long embeddingModel;

}