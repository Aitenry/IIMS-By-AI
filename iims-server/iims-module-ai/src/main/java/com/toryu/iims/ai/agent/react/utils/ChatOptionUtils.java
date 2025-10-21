package com.toryu.iims.ai.agent.react.utils;

import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.model.tool.ToolCallingChatOptions;

public class ChatOptionUtils {
    private ChatOptionUtils() {
    }

    public static ToolCallingChatOptions copyToToolCallingChatOptions(ChatOptions chatOptions) {
        if (chatOptions instanceof ToolCallingChatOptions toolCallingChatOptions) {
            return toolCallingChatOptions.copy();
        }
        return copyAllChatOptionFields(chatOptions);
    }

    private static ToolCallingChatOptions copyAllChatOptionFields(ChatOptions chatOptions) {
        return ToolCallingChatOptions.builder()
                .model(chatOptions.getModel())
                .frequencyPenalty(chatOptions.getFrequencyPenalty())
                .maxTokens(chatOptions.getMaxTokens())
                .presencePenalty(chatOptions.getPresencePenalty())
                .stopSequences(chatOptions.getStopSequences())
                .temperature(chatOptions.getTemperature())
                .topK(chatOptions.getTopK())
                .topP(chatOptions.getTopP())
                .build();
    }
}
