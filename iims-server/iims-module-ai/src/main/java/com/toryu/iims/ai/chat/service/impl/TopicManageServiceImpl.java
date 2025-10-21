package com.toryu.iims.ai.chat.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.toryu.iims.ai.chat.mapper.AiChatTopicMapper;
import com.toryu.iims.ai.chat.model.dto.ChatRenameTopicDTO;
import com.toryu.iims.ai.chat.model.dto.ChatTopicPageQueryDTO;
import com.toryu.iims.ai.chat.model.entity.AiChatTopic;
import com.toryu.iims.ai.chat.model.vo.ChatTopicVO;
import com.toryu.iims.ai.chat.service.DialogueManageService;
import com.toryu.iims.ai.chat.service.TopicManageService;
import com.toryu.iims.common.context.BaseContext;
import com.toryu.iims.common.model.entity.status.DeletedStatus;
import com.toryu.iims.common.result.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class TopicManageServiceImpl implements TopicManageService {

    private final DialogueManageService dialogueManageService;

    private final AiChatTopicMapper aiChatTopicMapper;

    public TopicManageServiceImpl(DialogueManageService dialogueManageService,
                                   AiChatTopicMapper aiChatTopicMapper) {
        this.dialogueManageService = dialogueManageService;
        this.aiChatTopicMapper = aiChatTopicMapper;
    }

    @Override
    public PageResult chatTopicPageQuery(ChatTopicPageQueryDTO pageQueryDto) {
        int page = pageQueryDto.getPage();
        int pageSize = pageQueryDto.getPageSize();
        PageHelper.startPage(page, pageSize);
        AiChatTopic aiChatTopic = AiChatTopic.builder()
                .isDeleted(false).build();
        aiChatTopic.setCreateBy(BaseContext.getCurrentId());
        BeanUtils.copyProperties(pageQueryDto, aiChatTopic);
        Page<ChatTopicVO> chatTopicVOS = aiChatTopicMapper.pageQuery(aiChatTopic);
        long total = chatTopicVOS.getTotal();
        List<ChatTopicVO> records = chatTopicVOS.getResult();
        return new PageResult(total, records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertTopic(AiChatTopic aiChatTopic) {
        aiChatTopicMapper.insert(aiChatTopic);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delTopic(List<Long> ids) {
        if (ids.size() > 0) {
            DeletedStatus deletedStatus = DeletedStatus.builder()
                    .isDeleted(true).ids(ids).build();
            dialogueManageService.updateDeletedByTopicIds(ids);
            return aiChatTopicMapper.updateTopicDeleted(deletedStatus);
        }
        return false;
    }

    @Override
    public Boolean clearDialogueFromTopic(Long id) {
        return dialogueManageService.updateDeletedByTopicIds(List.of(id));
    }

    @Override
    public Boolean renameTopic(ChatRenameTopicDTO renameTopicDto) {
        return aiChatTopicMapper.update(AiChatTopic.builder()
                .id(renameTopicDto.getId())
                .title(renameTopicDto.getTitle()).build());
    }

}
