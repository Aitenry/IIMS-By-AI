package com.toryu.iims.ai.chat.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toryu.iims.ai.agent.react.ReActAgent;
import com.toryu.iims.ai.agent.react.RunAgentOptions;
import com.toryu.iims.ai.agent.react.event.ReActAgentEvent;
import com.toryu.iims.ai.agent.react.memory.BranchMessageSaver;
import com.toryu.iims.ai.agent.react.memory.MemoryBranchMessageSaver;
import com.toryu.iims.ai.chat.model.entity.ChatTool;
import com.toryu.iims.ai.chat.model.entity.MessageData;
import com.toryu.iims.ai.chat.service.ModelWarehouseService;
import com.toryu.iims.ai.tools.factory.AITool;
import com.toryu.iims.ai.tools.factory.ToolFactory;
import com.toryu.iims.common.context.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: Aitenry
 * @Date: 2025/12/12 11:02
 * @Version: v1.0.0
 * @Description: AI Agent 工具类
 **/
@Slf4j
@Component
public class AgentUtil {

    private final ChatUtil chatUtil;
    private final ToolFactory toolFactory;
    private final ModelWarehouseService modelWarehouseService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AgentUtil(ModelWarehouseService modelWarehouseService, ChatUtil chatUtil, ToolFactory toolFactory) {
        this.modelWarehouseService = modelWarehouseService;
        this.chatUtil = chatUtil;
        this.toolFactory = toolFactory;
    }

    public void processStream(Long uuid, Long userId, Long modelId, List<Long> enabledToolIds,
                              List<Message> messages, MessageData messageData,
                              SseEmitter emitter, Map<Long, MessageData> msgMap) {
        StringBuffer aiContent = new StringBuffer();
        messageData.setAiContent(aiContent);
        Flux<ReActAgentEvent> reActAgentEvents = this.getReActAgentEvents(modelId, messages, enabledToolIds);
        reActAgentEvents.subscribe(data -> {
                    try {
                        Object content = data.getContent();
                        String type = data.getType();
                        if (Objects.equals(type, "AssistantTextPartEvent")) {
                            aiContent.append(content.toString());
                        } else if (Objects.equals(type, "LlmMessageEvent")) {
                            // 处理工具调用和响应
                            if (content instanceof AssistantMessage assistantMessage) {
                                assistantMessage.getToolCalls().forEach(toolCall -> {
                                    String toolId = toolCall.id();
                                    boolean exists = false;
                                    for (ChatTool existingTool : messageData.getTools()) {
                                        if (existingTool.getId().equals(toolId)) {
                                            exists = true;
                                            break;
                                        }
                                    }
                                    // 如果不存在则添加
                                    if (!exists) {
                                        ChatTool newTool = new ChatTool();
                                        newTool.setId(toolId);
                                        newTool.setType(toolCall.type());
                                        newTool.setName(toolCall.name());
                                        newTool.setArguments(toolCall.arguments());
                                        messageData.getTools().add(newTool);
                                    }
                                });
                            } else if (content instanceof ToolResponseMessage toolResponseMessage) {
                                toolResponseMessage.getResponses().forEach(responseMessage -> {
                                    String responseId = responseMessage.id();
                                    String responseData = responseMessage.responseData();
                                    // 查找对应的工具并设置响应数据
                                    for (ChatTool tool : messageData.getTools()) {
                                        if (tool.getId().equals(responseId)) {
                                            tool.setResponseData(responseData);
                                            break;
                                        }
                                    }
                                });
                            }
                        }
                        emitter.send(SseEmitter.event().name(type)
                                .id(String.valueOf(uuid)).data(content));
                    } catch (IOException e) {
                        log.error("AI Stream 消息发送出错：", e);
                        BaseContext.setCurrentId(userId);
                        chatUtil.saveAiDialogueAndComplete(msgMap, uuid);
                    }
                },
                error -> {
                    log.error("AI Subscribe 消息发送出错：", error);
                    BaseContext.setCurrentId(userId);
                    chatUtil.saveAiDialogueAndComplete(msgMap, uuid);
                },
                () -> {
                    BaseContext.setCurrentId(userId);
                    chatUtil.saveAiDialogueAndComplete(msgMap, uuid);
                });
    }

    public Flux<ReActAgentEvent> getReActAgentEvents(Long modelId, List<Message> messages, List<Long> enabledToolIds) {
        ChatModel chatModel = modelWarehouseService.getChatModel(modelId);

        // 获取启用的工具实例列表
        List<Object> toolInstances = getEnabledToolInstances(enabledToolIds);

        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultTools(toolInstances.toArray())
                .build();

        BranchMessageSaver messageSaver = new MemoryBranchMessageSaver();

        // 生成包含所有工具信息的系统提示
        String systemPrompt = generateSystemPrompt(toolInstances);

        ReActAgent build = ReActAgent.builder(chatClient)
                .messageSaver(messageSaver)
                .systemPrompt(systemPrompt)
                .build();

        RunAgentOptions options = RunAgentOptions.builder()
                .newMessages(messages)
                .threadId("iims-agent-thread")
                .maxIterations(10)
                .enableStream(true)
                .build();

        return build.run(options)
                .doOnError(error -> {
                    log.error("AI Agent执行错误: {}", error.getMessage(), error);
                });
    }

