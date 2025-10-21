package com.toryu.iims.common.result;

import com.toryu.iims.common.utils.ResultUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 后端统一返回结果
 * @param <T>
 */
@Data
public class Result<T> implements Serializable {

    private Integer code; //编码：1成功，0和其它数字为失败
    private String msg; //错误信息
    private Boolean success; //true成功，false失败
    private Integer errCode;//错误码
    private T data; //数据

    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.code = 1;
        result.success = true;
        result.errCode = 0;
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<>();
        result.data = object;
        result.code = 1;
        result.success = true;
        result.errCode = 0;
        return result;
    }

    public static <T> Result<T> error(String msg, Integer errCode) {
        Result<T> result = new Result<>();
        result.msg = msg;
        result.code = 0;
        result.success = false;
        result.errCode = errCode;
        return result;
    }

    public static <T> Result<T> fromBoolean(boolean isSuccess) {
        if (isSuccess) {
            return success();
        } else {
            return error("操作失败", 500);
        }
    }

    public static <T> Result<Map<String, Object>> successWithNonNull(T object) {
        Result<Map<String, Object>> result = new Result<>();
        Map<String, Object> nonNullProperties = ResultUtil.filterNonNullProperties(object);
        result.setData(nonNullProperties);
        result.setCode(1);
        result.setSuccess(true);
        result.setErrCode(0);
        return result;
    }

}
