package com.toryu.iims.ai.agent.react.event;

import org.springframework.ai.chat.messages.Message;

public record LlmMessageEvent (Message message, String id) implements ReActAgentEvent {
}
