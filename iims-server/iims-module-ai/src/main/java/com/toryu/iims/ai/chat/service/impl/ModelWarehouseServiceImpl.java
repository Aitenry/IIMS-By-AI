package com.toryu.iims.ai.chat.service.impl;

import com.toryu.iims.ai.chat.model.entity.ChatApi;
import com.toryu.iims.ai.chat.model.entity.ModelChatOptions;
import com.toryu.iims.ai.chat.service.AiChatModelsService;
import com.toryu.iims.ai.chat.service.ModelWarehouseService;
import com.toryu.iims.ai.rag.utils.AESEncryptionUtil;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Service;

@Service
public class ModelWarehouseServiceImpl implements ModelWarehouseService {

    public final AiChatModelsService aiChatModelsService;

    public ModelWarehouseServiceImpl(AiChatModelsService aiChatModelsService) {
        this.aiChatModelsService = aiChatModelsService;
    }


    @Override
    public OllamaChatModel getOllamaChatModel(OllamaApi ollamaApi, OllamaOptions ollamaOptions) {
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
    public ChatModel getChatModel(Long modelId, ModelChatOptions options) {
        ChatApi chatApi = aiChatModelsService.selectModelById(modelId);
        return switch (chatApi.getType()) {
            case OLLAMA -> getOllamaChatModel(OllamaApi.builder()
                    .baseUrl(chatApi.getUrl()).build() , OllamaOptions.builder().model(chatApi.getName()).topP(options.getTopP())
                    .temperature(options.getTemperature()).frequencyPenalty(options.getFrequencyPenalty())
                    .numCtx(options.getMaxTokens()).presencePenalty(options.getPresencePenalty()).build());
            case OPENAI -> getOpenAIChatModel(OpenAiApi.builder()
                    .apiKey(AESEncryptionUtil.decrypt(chatApi.getKey()))
                    .baseUrl(chatApi.getUrl()).build(),
                    OpenAiChatOptions.builder().model(chatApi.getName()).topP(options.getTopP())
                            .temperature(options.getTemperature()).frequencyPenalty(options.getFrequencyPenalty())
                            .maxTokens(options.getMaxTokens()).presencePenalty(options.getPresencePenalty()).build());
        };
    }

    @Override
    public EmbeddingModel getOllamaEmbeddingModel(OllamaApi ollamaApi, OllamaOptions ollamaOptions) {
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
            case OLLAMA -> getOllamaEmbeddingModel(OllamaApi.builder()
                    .baseUrl(chatApi.getUrl()).build() , OllamaOptions.builder().model(chatApi.getName()).topP(options.getTopP())
                    .temperature(options.getTemperature()).frequencyPenalty(options.getFrequencyPenalty())
                    .numCtx(options.getMaxTokens()).presencePenalty(options.getPresencePenalty()).build());
            case OPENAI -> getOpenAiEmbeddingModel(OpenAiApi.builder()
                    .apiKey(AESEncryptionUtil.decrypt(chatApi.getKey()))
                    .baseUrl(chatApi.getUrl()).build(), metadataMode, OpenAiEmbeddingOptions.builder()
                    .model(chatApi.getName()).build());
        };
    }

}
