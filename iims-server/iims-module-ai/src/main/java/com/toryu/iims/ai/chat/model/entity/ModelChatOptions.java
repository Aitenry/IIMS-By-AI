package com.toryu.iims.ai.chat.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModelChatOptions {

    private Double frequencyPenalty;

    private Integer maxTokens;

    private Double presencePenalty;

    private List<String> stopSequences;

    private Double temperature;

    private Integer topK;

    private Double topP;
}
