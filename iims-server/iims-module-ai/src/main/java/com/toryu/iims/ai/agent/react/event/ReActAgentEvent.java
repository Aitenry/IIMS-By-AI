package com.toryu.iims.ai.agent.react.event;

public interface ReActAgentEvent {
    /**
     * 获取事件类型名称
     */
    default String getType() {
        return this.getClass().getSimpleName();
    }

    /**
     * 获取事件内容
     */
    Object getContent();
}