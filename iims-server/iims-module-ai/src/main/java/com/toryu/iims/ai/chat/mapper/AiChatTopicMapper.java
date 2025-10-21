package com.toryu.iims.ai.chat.mapper;

import com.github.pagehelper.Page;
import com.toryu.iims.ai.chat.model.entity.AiChatTopic;
import com.toryu.iims.ai.chat.model.vo.ChatTopicVO;
import com.toryu.iims.common.annotation.AutoFill;
import com.toryu.iims.common.enums.OperationType;
import com.toryu.iims.common.model.entity.status.DeletedStatus;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AiChatTopicMapper {

    Page<ChatTopicVO> pageQuery(AiChatTopic aiChatTopic);

    @AutoFill(value = OperationType.INSERT)
    Boolean insert(AiChatTopic aiChatTopic);

    @AutoFill(value = OperationType.UPDATE)
    Boolean updateTopicDeleted(DeletedStatus deletedStatus);

    @AutoFill(value = OperationType.UPDATE)
    Boolean update(AiChatTopic aiChatTopic);

}