    private List<Object> getEnabledToolInstances(List<Long> enabledToolIds) {
        // 这里可以根据enabledToolIds来决定启用哪些工具
        // 如果需要根据ID过滤，可以在这里实现
        return toolFactory.getEnabledToolInstances();
    }

    private String generateSystemPrompt(List<Object> toolInstances) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("""
        你是一个自主规划任务智能AI助手，具备使用工具的能力。你可以调用以下工具来帮助用户解决问题。
        每个工具都有特定的功能和参数要求，请严格按照格式调用。

        可用工具列表:
        """);

        List<Map<String, Object>> toolsList = new ArrayList<>();

        for (Object toolInstance : toolInstances) {
            Class<?> clazz = toolInstance.getClass();
            String toolName = getToolNameFromInstance(toolInstance); // 使用工具名称

            // 收集该工具的所有方法信息
            List<Map<String, Object>> methods = Arrays.stream(clazz.getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(Tool.class))
                    .map(this::extractMethodInfo)
                    .collect(Collectors.toList());

            if (!methods.isEmpty()) {
                Map<String, Object> toolInfo = new HashMap<>();
                toolInfo.put("name", toolName);
                toolInfo.put("className", clazz.getSimpleName());
                toolInfo.put("methods", methods);
                toolsList.add(toolInfo);
            }
        }

        // 将工具信息转换为JSON格式并添加到提示词中
        try {
            String toolsJson = objectMapper.writeValueAsString(toolsList);
            prompt.append(toolsJson).append("\n\n");
        } catch (Exception e) {
            log.warn("工具信息JSON序列化失败，使用文本格式: {}", e.getMessage());
            // 如果JSON序列化失败，使用文本格式
            for (Map<String, Object> tool : toolsList) {
                prompt.append(String.format("工具名: %s (%s)\n",
                        tool.get("name"), tool.get("className")));
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> methods = (List<Map<String, Object>>) tool.get("methods");
                for (Map<String, Object> method : methods) {
                    prompt.append(String.format("  - %s: %s\n",
                            method.get("name"), method.get("description")));
                }
            }
        }

        prompt.append("""
        工具调用格式说明:
        1. 当需要使用工具时，请按照指定格式输入参数。
        2. 当工具调用完成后，你会收到结果，然后继续处理或给出最终答案。
        3. 请根据用户的问题和上下文，合理选择和使用工具。
        4. 只有在确实需要工具帮助时才调用工具，否则直接回答用户问题。
        5. 当使用工具之后，必须要输出内容，解读和总结。
        6. 该换行必须换行，该缩进必须缩进，严格遵循markdown语法输出内容。
        """);

        return prompt.toString();
    }

    // 从实例获取工具名称
    private String getToolNameFromInstance(Object toolInstance) {
        // 查找对应的AITool实现
        for (AITool tool : toolFactory.getAllTools()) { // 假设ToolFactory提供了getAllTools方法
            if (tool.getToolInstance() == toolInstance) {
                return tool.getToolName();
            }
        }
        // 如果找不到对应的AITool，使用类名
        return toolInstance.getClass().getSimpleName();
    }

    // 提取方法信息
    private Map<String, Object> extractMethodInfo(Method method) {
        Tool toolAnnotation = method.getAnnotation(Tool.class);

        Map<String, Object> methodInfo = new HashMap<>();
        methodInfo.put("name", method.getName());
        methodInfo.put("description", toolAnnotation.description());

        // 参数信息
        List<Map<String, Object>> parameters = Arrays.stream(method.getParameters())
                .filter(param -> param.isAnnotationPresent(ToolParam.class))
                .map(param -> {
                    ToolParam toolParam = param.getAnnotation(ToolParam.class);
                    Map<String, Object> paramInfo = new HashMap<>();
                    paramInfo.put("name", param.getName());
                    paramInfo.put("description", toolParam.description());
                    paramInfo.put("type", getParameterType(param.getType()));
                    paramInfo.put("required", toolParam.required());
                    return paramInfo;
                })
                .collect(Collectors.toList());

        methodInfo.put("parameters", parameters);
        methodInfo.put("returnType", method.getReturnType().getSimpleName());

        return methodInfo;
    }

    // 获取参数类型
    private String getParameterType(Class<?> type) {
        if (type == int.class || type == Integer.class ||
                type == long.class || type == Long.class ||
                type == short.class || type == Short.class ||
                type == byte.class || type == Byte.class) {
            return "integer";
        } else if (type == float.class || type == Float.class ||
                type == double.class || type == Double.class) {
            return "number";
        } else if (type == boolean.class || type == Boolean.class) {
            return "boolean";
        } else {
            return "string";
        }
    }
}