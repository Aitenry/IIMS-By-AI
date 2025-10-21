package com.toryu.iims.ai.chat.model.vo;

import com.toryu.iims.ai.rag.model.entity.Metadata;
import com.toryu.iims.ai.rag.model.entity.MetadataScore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocMetadataVO implements Serializable {

    private List<MetadataScore> score;

    private Metadata metadata;

}
