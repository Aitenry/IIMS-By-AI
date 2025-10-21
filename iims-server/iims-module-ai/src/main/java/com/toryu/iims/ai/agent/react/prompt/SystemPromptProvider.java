package com.toryu.iims.ai.agent.react.prompt;

import java.util.Map;

public interface SystemPromptProvider {
    String getSystemPrompt(Map<String, Object> context);
}
