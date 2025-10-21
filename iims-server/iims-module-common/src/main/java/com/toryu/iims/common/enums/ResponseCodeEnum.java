package com.toryu.iims.common.enums;

import com.toryu.iims.common.exception.BaseExceptionInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCodeEnum implements BaseExceptionInterface {

    // ----------- 通用异常状态码 -----------
    SYSTEM_ERROR("10000", "出错啦，后台小哥正在努力修复中..."),
    PARAM_NOT_VALID("10001", "参数错误"),


    // ----------- 业务异常状态码 -----------
    LOGIN_FAIL("20000", "登录失败"),
    USERNAME_OR_PWD_ERROR("20001", "用户名或密码错误"),
    UNAUTHORIZED("20002", "无访问权限，请先登录！"),
    USERNAME_NOT_FOUND("20003", "该用户不存在"),
    FORBIDDEN("20004", "演示账号仅支持查询操作！"),
    CATEGORY_NAME_IS_EXISTED("20005", "该分类已存在，请勿重复添加！"),
    TAG_CANT_DUPLICATE("20006", "请勿添加表中已存在的标签！"),
    TAG_NOT_EXISTED("20007", "该标签不存在！"),
    FILE_UPLOAD_FAILED("20008", "文件上传失败！"),
    CATEGORY_NOT_EXISTED("20009", "提交的分类不存在！"),
    ARTICLE_NOT_FOUND("20010", "该文章不存在！"),
    CATEGORY_CAN_NOT_DELETE("20011", "该分类下包含文章，请先删除对应文章，才能删除！"),
    TAG_CAN_NOT_DELETE("20012", "该标签下包含文章，请先删除对应文章，才能删除！"),
    WIKI_NOT_FOUND("20013", "该知识库不存在"),
    NOT_QQ_NUMBER("20014", "QQ 号格式不正确"),
    COMMENT_CONTAIN_SENSITIVE_WORD("20015", "评论内容中包含敏感词，请重新编辑后再提交"),
    COMMENT_WAIT_EXAMINE("20016", "评论已提交, 等待博主审核通过"),
    COMMENT_NOT_FOUND("20017", "该评论不存在"),
    COMMENT_STATUS_NOT_WAIT_EXAMINE("20018", "该评论未处于待审核状态"),
    ;

    // 异常码
    private final String errorCode;
    // 错误信息
    private final String errorMessage;

}
