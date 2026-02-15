package com.toryu.iims.integral.service.impl;

import com.github.pagehelper.Page;
import com.toryu.iims.ai.chat.model.dto.CreateModelDTO;
import com.toryu.iims.ai.chat.model.dto.ModelPageQueryDTO;
import com.toryu.iims.ai.chat.model.dto.UpdateModelDTO;
import com.toryu.iims.ai.chat.model.entity.AiModel;
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

    @Override
    public boolean deleteModel(Long id) {
        return aiChatModelsService.deleteModelById(id);
    }

    @Override
    public boolean updateModel(UpdateModelDTO model) {
        AiModel build = AiModel.builder().id(model.getId()).rename(model.getRename()).description(model.getDescription())
                .token(model.getToken()).modelType(model.getModelType()).type(model.getType())
                .name(model.getName()).url(model.getUrl()).key(model.getKey())
                .build();
        return aiChatModelsService.updateModel(build);
    }

    @Override
    public boolean createModel(CreateModelDTO model) {
        AiModel build = AiModel.builder().rename(model.getRename()).description(model.getDescription())
                .token(model.getToken()).modelType(model.getModelType()).type(model.getType())
                .name(model.getName()).url(model.getUrl()).key(model.getKey())
                .build();
        return aiChatModelsService.insertModel(build);
    }

}
