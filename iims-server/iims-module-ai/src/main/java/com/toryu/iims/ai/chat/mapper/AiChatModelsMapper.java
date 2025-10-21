package com.toryu.iims.ai.chat.mapper;


import com.toryu.iims.ai.chat.model.entity.ChatApi;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AiChatModelsMapper {

    @Select("select * from iims_ai_chat_models where id = #{id} limit 1")
    ChatApi selectModelById(Long id);

}
