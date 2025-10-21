package com.toryu.iims.common.handler;

import com.alibaba.fastjson.JSON;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JsonToListTypeHandler extends BaseTypeHandler<List<Long>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<Long> parameter, JdbcType jdbcType) throws SQLException {
        // 将 List<Long> 转换为 JSON 字符串并设置到 PreparedStatement 中
        ps.setString(i, JSON.toJSONString(parameter));
    }

    @Override
    public List<Long> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        // 从 ResultSet 中读取 JSON 字符串并转换为 List<Long>
        String json = rs.getString(columnName);
        return json == null ? null : JSON.parseArray(json, Long.class);
    }

    @Override
    public List<Long> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        // 从 ResultSet 中读取 JSON 字符串并转换为 List<Long>
        String json = rs.getString(columnIndex);
        return json == null ? null : JSON.parseArray(json, Long.class);
    }

    @Override
    public List<Long> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        // 从 CallableStatement 中读取 JSON 字符串并转换为 List<Long>
        String json = cs.getString(columnIndex);
        return json == null ? null : JSON.parseArray(json, Long.class);
    }
}