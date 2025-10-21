package com.toryu.iims.ai.graph.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GraphRelationship {

    private Long id;

    private Long from;

    private Long to;

    private Integer weight;

    private String relationship;

    private String description;

    private List<String> keywords;

}
