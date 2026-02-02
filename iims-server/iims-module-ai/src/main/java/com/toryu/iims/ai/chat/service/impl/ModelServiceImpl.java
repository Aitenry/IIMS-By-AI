package com.toryu.iims.ai.chat.service.impl;

import com.toryu.iims.ai.chat.model.entity.ChatApi;
import com.toryu.iims.ai.chat.model.entity.ModelChatOptions;
import com.toryu.iims.ai.chat.service.AiChatModelsService;
import com.toryu.iims.ai.chat.service.ModelService;
import com.toryu.iims.ai.rag.utils.AESEncryptionUtil;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.ollama.api.OllamaEmbeddingOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ModelServiceImpl implements ModelService {

    public final AiChatModelsService aiChatModelsService;

    public ModelServiceImpl(AiChatModelsService aiChatModelsService) {
        this.aiChatModelsService = aiChatModelsService;
    }


    @Override
    public OllamaChatModel getOllamaChatModel(OllamaApi ollamaApi, OllamaChatOptions ollamaOptions) {
        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(ollamaOptions).build();
    }

    @Override
    public OpenAiChatModel getOpenAIChatModel(OpenAiApi openAiApi, OpenAiChatOptions openAiOptions) {
        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(openAiOptions).build();
    }

    @Override
    public ChatModel getDeepSeekChatModel(DeepSeekApi deepSeekApi, DeepSeekChatOptions deepSeekChatOptions) {
        return DeepSeekChatModel.builder()
                .deepSeekApi(deepSeekApi)
                .defaultOptions(deepSeekChatOptions).build();
    }

    @Override
    public ChatModel getChatModel(Long modelId, ModelChatOptions options) {
        ChatApi chatApi = aiChatModelsService.selectModelById(modelId);
        Integer maxTokens = options.getMaxTokens();
        if (Objects.isNull(maxTokens)) {
            maxTokens = chatApi.getToken();
        }
        return switch (chatApi.getType()) {
            case AGENT -> null;
            case OLLAMA -> getOllamaChatModel(OllamaApi.builder().baseUrl(chatApi.getUrl()).build(), OllamaChatOptions.builder().model(chatApi.getName()).topP(options.getTopP())
                    .temperature(options.getTemperature()).frequencyPenalty(options.getFrequencyPenalty())
                    .numCtx(maxTokens).presencePenalty(options.getPresencePenalty()).build());
            case OPENAI -> getOpenAIChatModel(OpenAiApi.builder()
                            .apiKey(Objects.requireNonNullElse(AESEncryptionUtil.decrypt(chatApi.getKey()), ""))
                            .baseUrl(chatApi.getUrl()).build(), OpenAiChatOptions.builder().model(chatApi.getName()).topP(options.getTopP())
                            .temperature(options.getTemperature()).frequencyPenalty(options.getFrequencyPenalty())
                            .maxTokens(maxTokens).presencePenalty(options.getPresencePenalty()).build());
            case DEEPSEEK -> getDeepSeekChatModel(DeepSeekApi.builder()
                            .apiKey(Objects.requireNonNullElse(AESEncryptionUtil.decrypt(chatApi.getKey()), ""))
                            .baseUrl(chatApi.getUrl()).build(), DeepSeekChatOptions.builder().model(chatApi.getName()).topP(options.getTopP())
                            .temperature(options.getTemperature()).frequencyPenalty(options.getFrequencyPenalty())
                            .maxTokens(maxTokens).presencePenalty(options.getPresencePenalty()).build());
        };
    }

    @Override
    public EmbeddingModel getOllamaEmbeddingModel(OllamaApi ollamaApi, OllamaEmbeddingOptions ollamaOptions) {
        return OllamaEmbeddingModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(ollamaOptions).build();
    }

    @Override
    public EmbeddingModel getOpenAiEmbeddingModel(OpenAiApi openAiApi, MetadataMode metadataMode,
                                                  OpenAiEmbeddingOptions openAiOptions) {
        return new OpenAiEmbeddingModel(openAiApi, metadataMode, openAiOptions);
    }

    @Override
    public EmbeddingModel getEmbeddingModel(Long modelId, MetadataMode metadataMode, ModelChatOptions options) {
        ChatApi chatApi = aiChatModelsService.selectModelById(modelId);
        return switch (chatApi.getType()) {
            case AGENT, DEEPSEEK -> null;
            case OLLAMA -> getOllamaEmbeddingModel(OllamaApi.builder()
                    .baseUrl(chatApi.getUrl()).build() , OllamaEmbeddingOptions.builder()
                    .model(chatApi.getName()).build());
            case OPENAI -> getOpenAiEmbeddingModel(OpenAiApi.builder()
                    .apiKey(Objects.requireNonNull(AESEncryptionUtil.decrypt(chatApi.getKey())))
                    .baseUrl(chatApi.getUrl()).build(), metadataMode, OpenAiEmbeddingOptions.builder()
                    .model(chatApi.getName()).build());
        };
    }

}
