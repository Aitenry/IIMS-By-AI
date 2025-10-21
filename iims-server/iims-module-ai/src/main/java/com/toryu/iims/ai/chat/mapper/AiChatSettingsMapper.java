package com.toryu.iims.ai.chat.mapper;

import com.toryu.iims.ai.chat.model.entity.ModelSetting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AiChatSettingsMapper {

    @Select("select * from iims_ai_chat_settings where user_id = #{userId} limit 1")
    ModelSetting getModelSettingByUserId(Long userId);

}
