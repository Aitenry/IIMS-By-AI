package com.toryu.iims.integral.model.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindCommentListDTO {

    @NotBlank(message = "路由地址不能为空")
    private String routerUrl;

}