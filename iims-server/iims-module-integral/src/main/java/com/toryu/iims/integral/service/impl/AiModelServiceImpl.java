package com.toryu.iims.integral.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.toryu.iims.common.model.vo.BaseAdminInfoVO;
import com.toryu.iims.common.result.PageResult;
import com.toryu.iims.integral.mapper.AiModelMapper;
import com.toryu.iims.integral.model.dto.llm.ModelPageQueryDTO;
import com.toryu.iims.integral.model.vo.llm.ModelVO;
import com.toryu.iims.integral.service.AdminService;
import com.toryu.iims.integral.service.AiModelService;
import com.toryu.iims.integral.service.ChatSettingService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2025/11/23 14:32
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Service
public class AiModelServiceImpl implements AiModelService {

    public final AiModelMapper aiModelMapper;

    public final ChatSettingService chatSettingService;

    public final AdminService adminService;

    public AiModelServiceImpl(AiModelMapper aiModelMapper, ChatSettingService chatSettingService, AdminService adminService) {
        this.aiModelMapper = aiModelMapper;
        this.chatSettingService = chatSettingService;
        this.adminService = adminService;
    }

    @Override
    public PageResult pageQuery(ModelPageQueryDTO modelPageQueryDTO) {
        int page = modelPageQueryDTO.getPage();
        int pageSize = modelPageQueryDTO.getPageSize();
        PageHelper.startPage(page, pageSize);

        try (Page<ModelVO> modelVOS = aiModelMapper.pageQuery(modelPageQueryDTO)) {
            long total = modelVOS.getTotal();
            List<ModelVO> results = modelVOS.getResult();
            results.forEach(modelVO -> {
                Long modelId = modelVO.getId();
                List<Long> userIds = chatSettingService.selectUserIdByModelId(modelId);
                List<BaseAdminInfoVO> adminBases = adminService.getAdminBaseInfoByIds(userIds);
                modelVO.setUsers(adminBases);
            });
            return new PageResult(total, results);
        }

    }

}
