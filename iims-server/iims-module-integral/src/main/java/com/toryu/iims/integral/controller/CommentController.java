package com.toryu.iims.integral.controller;

import com.toryu.iims.common.result.Result;
import com.toryu.iims.integral.model.dto.comment.FindCommentListDTO;
import com.toryu.iims.integral.model.dto.comment.PublishCommentDTO;
import com.toryu.iims.integral.model.vo.comment.FindCommentListVO;
import com.toryu.iims.integral.service.CommentService;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/iims/comment")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/publish")
    public Result<T> publishComment(@RequestBody PublishCommentDTO dto) {
        return Result.fromBoolean(commentService.publishComment(dto));
    }

    @PostMapping("/list")
    @ApiOperation(value = "获取页面所有评论")
    public Result<FindCommentListVO> findPageComments(@RequestBody FindCommentListDTO dto) {
        return Result.success(commentService.findCommentList(dto));
    }


}