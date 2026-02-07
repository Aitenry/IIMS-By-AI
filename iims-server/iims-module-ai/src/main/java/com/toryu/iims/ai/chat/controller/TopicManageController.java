package com.toryu.iims.ai.chat.controller;

import com.toryu.iims.ai.chat.model.dto.ChatRenameTopicDTO;
import com.toryu.iims.ai.chat.model.dto.ChatTopicPageQueryDTO;
import com.toryu.iims.ai.chat.service.TopicManageService;
import com.toryu.iims.common.result.PageResult;
import com.toryu.iims.common.result.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/iims/ai/topic")
public class TopicManageController {

    private final TopicManageService topicManageService;

    public TopicManageController(TopicManageService topicManageService) {
        this.topicManageService = topicManageService;
    }

    @PostMapping("/page")
    @ApiOperation("对话记录查询")
    public Result<PageResult> page(@RequestBody ChatTopicPageQueryDTO chatTopicPageQueryDto) {
        PageResult pageResult = topicManageService.chatTopicPageQuery(chatTopicPageQueryDto);
        return Result.success(pageResult);
    }

    @PostMapping("/del")
    @ApiOperation("删除对话")
    public Result<String> delTopic(@RequestBody List<Long> ids) {
        return Result.fromBoolean(topicManageService.delTopic(ids));
    }

    @PostMapping("/rename")
    @ApiOperation("重命名对话")
    public Result<String> renameTopic(@RequestBody ChatRenameTopicDTO renameTopicDto) {
        return Result.fromBoolean(topicManageService.renameTopic(renameTopicDto));
    }

}
