package com.toryu.iims.ai.rag.factory;

import com.toryu.iims.common.enums.DocumentTypeEnum;
import org.springframework.ai.document.Document;

import java.util.List;

public interface DocTypeService {

    DocumentTypeEnum getType();
    List<Document> getDocument(Long id);
    String getName(Long id);

}
