package com.toryu.iims.ai.chat.controller;

import com.toryu.iims.ai.chat.model.dto.ChatDialoguePageQueryDTO;
import com.toryu.iims.ai.chat.service.DialogueManageService;
import com.toryu.iims.common.result.PageResult;
import com.toryu.iims.common.result.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/iims/ai/dialogue")
public class DialogueManageController {

    private final DialogueManageService dialogueManageService;

    public DialogueManageController(DialogueManageService dialogueManageService) {
        this.dialogueManageService = dialogueManageService;
    }

    @PostMapping("/page")
    @ApiOperation("对话内容记录查询")
    public Result<PageResult> page(@RequestBody ChatDialoguePageQueryDTO chatDialoguePageQueryDto) {
        PageResult pageResult = dialogueManageService.chatDialoguePageQuery(chatDialoguePageQueryDto);
        return Result.success(pageResult);
    }

    @PostMapping("/star/{status}")
    @ApiOperation("收藏对话记录")
    public Result<T> switchStar(@PathVariable Boolean status, @RequestParam("id") Long id) {
        return Result.fromBoolean(dialogueManageService.switchStar(id, status));
    }

    @PostMapping("/feedback/{status}")
    @ApiOperation("收藏对话记录")
    public Result<T> exchangeFeedback(@PathVariable Integer status, @RequestParam("id") Long id) {
        return Result.fromBoolean(dialogueManageService.exchangeFeedback(id, status));
    }

    @GetMapping("/del/{lastId}")
    @ApiOperation("删除对话历史记录")
    public Result<T> delDialogue(@PathVariable Long lastId) {
        return Result.fromBoolean(dialogueManageService.delDialogue(lastId));
    }

}
