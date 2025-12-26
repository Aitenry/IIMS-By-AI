package com.toryu.iims.ai.tools.factory;

// 抽象工具接口
public interface AITool {
    /**
     * 获取工具实例
     */
    Object getToolInstance();
    
    /**
     * 获取工具名称
     */
    String getToolName();
    
    /**
     * 判断是否启用该工具
     */
    default boolean isEnabled() {
        return true;
    }
}