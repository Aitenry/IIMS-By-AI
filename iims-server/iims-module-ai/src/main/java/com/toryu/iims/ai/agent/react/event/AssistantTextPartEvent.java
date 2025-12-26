package com.toryu.iims.ai.agent.react.event;

public record AssistantTextPartEvent (String text) implements ReActAgentEvent {
    @Override
    public String getContent() {
        return text;
    }
}
