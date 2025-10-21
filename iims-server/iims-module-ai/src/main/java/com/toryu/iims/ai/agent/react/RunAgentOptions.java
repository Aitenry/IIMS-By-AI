package com.toryu.iims.ai.agent.react;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.ChatOptions;

import java.util.List;
import java.util.Map;

public interface RunAgentOptions {
    List<Message> getNewMessages();

    String getPreviousMessageId();

    boolean isEnableStream();

    int getMaxIterations();

    ChatOptions getChatOptions();

    Map<String, Object> getContext();

    Integer getMessageHistoryWindowSize();

    String getThreadId();

    static Builder builder() {
        return new DefaultRunAgentOptions.Builder();
    }

    interface Builder {
        Builder newMessages(List<Message> messages);

        Builder newUserMessage(String content);

        Builder previousMessageId(String previousMessageId);

        Builder enableStream(boolean streamChatModel);

        Builder maxIterations(int maxIterations);

        Builder chatOptions(ChatOptions chatOptions);

        Builder context(Map<String, Object> context);

        Builder messageHistoryWindowSize(Integer size);

        Builder threadId(String threadId);

        RunAgentOptions build();
    }
}
