package com.toryu.iims.ai.agent.react.event;

import org.springframework.ai.chat.messages.Message;

public record LlmMessageEvent (Message message, Boolean isComplete) implements ReActAgentEvent {

    @Override
    public Boolean getComplete() {
        return isComplete;
    }

    @Override
    public Message getContent() {
        return message;
    }

}
