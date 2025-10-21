package com.toryu.iims.ai.chat.service;

import com.toryu.iims.ai.chat.model.dto.ChatRenameTopicDTO;
import com.toryu.iims.ai.chat.model.dto.ChatTopicPageQueryDTO;
import com.toryu.iims.ai.chat.model.entity.AiChatTopic;
import com.toryu.iims.common.result.PageResult;

import java.util.List;

public interface TopicManageService {
    PageResult chatTopicPageQuery(ChatTopicPageQueryDTO topicPageQueryDto);

    void insertTopic(AiChatTopic aiChatTopic);

    Boolean delTopic(List<Long> ids);

    Boolean clearDialogueFromTopic(Long id);

    Boolean renameTopic(ChatRenameTopicDTO renameTopicDto);
}
