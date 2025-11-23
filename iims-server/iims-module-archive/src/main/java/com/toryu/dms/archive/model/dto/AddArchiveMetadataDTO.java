package com.toryu.dms.archive.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AddArchiveMetadataDTO extends ArchiveMetadataDTO {

    @ApiModelProperty("档案归属ID[档案菜单ID]")
    private Integer metadataOwnership;

}
