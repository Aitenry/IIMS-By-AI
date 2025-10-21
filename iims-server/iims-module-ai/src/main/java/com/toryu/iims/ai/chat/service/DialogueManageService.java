package com.toryu.iims.ai.chat.service;

import com.toryu.iims.ai.chat.model.dto.ChatDialoguePageQueryDTO;
import com.toryu.iims.ai.chat.model.entity.AiChatDialogue;
import com.toryu.iims.common.result.PageResult;
import org.springframework.ai.chat.messages.Message;

import java.util.List;

public interface DialogueManageService {

    PageResult chatDialoguePageQuery(ChatDialoguePageQueryDTO dialoguePageQueryDto);

    void insertDialogue(AiChatDialogue aiChatDialogue);

    Boolean switchStar(Long id, Boolean status);

    List<Message> loadingDialogueHistory(Long topicId);

    Boolean delDialogue(Long lastId);

    Boolean updateDeletedByTopicIds(List<Long> topicIds);

    Boolean exchangeFeedback(Long id, Integer status);
}
