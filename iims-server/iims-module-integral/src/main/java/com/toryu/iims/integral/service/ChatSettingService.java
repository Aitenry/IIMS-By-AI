package com.toryu.iims.integral.service;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2025/11/23 10:26
 * @Version: v1.0.0
 * @Description: TODO
 **/
public interface ChatSettingService {

    List<Long> selectUserIdByModelId(Long modelId);

}
