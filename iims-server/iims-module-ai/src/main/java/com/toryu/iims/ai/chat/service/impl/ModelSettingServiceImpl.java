package com.toryu.iims.ai.chat.service.impl;

import com.toryu.iims.ai.chat.mapper.AiChatSettingsMapper;
import com.toryu.iims.ai.chat.model.entity.ModelSetting;
import com.toryu.iims.ai.chat.service.ModelSettingService;
import com.toryu.iims.common.context.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ModelSettingServiceImpl implements ModelSettingService {

    private final AiChatSettingsMapper settingsMapper;

    public ModelSettingServiceImpl(AiChatSettingsMapper settingsMapper) {
        this.settingsMapper = settingsMapper;
    }

    @Override
    public ModelSetting getUserModelSetting() {
        return settingsMapper.getModelSettingByUserId(BaseContext.getCurrentId());
    }
}
