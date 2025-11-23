package com.toryu.iims.integral.service.impl;

import com.toryu.iims.integral.mapper.ChatSettingMapper;
import com.toryu.iims.integral.service.ChatSettingService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2025/11/23 10:26
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Service
public class ChatSettingServiceImpl implements ChatSettingService {

    public final ChatSettingMapper chatSettingMapper;

    public ChatSettingServiceImpl(ChatSettingMapper chatSettingMapper) {
        this.chatSettingMapper = chatSettingMapper;
    }

    @Override
    public List<Long> selectUserIdByModelId(Long modelId) {
        return chatSettingMapper.selectUserIdByModelId(modelId);
    }
}
