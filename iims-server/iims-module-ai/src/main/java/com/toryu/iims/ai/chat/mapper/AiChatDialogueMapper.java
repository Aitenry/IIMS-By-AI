package com.toryu.iims.ai.chat.mapper;

import com.github.pagehelper.Page;
import com.toryu.iims.ai.chat.model.entity.AiChatDialogue;
import com.toryu.iims.ai.chat.model.entity.ChatDialogue;
import com.toryu.iims.common.annotation.AutoFill;
import com.toryu.iims.common.enums.OperationType;
import com.toryu.iims.common.model.entity.status.DeletedStatus;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiChatDialogueMapper {

    Page<ChatDialogue> pageQuery(AiChatDialogue aiChatDialogue);

    List<ChatDialogue> getHistoryByTopicId(Long topicId, Integer size);

    @AutoFill(value = OperationType.INSERT)
    Boolean insert(AiChatDialogue aiChatDialogue);

    @AutoFill(value = OperationType.UPDATE)
    Boolean update(AiChatDialogue aiChatDialogue);

    @AutoFill(value = OperationType.UPDATE)
    Boolean del(AiChatDialogue aiChatDialogue);

    @AutoFill(value = OperationType.UPDATE)
    Boolean updateDeletedByTopicIds(DeletedStatus deletedStatus);

}
