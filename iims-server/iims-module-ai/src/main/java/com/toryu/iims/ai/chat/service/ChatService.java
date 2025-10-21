package com.toryu.iims.ai.chat.service;

import com.toryu.iims.ai.chat.model.dto.SendMessageDTO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ChatService {

    SseEmitter conversation(Long uuid, SendMessageDTO messageDto);

    SseEmitter regenerate(Long uuid, Long lastId);

    Boolean stopConversation(Long uuid);
}
