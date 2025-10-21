package com.toryu.iims.ai.graph.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.toryu.iims.ai.graph.context.PromptTemplateContext;
import com.toryu.iims.ai.graph.model.entity.GraphEntity;
import com.toryu.iims.ai.graph.model.entity.GraphRelationship;
import com.toryu.iims.ai.chat.model.entity.ModelSetting;
import com.toryu.iims.ai.chat.service.ModelSettingService;
import com.toryu.iims.ai.chat.service.ModelWarehouseService;
import com.toryu.iims.ai.rag.utils.PromptTemplateUtil;
import com.toryu.iims.ai.graph.service.GenerateGraphService;
import com.toryu.iims.common.utils.SnowFlakeIdWorker;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GenerateGraphServiceImpl implements GenerateGraphService {

    private final SnowFlakeIdWorker aiSnowFlakeIdWorker;
    private final ModelWarehouseService modelWarehouseService;
    private final ModelSettingService settingService;

    public GenerateGraphServiceImpl(SnowFlakeIdWorker aiSnowFlakeIdWorker, ModelWarehouseService modelWarehouseService,
                                    ModelSettingService settingService) {
        this.aiSnowFlakeIdWorker = aiSnowFlakeIdWorker;
        this.modelWarehouseService = modelWarehouseService;
        this.settingService = settingService;
    }


    public long uniqueId() {
        return aiSnowFlakeIdWorker.nextId();
    }


    @Override
    public List<GraphEntity> extractEntityByModel(String chunk) {
        SystemMessage systemMessage = new SystemMessage(PromptTemplateContext.EXTRACT_ENTITY_SYSTEM);
        Message userMessage = new PromptTemplate(PromptTemplateContext.EXTRACT_ENTITY_USER).createMessage(Map.of("chunk", chunk));
        List<Message> messages = new ArrayList<>(List.of(systemMessage, userMessage));

        ModelSetting modelSetting = settingService.getUserModelSetting();
        ChatResponse call = modelWarehouseService.getChatModel(modelSetting.getLanguageModel())
                .call(new Prompt(messages));
        String result = PromptTemplateUtil.removeJsonCodeBlocks(PromptTemplateUtil.removeThink(call.getResult().getOutput().getText()));
        List<GraphEntity> graphEntities = JSONArray.parseArray(result, GraphEntity.class);
        graphEntities.forEach(entity -> entity.setId(uniqueId()));
        return graphEntities;
    }

    @Override
    public List<GraphRelationship> generateRelationshipByModel(String chunk, String content) {
        SystemMessage systemMessage = new SystemMessage(PromptTemplateContext.GENERATE_RELATIONSHIP_SYSTEM);
        Message userMessage = new PromptTemplate(PromptTemplateContext.GENERATE_RELATIONSHIP_USER)
                .createMessage(Map.of("chunk", chunk, "content", content));
        List<Message> messages = new ArrayList<>(List.of(systemMessage, userMessage));
        ModelSetting modelSetting = settingService.getUserModelSetting();
        ChatResponse call = modelWarehouseService.getChatModel(modelSetting.getLanguageModel()).call(new Prompt(messages));
        String result = PromptTemplateUtil.removeJsonCodeBlocks(PromptTemplateUtil.removeThink(call.getResult().getOutput().getText()));
        List<GraphRelationship> graphRelationships = JSONArray.parseArray(result, GraphRelationship.class);
        graphRelationships.forEach(relationship -> relationship.setId(uniqueId()));
        return graphRelationships;
    }

}
