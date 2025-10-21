package com.toryu.iims.integral.model.dto.wiki;

import com.toryu.iims.common.enums.DocumentTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateWikiCatalogItemDTO {

    /**
     * 目录 ID
     */
    @NotNull(message = "目录 ID 不能为空")
    private Long id;

    private Long docId;

    private DocumentTypeEnum type;

    @NotBlank(message = "目录标题不能为空")
    private String title;

    private Integer sort;

    private Integer level;

    private Boolean isEmbedding;

    /**
     * 子目录
     */
    @Valid
    private List<UpdateWikiCatalogItemDTO> children;

}
