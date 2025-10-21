package com.toryu.iims.ai.graph.service;

import com.toryu.iims.ai.graph.model.entity.GraphEntity;
import com.toryu.iims.ai.graph.model.entity.GraphRelationship;

import java.util.List;

public interface GenerateGraphService {

    List<GraphEntity> extractEntityByModel(String chunk);

    List<GraphRelationship> generateRelationshipByModel(String chunk, String content);

}
