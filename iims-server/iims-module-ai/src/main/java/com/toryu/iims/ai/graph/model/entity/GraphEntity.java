package com.toryu.iims.ai.graph.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GraphEntity {

    private Long id;

    private String entity;

    private String description;

}
