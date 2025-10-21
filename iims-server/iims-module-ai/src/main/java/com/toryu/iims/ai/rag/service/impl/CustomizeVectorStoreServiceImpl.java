package com.toryu.iims.ai.rag.service.impl;

import com.toryu.iims.ai.rag.service.CustomizeVectorStoreService;
import com.toryu.iims.ai.chat.service.ModelWarehouseService;
import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomizeVectorStoreServiceImpl implements CustomizeVectorStoreService {

    private final ModelWarehouseService modelWarehouseService;

    public CustomizeVectorStoreServiceImpl(ModelWarehouseService modelWarehouseService) {
        this.modelWarehouseService = modelWarehouseService;
    }

    @Override
    public VectorStore loadMilvusVectorStore(String dbName, String collectionName) {
        EmbeddingModel embeddingModel = modelWarehouseService.getEmbeddingModel(3L);
        MilvusServiceClient milvusServiceClient = new MilvusServiceClient(ConnectParam.newBuilder().build());
        return MilvusVectorStore.builder(milvusServiceClient, embeddingModel)
                .databaseName(dbName)
                .collectionName(collectionName)
                .indexType(IndexType.IVF_FLAT)
                .metricType(MetricType.COSINE)
                .iDFieldName("id")
                .batchingStrategy(new TokenCountBatchingStrategy())
                .build();
    }

}
