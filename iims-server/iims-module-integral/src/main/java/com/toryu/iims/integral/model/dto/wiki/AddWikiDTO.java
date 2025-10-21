package com.toryu.iims.integral.model.dto.wiki;

import com.toryu.iims.common.enums.WikiTypeEnum;
import io.swagger.annotations.ApiModel;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "新增知识库 VO")
public class AddWikiDTO {

    @NotBlank(message = "知识库标题不能为空")
    @Length(min = 1, max = 20, message = "知识库标题字数需大于 1 小于 20")
    private String title;

    @NotBlank(message = "知识库摘要不能为空")
    private String summary;

    @NotNull(message = "知识库封面不能为空")
    private Long cover;

    @NotNull(message = "知识库类型不能为空")
    private WikiTypeEnum type;

}
