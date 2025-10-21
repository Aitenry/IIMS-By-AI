package com.toryu.iims.ai.agent.react;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;

import java.util.List;
import java.util.Map;

class DefaultRunAgentOptions implements RunAgentOptions {

    private final List<Message> messages;
    private final String previousMessageId;
    private final boolean streamChatModel;
    private final int maxIterations;
    private final ChatOptions chatOptions;
    private final Map<String, Object> context;
    private final Integer messageHistoryWindowSize;
    private final String threadId;

    private DefaultRunAgentOptions(Builder builder) {
        this.messages = builder.messages;
        this.previousMessageId = builder.previousMessageId;
        this.streamChatModel = builder.enableStream;
        this.maxIterations = builder.maxIterations;
        this.chatOptions = builder.chatOptions;
        this.context = builder.context;
        this.messageHistoryWindowSize = builder.messageHistoryWindowSize;
        this.threadId = builder.threadId;
    }

    @Override
    public List<Message> getNewMessages() {
        return this.messages;
    }

    @Override
    public String getPreviousMessageId() {
        return this.previousMessageId;
    }

    @Override
    public boolean isEnableStream() {
        return streamChatModel;
    }

    @Override
    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public ChatOptions getChatOptions() {
        return chatOptions;
    }

    @Override
    public Map<String, Object> getContext() {
        return this.context;
    }

    @Override
    public Integer getMessageHistoryWindowSize() {
        return this.messageHistoryWindowSize;
    }

    @Override
    public String getThreadId() {
        return this.threadId;
    }

    public static class Builder implements RunAgentOptions.Builder {
        private List<Message> messages;
        private String previousMessageId;
        private boolean enableStream = false;
        private int maxIterations = 25;
        private ChatOptions chatOptions;
        private Map<String, Object> context = Map.of();
        private Integer messageHistoryWindowSize = Integer.MAX_VALUE;
        private String threadId;

        @Override
        public RunAgentOptions.Builder newMessages(List<Message> messages) {
            this.messages = messages;
            return this;
        }

        @Override
        public RunAgentOptions.Builder newUserMessage(String content) {
            return newMessages(List.of(UserMessage.builder().text(content).build()));
        }

        @Override
        public RunAgentOptions.Builder previousMessageId(String previousMessageId) {
            this.previousMessageId = previousMessageId;
            return this;
        }

        @Override
        public Builder enableStream(boolean enableStream) {
            this.enableStream = enableStream;
            return this;
        }

        @Override
        public Builder maxIterations(int maxIterations) {
            this.maxIterations = maxIterations;
            return this;
        }

        @Override
        public Builder chatOptions(ChatOptions chatOptions) {
            this.chatOptions = chatOptions;
            return this;
        }

        @Override
        public RunAgentOptions.Builder context(Map<String, Object> context) {
            this.context = context;
            return this;
        }

        @Override
        public RunAgentOptions.Builder messageHistoryWindowSize(Integer size) {
            this.messageHistoryWindowSize = size;
            return this;
        }

        @Override
        public RunAgentOptions.Builder threadId(String threadId) {
            this.threadId = threadId;
            return this;
        }

        @Override
        public RunAgentOptions build() {
            return new DefaultRunAgentOptions(this);
        }

    }

}
