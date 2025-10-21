package com.toryu.iims.common.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Map;

public class MetadataSerializer extends JsonSerializer<Object> {

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // 将对象序列化为 JSON 字符串
        if (value instanceof Map) {
            gen.writeRawValue(value.toString()); // 直接写入 JSON 格式
        } else {
            gen.writeString(value != null ? value.toString() : null);
        }
    }
}