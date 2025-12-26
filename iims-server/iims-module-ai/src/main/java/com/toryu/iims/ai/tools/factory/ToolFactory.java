package com.toryu.iims.ai.tools.factory;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ToolFactory {
    
    private final List<AITool> availableTools;
    
    public ToolFactory(List<AITool> tools) {
        this.availableTools = tools;
    }
    
    public List<Object> getEnabledToolInstances() {
        return availableTools.stream()
                .filter(AITool::isEnabled)
                .map(AITool::getToolInstance)
                .collect(Collectors.toList());
    }

    public List<AITool> getAllTools() {
        return availableTools;
    }
    
    public List<String> getAvailableToolNames() {
        return availableTools.stream()
                .filter(AITool::isEnabled)
                .map(AITool::getToolName)
                .collect(Collectors.toList());
    }

}