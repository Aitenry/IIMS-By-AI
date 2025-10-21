package com.toryu.iims.integral.service.impl;

import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import com.toryu.iims.common.enums.CommentStatusEnum;
import com.toryu.iims.common.model.entity.integral.Comment;
import com.toryu.iims.common.model.vo.BaseAdminInfoVO;
import com.toryu.iims.common.properties.CommentProperties;
import com.toryu.iims.common.result.PageResult;
import com.toryu.iims.integral.convert.CommentConvert;
import com.toryu.iims.integral.mapper.CommentMapper;
import com.toryu.iims.integral.model.dto.comment.*;
import com.toryu.iims.integral.model.vo.comment.FindCommentItemVO;
import com.toryu.iims.integral.model.vo.comment.FindCommentListVO;
import com.toryu.iims.integral.service.AdminService;
import com.toryu.iims.integral.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;

    private final CommentProperties properties;

    private final AdminService adminService;

    public CommentServiceImpl(CommentMapper commentMapper, CommentProperties properties, AdminService adminService) {
        this.commentMapper = commentMapper;
        this.properties = properties;
        this.adminService = adminService;
    }

    @Override
    public PageResult findCommentPageList(FindCommentPageListDTO dto) {
        return null;
    }

    @Override
    public Boolean deleteComment(DeleteCommentDTO dto) {
        return null;
    }

    @Override
    public Boolean examine(ExamineCommentDTO dto) {
        return null;
    }

    @Override
    public Boolean publishComment(PublishCommentDTO dto) {
        String reason = "";
        // 设置默认状态（正常）
        Integer status = CommentStatusEnum.NORMAL.getCode();
        // 如果开启了审核, 设置状态为待审核，等待后台审核通过
        if (properties.getExamine()) {
            status = CommentStatusEnum.WAIT_EXAMINE.getCode();
        }
        String content = dto.getContent();
        if (properties.getSensitive()) {
            // 若包含敏感词，设置状态为审核不通过
            boolean contains = SensitiveWordHelper.contains(content);
            if (contains) {
                status = CommentStatusEnum.EXAMINE_FAILED.getCode();
                // 匹配到的所有敏感词组
                List<String> keywords = SensitiveWordHelper.findAll(content);
                reason = String.format("系统自动拦截，包含敏感词：%s", keywords);
                log.warn("此评论内容中包含敏感词: {}, content: {}", keywords, content);
            } else {
                status = CommentStatusEnum.NORMAL.getCode();
            }
        }
        Comment comment = Comment.builder().parentCommentId(dto.getParentCommentId()).routerUrl(dto.getRouterUrl())
                .replyCommentId(dto.getReplyCommentId()).content(content).status(status).reason(reason).build();
        return commentMapper.insert(comment);
    }

    @Override
    public FindCommentListVO findCommentList(FindCommentListDTO dto) {
        String routerUrl = dto.getRouterUrl();

        // 查询该路由地址下所有评论（仅查询状态正常的）
        List<Comment> commentDOS = commentMapper.selectByRouterUrlAndStatus(routerUrl, CommentStatusEnum.NORMAL.getCode());
        // 总评论数
        Integer total = commentDOS.size();

        List<FindCommentItemVO> vos = null;
        // DO 转 VO
        if (!CollectionUtils.isEmpty(commentDOS)) {
            // 一级评论
            vos = commentDOS.stream()
                    .filter(comment -> Objects.isNull(comment.getParentCommentId())) // parentCommentId 父级 ID 为空，则表示为一级评论
                    .map(CommentConvert.INSTANCE::convertDO2VO)
                    .collect(Collectors.toList());
            HashMap<Long, BaseAdminInfoVO> adminBaseInfoByIds = adminService.getAdminBaseHashInfoByIds(
                    commentDOS.stream().map(Comment::getCreateBy).distinct().collect(Collectors.toList()));
            // 循环设置评论回复数据
            vos.forEach(vo -> {
                Long commentId = vo.getId();
                BaseAdminInfoVO adminBaseInfo = adminBaseInfoByIds.get(vo.getCreateBy());
                vo.setAvatar(adminBaseInfo.getImageUrl());
                vo.setNickname(adminBaseInfo.getUsername());
                List<FindCommentItemVO> childComments = commentDOS.stream()
                        .filter(commentDO -> Objects.equals(commentDO.getParentCommentId(), commentId)) // 过滤出一级评论下所有子评论
                        .sorted(Comparator.comparing(Comment::getCreateTime)) // 按发布时间升序排列
                        .map(commentDO -> {
                            FindCommentItemVO findPageCommentVO = CommentConvert.INSTANCE.convertDO2VO(commentDO);
                            Long replyCommentId = commentDO.getReplyCommentId();
                            // 若二级评论的 replayCommentId 不等于一级评论 ID, 前端则需要展示【回复 @ xxx】，需要设置回复昵称
                            if (!Objects.equals(replyCommentId, commentId)) {
                                // 设置回复用户的昵称
                                Optional<Comment> optionalCommentDO = commentDOS.stream()
                                        .filter(commentDO1 -> Objects.equals(commentDO1.getId(), replyCommentId)).findFirst();
                                optionalCommentDO.ifPresent(comment -> {
                                    BaseAdminInfoVO adminBaseInfoC = adminBaseInfoByIds.get(comment.getCreateBy());
                                    findPageCommentVO.setReplyNickname(adminBaseInfoC.getUsername());
                                });
                            }
                            BaseAdminInfoVO adminBaseInfoC = adminBaseInfoByIds.get(findPageCommentVO.getCreateBy());
                            findPageCommentVO.setNickname(adminBaseInfoC.getUsername());
                            findPageCommentVO.setAvatar(adminBaseInfoC.getImageUrl());
                            return findPageCommentVO;
                        }).collect(Collectors.toList());

                vo.setChildComments(childComments);
            });
        }

        return FindCommentListVO.builder()
                .total(total)
                .comments(vos)
                .build();
    }

}
