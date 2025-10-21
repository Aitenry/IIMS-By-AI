package com.toryu.iims.ai.rag.handle;

import com.toryu.iims.ai.chat.model.entity.ModelUseInfo;
import com.toryu.iims.common.model.entity.file.FileWarehouse;

public interface PromptHandler {
    ModelUseInfo handle(FileWarehouse file);
}