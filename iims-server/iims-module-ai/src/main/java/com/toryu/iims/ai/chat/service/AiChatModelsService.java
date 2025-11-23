package com.toryu.iims.ai.chat.service;

import com.github.pagehelper.Page;
import com.toryu.iims.ai.chat.model.dto.ModelPageQueryDTO;
import com.toryu.iims.ai.chat.model.entity.ChatApi;
import com.toryu.iims.ai.chat.model.vo.ModelVO;

/**
 * @Author: Aitenry
 * @Date: 2025/11/23 20:30
 * @Version: v1.0.0
 * @Description: TODO
 **/
public interface AiChatModelsService {

    ChatApi selectModelById(Long id);

    Page<ModelVO> pageQuery(ModelPageQueryDTO modelPageQueryDTO);

}
