package com.toryu.iims.ai.agent.react.memory;

import com.toryu.iims.ai.agent.react.message.BranchMessageItem;
import org.springframework.ai.chat.messages.Message;

import java.util.List;

/**
 * 分支消息保存器接口
 * <p>
 * 该接口定义了用于管理聊天消息分支存储的基本操作，支持消息的保存、查询和获取。
 * 每个消息都有唯一的标识符，并且可以关联到前一个消息，形成消息链。
 * </p>
 */
public interface BranchMessageSaver {

    /**
     * 保存消息到指定的线程中
     * 
     * @param threadId          线程标识符
     * @param branchMessageItem 要保存的消息项，包含消息内容、唯一标识符和前一个消息的标识符
     */
    void save(String threadId, BranchMessageItem branchMessageItem);

    /**
     * 获取指定线程中的最新消息的标识符
     * 
     * @param threadId 线程标识符
     * @return 最新消息的标识符, 如果线程中没有消息, 则返回null
     */
    String getLatestMessageId(String threadId);

    /**
     * 获取指定线程中的所有消息
     * 
     * @param threadId 线程标识符
     * @return 包含所有消息的列表
     */
    List<BranchMessageItem> getAllMessages(String threadId);

    /**
     * 获取指定数量的最新消息
     * 
     * @param id            起始消息的标识符
     * @param count         要获取的消息数量
     * @param lastMessageId 最后一个消息的标识符，用于分页或定位
     * @return 最新的消息列表
     */
    List<Message> getLatestMessages(String id, int count, String lastMessageId);
}
