package com.toryu.iims.ai.rag.factory.impl;

import com.toryu.iims.ai.rag.enums.DomProcessEnum;
import com.toryu.iims.ai.rag.factory.DocumentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DocumentServiceFactory {
    private final Map<DomProcessEnum, DocumentService> services;

    public DocumentServiceFactory(List<DocumentService> serviceList) {
        this.services = serviceList.stream()
                .collect(Collectors.toMap(
                        DocumentService::getType,
                        Function.identity(),
                        (existing, replacement) -> existing
                ));
    }

    public DocumentService getService(DomProcessEnum type) {
        return Optional.ofNullable(services.get(type))
                .orElseThrow(() -> new IllegalArgumentException("No service found for type: " + type));
    }
}