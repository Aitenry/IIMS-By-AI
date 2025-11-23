package com.toryu.iims.ai.chat.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.toryu.iims.ai.chat.mapper.AiChatModelsMapper;
import com.toryu.iims.ai.chat.model.dto.ModelPageQueryDTO;
import com.toryu.iims.ai.chat.model.entity.ChatApi;
import com.toryu.iims.ai.chat.model.vo.ModelVO;
import com.toryu.iims.ai.chat.service.AiChatModelsService;
import org.springframework.stereotype.Service;

/**
 * @Author: Aitenry
 * @Date: 2025/11/23 20:32
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Service
public class AiChatModelsServiceImpl implements AiChatModelsService {

    public final AiChatModelsMapper aiChatModelsMapper;

    public AiChatModelsServiceImpl(AiChatModelsMapper aiChatModelsMapper) {
        this.aiChatModelsMapper = aiChatModelsMapper;
    }

    @Override
    public ChatApi selectModelById(Long modelId) {
        return aiChatModelsMapper.selectModelById(modelId);
    }

    @Override
    public Page<ModelVO> pageQuery(ModelPageQueryDTO modelPageQueryDTO) {
        int page = modelPageQueryDTO.getPage();
        int pageSize = modelPageQueryDTO.getPageSize();
        PageHelper.startPage(page, pageSize);
        return aiChatModelsMapper.pageQuery(modelPageQueryDTO);
    }
}
