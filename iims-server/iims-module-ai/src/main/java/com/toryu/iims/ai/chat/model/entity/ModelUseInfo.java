package com.toryu.iims.ai.chat.model.entity;

import com.toryu.iims.ai.rag.enums.FileModelTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelUseInfo {

    private List<String> context;

    private FileModelTypeEnum type;

}
