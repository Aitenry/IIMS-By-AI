package com.toryu.iims.ai.rag.handle.work;

import com.toryu.iims.ai.rag.aspect.FileTypeHandler;
import com.toryu.iims.ai.rag.handle.PromptHandler;
import com.toryu.iims.ai.chat.model.entity.ModelUseInfo;
import com.toryu.iims.common.model.entity.file.FileWarehouse;
import org.springframework.stereotype.Component;

@Component
@FileTypeHandler({"ppt", "pptx", "pdf", "md", "docx", "doc", "csv", "xls", "xlsx"})
public class DocumentPromptHandler implements PromptHandler {

    @Override
    public ModelUseInfo handle(FileWarehouse file) {
        // 根据具体文件类型执行逻辑
        return ModelUseInfo.builder().build();
    }
}