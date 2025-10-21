package com.toryu.iims.ai.agent.react;

import com.toryu.iims.ai.agent.react.event.ReActAgentEvent;
import com.toryu.iims.ai.agent.react.memory.BranchMessageSaver;
import com.toryu.iims.ai.agent.react.message.BranchMessageItem;
import com.toryu.iims.ai.agent.react.prompt.SystemPromptProvider;
import org.springframework.ai.chat.client.ChatClient;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ReActAgent {
    Flux<ReActAgentEvent> run(RunAgentOptions options);

    List<BranchMessageItem> getBranchMessages(String threadId);

    static Builder builder(ChatClient chatClient) {
        return new DefaultReActAgent.Builder(chatClient);
    }

    interface Builder {
        DefaultReActAgent.Builder messageSaver(BranchMessageSaver branchMessageSaver);

        DefaultReActAgent.Builder systemPromptProvider(SystemPromptProvider systemPromptProvider);

        DefaultReActAgent.Builder systemPrompt(String systemPrompt);

        ReActAgent build();
    }
}
