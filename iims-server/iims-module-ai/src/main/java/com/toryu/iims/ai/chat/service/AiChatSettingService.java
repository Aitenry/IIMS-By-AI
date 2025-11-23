package com.toryu.iims.ai.chat.service;

import com.toryu.iims.ai.chat.model.entity.ModelSetting;

import java.util.List;

public interface AiChatSettingService {

    ModelSetting getUserModelSetting();

    List<Long> selectUserIdByModelId(Long modelId);

}
