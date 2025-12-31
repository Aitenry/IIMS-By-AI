package com.toryu.iims.integral.event.subscriber;

import com.toryu.iims.integral.event.WriteWikiDocEvent;
import com.toryu.iims.integral.mapper.WikiCatalogMapper;
import com.toryu.iims.ai.rag.service.MilvusStoreService;
import com.toryu.iims.common.enums.DocumentTypeEnum;
import com.toryu.iims.common.model.entity.integral.WikiCatalog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class WriteWikiDocSubscriber implements ApplicationListener<WriteWikiDocEvent> {

    private final WikiCatalogMapper wikiCatalogMapper;
    private final MilvusStoreService milvusStoreService;

    public WriteWikiDocSubscriber(WikiCatalogMapper wikiCatalogMapper, MilvusStoreService milvusStoreService) {
        this.wikiCatalogMapper = wikiCatalogMapper;
        this.milvusStoreService = milvusStoreService;
    }

    @Override
    @Async("threadPoolTaskExecutor")
    public void onApplicationEvent(WriteWikiDocEvent event) {
        // 在这里处理收到的事件，可以是任何逻辑操作
        Long docId = event.getDocId();
        DocumentTypeEnum type = event.getType();

        // 获取当前线程名称
        String threadName = Thread.currentThread().getName();
        log.info("==> 知识库文章更新向量库: {}", threadName);
        WikiCatalog wikiCatalog = wikiCatalogMapper.selectWikiByDoc(docId, type);
        Boolean isEmbedding = wikiCatalog.getIsEmbedding();
        if (isEmbedding) {
            Long wikiId = wikiCatalog.getWikiId();
            wikiCatalogMapper.updateIsEmbedding(List.of(wikiCatalog.getId()), false);
            milvusStoreService.addDocumentByWiki(wikiId);
            log.info("==> 知识库文章更新向量库内容事件消费成功，wikiId: {}，docId: {}，type: {}", wikiId, docId, type);
        }
    }

}
