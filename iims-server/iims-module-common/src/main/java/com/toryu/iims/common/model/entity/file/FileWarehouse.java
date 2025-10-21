package com.toryu.iims.common.model.entity.file;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder /*@Builder可以让你类链式的调用你的代码，来初始化你的实例对象*/
@NoArgsConstructor /*注解在类上；为类提供一个无参的构造方法*/
@AllArgsConstructor /*注解在类上；为类提供一个全参的构造方法*/
public class FileWarehouse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("系统类型：ES：0、IIMS：1、DMS：2、EAS：3")
    private Integer itemType;

    @ApiModelProperty("文件初始名称")
    private String fileName;

    @ApiModelProperty("文件重命名")
    private String fileRename;

    @ApiModelProperty("文件存储桶")
    private String fileBucket;

    @ApiModelProperty("文件MD5值")
    private String fileKey;

    @ApiModelProperty("文件存储路径")
    private String filePath;

    @ApiModelProperty("文件大小")
    private Long fileSize;

    @ApiModelProperty("文件类型")
    private String fileType;

    @ApiModelProperty("文件日期")
    private LocalDateTime fileDate;

    @ApiModelProperty("文件摘要")
    private String fileAbstract;

    @ApiModelProperty("文件状态：删除：-1、未使用：0、已使用：1")
    private Integer fileStatus;

    @ApiModelProperty("创建者")
    private Long createBy;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新者")
    private Long updateBy;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

}
