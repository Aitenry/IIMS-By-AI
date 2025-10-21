package com.toryu.iims.ai.agent.react.message;

import org.springframework.ai.chat.messages.Message;

import java.util.Map;

public record BranchMessageItem(Message message, String id, String previousId, Map<String, Object> metadata) {
}