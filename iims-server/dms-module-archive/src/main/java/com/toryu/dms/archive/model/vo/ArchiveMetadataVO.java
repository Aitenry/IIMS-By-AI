package com.toryu.dms.archive.model.vo;

import com.toryu.dms.archive.model.entity.ArchiveBaseMetadata;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ArchiveMetadataVO extends ArchiveBaseMetadata {

    @ApiModelProperty("档案摘要")
    private String archivalAbstract;

    @ApiModelProperty("不公共属性")
    private String metadataProperty;

}
