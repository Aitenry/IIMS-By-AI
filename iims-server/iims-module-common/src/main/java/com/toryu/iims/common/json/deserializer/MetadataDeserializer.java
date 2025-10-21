package com.toryu.iims.common.json.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

public class MetadataDeserializer extends JsonDeserializer<Object> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object deserialize(JsonParser p, DeserializationContext text) throws IOException {
        // 尝试将字符串解析为 Map<String, Object>
        String value = p.getText();
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(value, Map.class); // 转换为 Map
        } catch (Exception e) {
            // 如果解析失败，返回原始字符串
            return value;
        }
    }
}