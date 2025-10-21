package com.toryu.iims.ai.rag.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetadataScore implements Serializable {

    private String id;

    private String text;

    private Float score;
}
