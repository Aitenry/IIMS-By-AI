package com.toryu.iims.ai.chat.controller;

import com.toryu.iims.ai.chat.model.dto.SendMessageDTO;
import com.toryu.iims.ai.chat.model.vo.SelectEndpointVO;
import com.toryu.iims.ai.chat.service.AiChatModelsService;
import com.toryu.iims.ai.chat.service.ChatService;
import com.toryu.iims.common.result.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/iims/ai/chat")
public class ChatController {

    private final ChatService chatService;

    private final AiChatModelsService aiChatModelsService;

    public ChatController(ChatService ChatService, AiChatModelsService aiChatModelsService) {
        this.chatService = ChatService;
        this.aiChatModelsService = aiChatModelsService;
    }

    /**
     * 发送消息
     * @param uuid 唯一标识符号
     * @return SseEmitter
     */
    @ApiOperation("回答用户问题")
    @PostMapping(value = "/receive/answer/{uuid}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter conversation(@PathVariable("uuid") Long uuid, @RequestBody SendMessageDTO messageDto) {
        return chatService.conversation(uuid, messageDto);
    }

    @GetMapping("/endpoint/list")
    public Result<List<SelectEndpointVO>> selectEndpointList() {
        return Result.success(aiChatModelsService.selectEndpointList());
    }

    @ApiOperation("重新生成回答")
    @PostMapping(value = "/regenerate/answer/{uuid}/{lastId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter regenerate(@PathVariable("uuid") Long uuid, @PathVariable("lastId") Long lastId) {
        return chatService.regenerate(uuid, lastId);
    }

    @ApiOperation("停止回答用户问题")
    @GetMapping("/stop/answer/{uuid}")
    public Result<Boolean> stopConversation(@PathVariable("uuid") Long uuid) {
        return Result.fromBoolean(chatService.stopConversation(uuid));
    }

}