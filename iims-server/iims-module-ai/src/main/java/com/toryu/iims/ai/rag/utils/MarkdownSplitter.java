package com.toryu.iims.ai.rag.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.ai.document.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownSplitter {

    /**
     * 根据Markdown标题分块，包含从顶级标题到当前标题的所有内容，并跳过代码块中的内容。
     */
    public static List<Document> splitByHeadersWithHierarchy(String markdownText, Map<String, Object> metadata) {
        // 定义正则表达式匹配Markdown标题
        Pattern headerPattern = Pattern.compile("^(#+)\\s+(.*)", Pattern.MULTILINE);
        // 定义正则表达式匹配代码块
        Pattern codeBlockPattern = Pattern.compile("(```[\\s\\S]*?```)", Pattern.DOTALL);

        Matcher matcher = headerPattern.matcher(markdownText);
        Matcher codeBlockMatcher = codeBlockPattern.matcher(markdownText);

        List<Document> documents = new ArrayList<>();
        int lastMatchEnd = 0; // 上一次匹配结束的位置
        List<String> headerStack = new ArrayList<>(); // 存储从顶级标题到当前标题的所有标题
        StringBuilder currentContent = new StringBuilder();

        // 跳过代码块的索引范围
        List<int[]> codeBlockRanges = new ArrayList<>();
        while (codeBlockMatcher.find()) {
            codeBlockRanges.add(new int[]{codeBlockMatcher.start(), codeBlockMatcher.end()});
        }

        int documentIndex = 0; // 位置索引

        while (matcher.find()) {
            // 检查当前匹配是否在代码块范围内
            boolean isInCodeBlock = false;
            for (int[] range : codeBlockRanges) {
                if (matcher.start() >= range[0] && matcher.end() <= range[1]) {
                    isInCodeBlock = true;
                    break;
                }
            }
            if (isInCodeBlock) {
                continue; // 跳过代码块中的标题
            }

            // 如果当前已经有标题，则保存上一个块
            if (!headerStack.isEmpty()) {
                // 将从上一次匹配结束位置到当前标题开始位置之间的内容作为正文
                String bodyContent = markdownText.substring(lastMatchEnd, matcher.start()).trim();
                currentContent.append(bodyContent).append("\n"); // 添加正文内容

                // 创建并添加文档块
                String fullContent = "# " + String.join(" > ", headerStack) + "\n\n" + currentContent.toString().trim();
                if (!bodyContent.isEmpty()) {
                    Map<String, Object> docMetadata = new HashMap<>(metadata);
                    docMetadata.put("chunk_index", documentIndex++);
                    String content = fullContent.trim();
                    String contentKey = DigestUtils.md5Hex(content);
                    docMetadata.put("chunk_key", contentKey);
                    documents.add(new Document(content, docMetadata));
                }
                currentContent.setLength(0); // 清空内容
            }

            // 更新标题栈
            String headerLevel = matcher.group(1); // 获取标题的层级（如 "#", "##"）
            String headerText = matcher.group(2).trim(); // 获取标题文本
            int level = headerLevel.length(); // 计算标题层级（如 "#" 对应 1，"##" 对应 2）

            // 根据当前标题层级更新标题栈
            if (level <= headerStack.size()) {
                // 如果当前标题层级小于或等于栈的大小，则截断栈
                headerStack = headerStack.subList(0, level - 1);
            }
            headerStack.add(headerText); // 添加当前标题到栈中

            // 更新起始位置
            lastMatchEnd = matcher.end();
        }

        // 处理最后一个块（包括最后一个标题及其后续内容）
        if (lastMatchEnd < markdownText.length()) {
            String remainingContent = markdownText.substring(lastMatchEnd).trim();
            currentContent.append(remainingContent); // 添加剩余内容
        }
        if (!headerStack.isEmpty()) {
            String fullContent = "# " + String.join(" > ", headerStack) + "\n\n" + currentContent.toString().trim();
            Map<String, Object> docMetadata = new HashMap<>(metadata);
            docMetadata.put("chunk_index", ++documentIndex);
            String content = fullContent.trim();
            String contentKey = DigestUtils.md5Hex(content);
            docMetadata.put("chunk_key", contentKey);
            documents.add(new Document(content, docMetadata));
        }

        return documents;
    }
}