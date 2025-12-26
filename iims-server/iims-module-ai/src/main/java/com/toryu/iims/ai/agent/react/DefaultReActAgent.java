package com.toryu.iims.ai.agent.react;

import com.toryu.iims.ai.agent.react.event.AssistantTextPartEvent;
import com.toryu.iims.ai.agent.react.event.LlmMessageEvent;
import com.toryu.iims.ai.agent.react.event.ReActAgentEvent;
import com.toryu.iims.ai.agent.react.exception.MaxIterationReachedException;
import com.toryu.iims.ai.agent.react.memory.BranchMessageSaver;
import com.toryu.iims.ai.agent.react.message.BranchMessageItem;
import com.toryu.iims.ai.agent.react.prompt.FixedSystemPromptProvider;
import com.toryu.iims.ai.agent.react.prompt.SystemPromptProvider;
import com.toryu.iims.ai.agent.react.utils.ChatOptionUtils;
import com.toryu.iims.ai.agent.react.utils.IdGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class DefaultReActAgent implements ReActAgent {

    private final ChatClient chatClient;

    private final BranchMessageSaver branchMessageSaver;

    private final SystemPromptProvider systemPromptProvider;

    private final ToolCallingManager toolCallingManager = ToolCallingManager.builder().build();

    private DefaultReActAgent(ChatClient chatClient, BranchMessageSaver branchMessageSaver,
                              SystemPromptProvider systemPromptProvider) {
        this.chatClient = chatClient;
        this.branchMessageSaver = branchMessageSaver;
        this.systemPromptProvider = systemPromptProvider;
    }

    @Override
    public Flux<ReActAgentEvent> run(RunAgentOptions options) {
        // 如果历史对话在Assistant返回ToolCall处中断，应该先调用对应的方法。
        //填充chatOptions中的toolCallbacks字段, 不会实际调用大模型
        return Flux.create(sink -> {
            String previousMessageId = options.getPreviousMessageId();
            ToolCallingChatOptions chatOptions = buildChatOptions(options);
            List<Message> messageHistory = fetchMessageHistory(options);
            Message systemMessage = buildSystemMessage(options);
            List<Message> allMessages = contactMessages(systemMessage, messageHistory, options.getNewMessages());
            Message lastMessage = allMessages.get(allMessages.size() - 1);
            boolean stream = options.isEnableStream();
            if (lastMessage instanceof AssistantMessage assistantMessage) {
                if (assistantMessage.getToolCalls().isEmpty()) {
                    sink.complete();
                    return;
                }
                // 如果历史对话在Assistant返回ToolCall处中断，应该先调用对应的方法。
                int toolCallCount = assistantMessage.getToolCalls().size();
                Prompt prompt = new Prompt(allMessages.subList(0, allMessages.size() - 1), chatOptions);

                //填充chatOptions中的toolCallbacks字段, 不会实际调用大模型
                ChatClient.ChatClientRequestSpec chatClientRequestSpec = prepareChatClient(options, prompt);
                if (stream) {
                    chatClientRequestSpec.stream();
                } else {
                    chatClientRequestSpec.call();
                }

                ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt,
                        ChatResponse.builder().generations(List.of(new Generation(assistantMessage))).build());
                List<Message> conversationHistory = toolExecutionResult.conversationHistory();
                List<Message> toolCallResults = conversationHistory
                        .subList(conversationHistory.size() - toolCallCount, conversationHistory.size());
                for (Message toolCallResult : toolCallResults) {
                    allMessages.add(toolCallResult);
                    previousMessageId = saveAndSinkMessage(options.getThreadId(), toolCallResult, previousMessageId,
                            sink);
                }
            }
            Prompt prompt = new Prompt(allMessages, chatOptions);
            ChatClient.ChatClientRequestSpec chatClientRequestSpec = prepareChatClient(options, prompt);
            if (previousMessageId == null && branchMessageSaver != null) {
                previousMessageId = branchMessageSaver.getLatestMessageId(options.getThreadId());
            }
            if (options.getNewMessages() != null) {
                for (Message message : options.getNewMessages()) {
                    previousMessageId = saveAndSinkMessage(options.getThreadId(), message, previousMessageId, sink);
                }
            }
            int iterationLeft = options.getMaxIterations();
            AssistantMessage firstMessage;
            if (stream) {
                ChatClient.StreamResponseSpec streamResponseSpec = chatClientRequestSpec.stream();
                Flux<ChatResponse> chatResponseFlux = streamResponseSpec.chatResponse();
                firstMessage = iterateResponseParts(sink, chatResponseFlux);
            } else {
                ChatClient.CallResponseSpec callResponseSpec = chatClientRequestSpec.call();
                ChatResponse chatResponse = callResponseSpec.chatResponse();
                if (chatResponse == null) {
                    sink.error(new IllegalStateException("chatResponse is null"));
                    return;
                }
                firstMessage = chatResponse.getResult().getOutput();
            }
            iterationLeft--;
            List<AssistantMessage.ToolCall> toolCalls = firstMessage.getToolCalls();
            List<AssistantMessage.ToolCall> list = toolCalls.stream().filter(
                    toolCall -> StringUtils.isNoneBlank(toolCall.name())
                            && StringUtils.isNoneBlank(toolCall.arguments())).toList();
            firstMessage.getToolCalls().clear();
            firstMessage.getToolCalls().addAll(list);
            previousMessageId = saveAndSinkMessage(options.getThreadId(), firstMessage, previousMessageId, sink);
            ChatResponse chatResponse = buildResponseFromAssistantMessage(firstMessage);
            while (chatResponse.hasToolCalls()) {
                this.filterValidToolCalls(chatResponse);
                int toolCallCount = chatResponse.getResult().getOutput().getToolCalls().size();
                ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, chatResponse);
                List<Message> conversationHistory = toolExecutionResult.conversationHistory();
                int historySize = conversationHistory.size();
                int startIndex = Math.max(historySize - 1, historySize - toolCallCount);
                List<Message> toolCallResults = conversationHistory.subList(startIndex, historySize);
                for (Message toolCallResult : toolCallResults) {
                    previousMessageId = saveAndSinkMessage(options.getThreadId(), toolCallResult, previousMessageId,
                            sink);
                }
                prompt = new Prompt(conversationHistory, chatOptions);
                if (iterationLeft <= 0) {
                    sink.error(new MaxIterationReachedException(
                            "Max iterations " + options.getMaxIterations() + " reached"));
                    return;
                }
                if (stream) {
                    Flux<ChatResponse> chatResponseFlux = prepareChatClient(options, prompt).stream().chatResponse();
                    AssistantMessage aiMessage = iterateResponseParts(sink, chatResponseFlux);
                    chatResponse = buildResponseFromAssistantMessage(aiMessage);
                } else {
                    chatResponse = prepareChatClient(options, prompt).call().chatResponse();
                }
                iterationLeft--;
                if (chatResponse == null) {
                    sink.error(new IllegalStateException("chatResponse is null"));
                    return;
                }
                Message responseMessage = chatResponse.getResult().getOutput();
                previousMessageId = saveAndSinkMessage(options.getThreadId(), responseMessage, previousMessageId,
                        sink);
            }
            sink.complete();
        });
    }

    /**
     * 过滤掉名称或参数为空的工具调用
     */
    private void filterValidToolCalls(ChatResponse chatResponse) {
        List<AssistantMessage.ToolCall> toolCalls = chatResponse.getResult().getOutput().getToolCalls();
        List<AssistantMessage.ToolCall> validToolCalls = toolCalls.stream()
                .filter(toolCall -> StringUtils.isNoneBlank(toolCall.name())
                        && StringUtils.isNoneBlank(toolCall.arguments()))
                .toList();

        // 更新原始列表
        List<AssistantMessage.ToolCall> mutableToolCalls = chatResponse.getResult().getOutput().getToolCalls();
        mutableToolCalls.clear();
        mutableToolCalls.addAll(validToolCalls);
    }

    private static ChatResponse buildResponseFromAssistantMessage(AssistantMessage firstMessage) {
        return ChatResponse.builder()
                .generations(List.of(new Generation(firstMessage))).build();
    }

    private static AssistantMessage iterateResponseParts(FluxSink<ReActAgentEvent> sink,
                                                         Flux<ChatResponse> chatResponseFlux) {
        StringBuilder accumulatedText = new StringBuilder();
        List<AssistantMessage.ToolCall> toolCalls = new ArrayList<>();
        chatResponseFlux.doOnNext(partResponse -> {
            AssistantMessage output = partResponse.getResult().getOutput();
            String text = output.getText();
            if (text != null && !text.isEmpty()) {
                accumulatedText.append(text);
                sink.next(new AssistantTextPartEvent(text));
            }
            if (partResponse.hasToolCalls()) {
                toolCalls.addAll(output.getToolCalls());
            }
        }).blockLast();
        String text = accumulatedText.toString();
        return AssistantMessage.builder().content(text).toolCalls(toolCalls).build();
    }

    private ChatClient.ChatClientRequestSpec prepareChatClient(RunAgentOptions options, Prompt prompt) {
        return chatClient.prompt(prompt)
                .toolContext(options.getContext());
    }

    private String saveAndSinkMessage(String threadId, Message message, String previousMessageId,
                                      FluxSink<ReActAgentEvent> sink) {
        String id = IdGenerator.generateId();
        if (branchMessageSaver != null) {
            branchMessageSaver.save(threadId, new BranchMessageItem(message, id, previousMessageId, Map.of()));
        }
        sink.next(new LlmMessageEvent(message, id));
        return id;
    }

    private static List<Message> contactMessages(Message systemMessage, List<Message> messageHistory,
                                                 List<Message> newMessages) {
        List<Message> result = new ArrayList<>();
        if (systemMessage != null) {
            result.add(systemMessage);
        }
        if (messageHistory != null) {
            result.addAll(messageHistory);
        }
        if (newMessages != null) {
            result.addAll(newMessages);
        }
        return result;
    }

    private Message buildSystemMessage(RunAgentOptions options) {
        if (systemPromptProvider == null) {
            return null;
        }
        String systemPrompt = this.systemPromptProvider.getSystemPrompt(options.getContext());
        return SystemMessage.builder().text(systemPrompt).build();
    }

    private List<Message> fetchMessageHistory(RunAgentOptions options) {
        if (branchMessageSaver == null) {
            return List.of();
        }
        if (options.getThreadId() == null) {
            throw new IllegalArgumentException("Thread id is required when message saver is provided");
        }
        return branchMessageSaver.getLatestMessages(options.getThreadId(), options.getMessageHistoryWindowSize(),
                options.getPreviousMessageId());
    }

    private static ToolCallingChatOptions buildChatOptions(RunAgentOptions runAgentOptions) {
        ChatOptions chatOptions = runAgentOptions.getChatOptions();
        if (chatOptions == null) {
            return ToolCallingChatOptions.builder().internalToolExecutionEnabled(false).build();
        }
        ToolCallingChatOptions ret = ChatOptionUtils.copyToToolCallingChatOptions(chatOptions);
        ret.setInternalToolExecutionEnabled(false);
        return ret;
    }

    public static class Builder implements ReActAgent.Builder {
        private final ChatClient chatClient;
        private BranchMessageSaver branchMessageSaver;
        private SystemPromptProvider systemPromptProvider;

        public Builder(ChatClient chatClient) {
            this.chatClient = chatClient;
        }

        @Override
        public Builder messageSaver(BranchMessageSaver branchMessageSaver) {
            this.branchMessageSaver = branchMessageSaver;
            return this;
        }

        @Override
        public Builder systemPromptProvider(SystemPromptProvider systemPromptProvider) {
            this.systemPromptProvider = systemPromptProvider;
            return this;
        }

        @Override
        public Builder systemPrompt(String systemPrompt) {
            return this.systemPromptProvider(new FixedSystemPromptProvider(systemPrompt));
        }

        @Override
        public ReActAgent build() {
            return new DefaultReActAgent(chatClient, branchMessageSaver, systemPromptProvider);
        }

    }

    @Override
    public List<BranchMessageItem> getBranchMessages(String threadId) {
        if (branchMessageSaver == null) {
            throw new UnsupportedOperationException("Branch message saver is not set. So messages were not saved.");
        }
        return branchMessageSaver.getAllMessages(threadId);
    }
}
