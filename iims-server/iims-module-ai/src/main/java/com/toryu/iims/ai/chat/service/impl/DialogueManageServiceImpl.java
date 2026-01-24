package com.toryu.iims.ai.chat.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.toryu.iims.ai.chat.mapper.AiChatDialogueMapper;
import com.toryu.iims.ai.chat.model.dto.ChatDialoguePageQueryDTO;
import com.toryu.iims.ai.chat.model.entity.*;
import com.toryu.iims.ai.chat.model.vo.ChatDialogueVO;
import com.toryu.iims.ai.chat.model.vo.DocMetadataVO;
import com.toryu.iims.ai.chat.service.DialogueManageService;
import com.toryu.iims.ai.rag.utils.DocMetadataUtil;
import com.toryu.iims.ai.rag.utils.PromptTemplateUtil;
import com.toryu.iims.common.context.BaseContext;
import com.toryu.iims.common.model.entity.file.FileInfo;
import com.toryu.iims.common.model.entity.file.FileWarehouse;
import com.toryu.iims.common.model.entity.status.DeletedStatus;
import com.toryu.iims.common.result.PageResult;
import com.toryu.iims.common.service.FileStorageService;
import com.toryu.iims.common.service.MinioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeTypeUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class DialogueManageServiceImpl implements DialogueManageService {

    private final FileStorageService storageService;
    private final MinioService minioService;
    private final DocMetadataUtil docMetadataUtil;
    private final AiChatDialogueMapper aiChatDialogueMapper;

    public DialogueManageServiceImpl(FileStorageService storageService, MinioService minioService,
                                     DocMetadataUtil docMetadataUtil, AiChatDialogueMapper aiChatDialogueMapper) {
        this.storageService = storageService;
        this.minioService = minioService;
        this.docMetadataUtil = docMetadataUtil;
        this.aiChatDialogueMapper = aiChatDialogueMapper;
    }

    @Override
    public PageResult chatDialoguePageQuery(ChatDialoguePageQueryDTO pageQueryDto) {
        int page = pageQueryDto.getPage();
        int pageSize = pageQueryDto.getPageSize();
        PageHelper.startPage(page, pageSize);
        AiChatDialogue aiChatDialogue = AiChatDialogue.builder()
                .topicId(pageQueryDto.getTopicId()).isDeleted(false).build();
        aiChatDialogue.setCreateBy(BaseContext.getCurrentId());
        BeanUtils.copyProperties(pageQueryDto, aiChatDialogue);
        long total;
        List<ChatDialogue> result;
        try (Page<ChatDialogue> chatTopicVos = aiChatDialogueMapper.pageQuery(aiChatDialogue)) {
            total = chatTopicVos.getTotal();
            result = chatTopicVos.getResult();
        }
        List<ChatDialogueVO> records = new ArrayList<>();
        result.forEach(r -> {
            ChatDialogueVO chatDialogueVo = new ChatDialogueVO();
            BeanUtils.copyProperties(r, chatDialogueVo);
            List<Long> ids = r.getFileIds();
            if (Objects.equals(r.getSender(), "user")) {
                chatDialogueVo.setUserContent(JSONObject.parseObject(r.getContent(), UserContent.class));
            } else {
                chatDialogueVo.setAiContent(JSONArray.parseArray(r.getContent(), AiContent.class));
            }
            if (Objects.nonNull(ids) && !ids.isEmpty()) {
                List<FileWarehouse> objects = storageService.getObjectByIds(ids);
                List<FileInfo> fileInfos = new ArrayList<>();
                objects.forEach(object -> {
                    String previewUrl = minioService.generateShortLink(object.getId());
                    fileInfos.add(FileInfo.builder()
                            .id(object.getId()).fileSize(object.getFileSize())
                            .url(previewUrl).filename(object.getFileName()).build());
                });
                chatDialogueVo.setFileInfos(fileInfos);
            }
            List<DocMetadataVO> docMetadata = docMetadataUtil.getDocMetadata(r.getMetadata());
            chatDialogueVo.setDocMetadata(docMetadata);
            chatDialogueVo.setTools(JSONArray.parseArray(r.getTools(), ChatTool.class));
            records.add(chatDialogueVo);
        });
        Collections.reverse(records);
        return new PageResult(total, records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertDialogue(AiChatDialogue aiChatDialogue) {
        aiChatDialogueMapper.insert(aiChatDialogue);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean switchStar(Long id, Boolean status) {
        AiChatDialogue aiChatDialogue = AiChatDialogue.builder().id(id).isStar(status).build();
        return aiChatDialogueMapper.update(aiChatDialogue);
    }

    @Override
    public List<Message> loadingDialogueHistory(Long topicId) {
        List<ChatDialogue> dialogueHistory = aiChatDialogueMapper.getHistoryByTopicId(topicId, 10);
        List<Message> messages = new ArrayList<>(dialogueHistory.size());

        for (ChatDialogue chatDialogue : dialogueHistory) {
            MessageType senderType = MessageType.fromValue(chatDialogue.getSender());
            String content = chatDialogue.getContent();
            if (MessageType.USER.equals(senderType)) {
                List<Media> mediaList = getMediaFromChatDialogue(chatDialogue);
                UserContent userContent = JSONObject.parseObject(content, UserContent.class);
                messages.add(UserMessage.builder().text(userContent.getQuestion()).media(mediaList).build());
            } else if (MessageType.ASSISTANT.equals(senderType)) {
                List<AiContent> aiContents = JSONArray.parseArray(content, AiContent.class);
                aiContents.forEach(aiContent -> {
                    StringBuffer aiContentContent = aiContent.getContent();
                    if (aiContentContent != null) {
                        String filteredContent = PromptTemplateUtil.removeThink(aiContentContent.toString());
                        aiContent.setContent(new StringBuffer(filteredContent));
                    }
                });
                messages.add(new AssistantMessage(JSONArray.toJSONString(aiContents)));
            }
        }

        return messages;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delDialogue(Long lastId) {
        AiChatDialogue aiChatDialogue = AiChatDialogue.builder().lastId(lastId).build();
        return aiChatDialogueMapper.del(aiChatDialogue);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateDeletedByTopicIds(List<Long> topicIds) {
        if (!topicIds.isEmpty()) {
            DeletedStatus deletedStatus = DeletedStatus.builder()
                    .isDeleted(true).ids(topicIds).build();
            return aiChatDialogueMapper.updateDeletedByTopicIds(deletedStatus);
        }
        return false;
    }

    @Override
    public Boolean exchangeFeedback(Long id, Integer status) {
        AiChatDialogue aiChatDialogue = AiChatDialogue.builder().id(id).feedbackStatus(status).build();
        return aiChatDialogueMapper.update(aiChatDialogue);
    }

    /**
     * 根据 ChatDialogue 获取 Media 列表
     */
    private List<Media> getMediaFromChatDialogue(ChatDialogue chatDialogue) {
        List<Long> fileIds = chatDialogue.getFileIds();
        if (fileIds == null || fileIds.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            List<FileWarehouse> storageItems = storageService.getObjectByIds(fileIds);
            List<Media> mediaList = new ArrayList<>(storageItems.size());

            for (FileWarehouse item : storageItems) {
                if (item.getFileType().contains("image")) {
                    try (InputStream inputStream = minioService.getInputStream(item.getId())) {
                        mediaList.add(new Media(MimeTypeUtils.ALL, new InputStreamResource(inputStream)));
                    } catch (IOException e) {
                        // 可以记录日志，继续处理其他文件
                        log.warn("Failed to load input stream for file ID: {}", item.getId(), e);
                    }
                }
            }

            return mediaList;
        } catch (Exception e) {
            log.error("Error fetching storage items for file IDs: {}", fileIds, e);
            return Collections.emptyList();
        }
    }

}
