package com.toryu.iims.ai.rag.service;

import org.springframework.ai.vectorstore.VectorStore;

public interface CustomizeVectorStoreService {

    VectorStore loadMilvusVectorStore(String dbName, String collectionName);

}
