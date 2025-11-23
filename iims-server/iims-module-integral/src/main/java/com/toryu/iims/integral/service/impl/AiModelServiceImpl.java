package com.toryu.iims.integral.service.impl;

import com.github.pagehelper.Page;
import com.toryu.iims.ai.chat.model.dto.ModelPageQueryDTO;
import com.toryu.iims.ai.chat.model.vo.ModelVO;
import com.toryu.iims.ai.chat.service.AiChatModelsService;
import com.toryu.iims.ai.chat.service.AiChatSettingService;
import com.toryu.iims.common.model.vo.BaseAdminInfoVO;
import com.toryu.iims.common.result.PageResult;
import com.toryu.iims.integral.service.AdminService;
import com.toryu.iims.integral.service.AiModelService;
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

    public final AiChatModelsService aiChatModelsService;

    public final AiChatSettingService aiChatSettingService;

    public final AdminService adminService;

    public AiModelServiceImpl(AiChatModelsService aiChatModelsService, AiChatSettingService aiChatSettingService, AdminService adminService) {
        this.aiChatModelsService = aiChatModelsService;
        this.aiChatSettingService = aiChatSettingService;
        this.adminService = adminService;
    }

    @Override
    public PageResult pageQuery(ModelPageQueryDTO modelPageQueryDTO) {
        try (Page<ModelVO> modelVOS = aiChatModelsService.pageQuery(modelPageQueryDTO)) {
            long total = modelVOS.getTotal();
            List<ModelVO> results = modelVOS.getResult();
            results.forEach(modelVO -> {
                Long modelId = modelVO.getId();
                List<Long> userIds = aiChatSettingService.selectUserIdByModelId(modelId);
                List<BaseAdminInfoVO> adminBases = adminService.getAdminBaseInfoByIds(userIds);
                modelVO.setUsers(adminBases);
            });
            return new PageResult(total, results);
        }

    }

}
