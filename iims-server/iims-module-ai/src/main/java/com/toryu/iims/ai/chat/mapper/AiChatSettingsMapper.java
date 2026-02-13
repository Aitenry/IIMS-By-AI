package com.toryu.iims.ai.chat.mapper;

import com.toryu.iims.ai.chat.model.entity.ModelSetting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AiChatSettingsMapper {

    @Select("select * from iims_ai_chat_settings where user_id = #{userId} limit 1")
    ModelSetting getModelSettingByUserId(Long userId);

    @Select("SELECT user_id FROM iims_ai_chat_settings WHERE model_id = #{modelId} OR embedding_model = #{modelId}")
    List<Long> selectUserIdByModelId(@Param("modelId") Long modelId);

}
