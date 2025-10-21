package com.toryu.iims.common.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.List;

// 自定义序列化器：将 JSON 数组字符串解析为 List<String>
public class IdsSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null || value.isEmpty()) {
            gen.writeNull();
        } else {
            // 将 JSON 数组字符串解析为 List<String>
            ObjectMapper objectMapper = new ObjectMapper();
            List<Long> list = objectMapper.readValue(value, new TypeReference<>() {});
            gen.writeObject(list);
        }
    }
}