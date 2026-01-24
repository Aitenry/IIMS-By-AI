export interface ParseResult {
    type: "text" | "reasoning";
    content: string;
    isComplete: boolean;
    isExpanded: boolean;
    view?: string
}

/**
 * 解析给定文本中的推理内容
 * 该函数尝试识别文本中关于思考或推理的部分这些部分被特定的标签标记出来
 * 如果提供了自定义标签，则使用这些标签；否则，使用默认标签
 *
 * @param {string} text - 待解析的文本
 * @param {string|string[]} tags - 用于标识推理部分的标签可以是单个标签字符串，也可以是标签字符串数组
 * @returns {ParseResult[]} 包含解析结果数组和思考完成状态的对象
 */
export const parseReasoning = (text: string, tags?: string[]): ParseResult[] => {
    try {
        const defaultTags = ["think", "reason", "reasoning", "thought"];
        const usedTags = tags && tags.length > 0 ? tags : defaultTags;
        const result: ParseResult[] = [];
        const tagPattern = new RegExp(`<(${usedTags.join("|")})>`, "i");
        const closeTagPattern = new RegExp(`</(${usedTags.join("|")})>`, "i");

        let currentIndex = 0;
        let isReasoning = false;
        let hasOpenReasoning = false;

        while (currentIndex < text.length) {
            const openTagMatch = text.slice(currentIndex).match(tagPattern);
            const closeTagMatch = text.slice(currentIndex).match(closeTagPattern);

            if (!isReasoning && openTagMatch) {
                const beforeText = text.slice(
                    currentIndex,
                    currentIndex + (openTagMatch.index || 0)
                );
                if (beforeText.trim()) {
                    result.push({
                        type: "text",
                        content: beforeText.trim(),
                        isComplete: true,
                        isExpanded: false
                    });
                }

                isReasoning = true;
                hasOpenReasoning = true;
                currentIndex += (openTagMatch.index || 0) + openTagMatch[0].length;
                continue;
            }

            if (isReasoning && closeTagMatch) {
                const reasoningContent = text.slice(
                    currentIndex,
                    currentIndex + (closeTagMatch.index || 0)
                );
                if (reasoningContent.trim()) {
                    result.push({
                        type: "reasoning",
                        content: reasoningContent.trim(),
                        isComplete: hasOpenReasoning,
                        isExpanded: false
                    });
                }

                isReasoning = false;
                currentIndex += (closeTagMatch.index || 0) + closeTagMatch[0].length;
                continue;
            }

            if (currentIndex < text.length) {
                const remainingText = text.slice(currentIndex);
                result.push({
                    type: isReasoning ? "reasoning" : "text",
                    content: remainingText.trim(),
                    isComplete: !isReasoning,
                    isExpanded: false
                });
                break;
            }
        }

        return result
    } catch (e) {
        console.error(`Error parsing reasoning: ${(e as Error).message}`);
        return [{
            type: "text",
            content: text,
            isComplete: true,
            isExpanded: false
        }]
    }
};