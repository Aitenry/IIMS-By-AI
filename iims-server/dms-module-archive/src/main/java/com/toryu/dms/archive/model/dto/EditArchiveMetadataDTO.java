package com.toryu.dms.archive.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EditArchiveMetadataDTO extends ArchiveMetadataDTO {

    @ApiModelProperty("档案ID")
    private Long id;

}
