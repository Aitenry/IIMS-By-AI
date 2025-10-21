package com.toryu.iims.common.json.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

// 自定义反序列化器：将 List<String> 转换为 JSON 数组字符串
public class IdsDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser p, DeserializationContext text) throws IOException {
        // 从 JSON 中读取 List<String>
        ObjectMapper objectMapper = new ObjectMapper();
        List<?> list = objectMapper.readValue(p, new TypeReference<List<Long>>() {});
        if (list == null || list.isEmpty()) {
            return null;
        }
        // 将 List 转换为 JSON 数组字符串
        return objectMapper.writeValueAsString(list);
    }
}