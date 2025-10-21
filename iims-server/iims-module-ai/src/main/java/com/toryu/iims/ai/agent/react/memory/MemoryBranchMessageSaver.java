package com.toryu.iims.ai.agent.react.memory;

import com.toryu.iims.ai.agent.react.message.BranchMessageItem;
import org.springframework.ai.chat.messages.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存中的分支消息保存器实现
 * <p>
 * 使用内存中的数据结构来存储和管理消息分支，支持多线程并发访问。
 * 每个线程的消息以链表形式存储，支持分支和回溯。
 * </p>
 */
public class MemoryBranchMessageSaver implements BranchMessageSaver {

    // 存储所有消息的映射: messageId -> BranchMessageItem
    private final Map<String, BranchMessageItem> messageStore = new ConcurrentHashMap<>();

    // 存储消息链关系的映射: messageId -> previousMessageId
    private final Map<String, String> messageChain = new ConcurrentHashMap<>();

    // 存储每个线程的最新消息ID: threadId -> latestMessageId
    private final Map<String, String> threadLatestMessage = new ConcurrentHashMap<>();

    // 存储每个线程的消息列表: threadId -> List<messageId>
    private final Map<String, List<String>> threadMessages = new ConcurrentHashMap<>();

    @Override
    public void save(String threadId, BranchMessageItem branchMessageItem) {
        if (threadId == null || branchMessageItem == null) {
            throw new IllegalArgumentException("threadId and branchMessageItem cannot be null");
        }

        String messageId = branchMessageItem.id();
        String previousMessageId = branchMessageItem.previousId();

        // 存储消息
        messageStore.put(messageId, branchMessageItem);

        // 存储消息链关系
        if (previousMessageId != null && !previousMessageId.isEmpty()) {
            messageChain.put(messageId, previousMessageId);
        }

        // 更新线程的消息列表
        threadMessages.computeIfAbsent(threadId, k -> new ArrayList<>()).add(messageId);

        // 更新线程的最新消息ID
        threadLatestMessage.put(threadId, messageId);
    }

    @Override
    public String getLatestMessageId(String threadId) {
        if (threadId == null) {
            return null;
        }
        return threadLatestMessage.get(threadId);
    }

    @Override
    public List<BranchMessageItem> getAllMessages(String threadId) {
        if (threadId == null) {
            return List.of();
        }

        List<String> messageIds = threadMessages.get(threadId);
        if (messageIds == null || messageIds.isEmpty()) {
            return List.of();
        }

        List<BranchMessageItem> result = new ArrayList<>();
        for (String messageId : messageIds) {
            BranchMessageItem branchMessageItem = messageStore.get(messageId);
            if (branchMessageItem != null) {
                result.add(branchMessageItem);
            }
        }

        return result;
    }

    @Override
    public List<Message> getLatestMessages(String threadId, int count, String lastMessageId) {
        if (threadId == null || count <= 0) {
            return List.of();
        }

        List<String> messageIds = threadMessages.get(threadId);
        if (messageIds == null || messageIds.isEmpty()) {
            return List.of();
        }

        List<Message> result = new ArrayList<>();

        // 如果指定了 lastMessageId，从该消息开始回溯
        String currentMessageId = lastMessageId;
        if (currentMessageId == null) {
            // 如果没有指定 lastMessageId，从最新消息开始
            currentMessageId = threadLatestMessage.get(threadId);
        }

        // 回溯消息链，收集指定数量的消息
        while (currentMessageId != null && result.size() < count) {
            BranchMessageItem branchMessageItem = messageStore.get(currentMessageId);
            if (branchMessageItem != null) {
                result.add(branchMessageItem.message());
                currentMessageId = messageChain.get(currentMessageId);
            } else {
                break;
            }
        }

        // 反转结果，使其按时间顺序排列（最早的消息在前面）
        Collections.reverse(result);
        return result;
    }

    /**
     * 获取指定线程的消息数量
     * 
     * @param threadId 线程ID
     * @return 消息数量
     */
    public int getMessageCount(String threadId) {
        if (threadId == null) {
            return 0;
        }
        List<String> messageIds = threadMessages.get(threadId);
        return messageIds == null ? 0 : messageIds.size();
    }

    /**
     * 清空指定线程的所有消息
     * 
     * @param threadId 线程ID
     */
    public void clearThread(String threadId) {
        if (threadId == null) {
            return;
        }

        List<String> messageIds = threadMessages.get(threadId);
        if (messageIds != null) {
            // 清理相关的所有数据
            for (String messageId : messageIds) {
                messageStore.remove(messageId);
                messageChain.remove(messageId);
            }
            threadMessages.remove(threadId);
            threadLatestMessage.remove(threadId);
        }
    }

    /**
     * 清空所有数据
     */
    public void clearAll() {
        messageStore.clear();
        messageChain.clear();
        threadLatestMessage.clear();
        threadMessages.clear();
    }
}
