package com.toryu.iims.integral.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2025/11/23 10:24
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Mapper
public interface ChatSettingMapper {

    @Select("SELECT user_id FROM iims_ai_chat_settings WHERE language_model = #{modelId} OR vision_model = #{modelId} OR multimodal_model = #{modelId} OR embedding_model = #{modelId}")
    List<Long> selectUserIdByModelId(@Param("modelId") Long modelId);

}
