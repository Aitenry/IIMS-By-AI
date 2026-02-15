package com.toryu.iims.integral.service;

import com.toryu.iims.ai.chat.model.dto.CreateModelDTO;
import com.toryu.iims.ai.chat.model.dto.ModelPageQueryDTO;
import com.toryu.iims.ai.chat.model.dto.UpdateModelDTO;
import com.toryu.iims.common.result.PageResult;

/**
 * @Author: Aitenry
 * @Date: 2025/11/23 14:18
 * @Version: v1.0.0
 * @Description: TODO
 **/
public interface AiModelService {

    PageResult pageQuery(ModelPageQueryDTO modelPageQueryDTO);

    boolean deleteModel(Long id);

    boolean updateModel(UpdateModelDTO model);

    boolean createModel(CreateModelDTO model);
}
