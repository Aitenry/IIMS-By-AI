package com.toryu.iims.common.controller;

import com.toryu.iims.common.constant.ErrorCodeConstant;
import com.toryu.iims.common.constant.MessageConstant;
import com.toryu.iims.common.model.entity.file.FileUpload;
import com.toryu.iims.common.result.Result;
import com.toryu.iims.common.service.MinioService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/iims/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    private final MinioService minioService;

    public CommonController(MinioService minioService) {
        this.minioService = minioService;
    }

    /**
     * 文件上传
     *
     * @param file MultipartFile
     * @return Result<String>
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<FileUpload> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("itemType") Integer itemType) {
        log.info("文件上传：{}", file);

        try {
            //文件的请求路径
            FileUpload fileUpload = minioService.uploadFile(file, itemType);
            return Result.success(fileUpload);
        } catch (IOException e) {
            log.error("文件上传失败：{}", e.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Result.error(MessageConstant.UPLOAD_FAILED, ErrorCodeConstant.UPLOAD_FAILED);
    }

    @GetMapping("/file/{fileId}")
    @ApiOperation("根据短链获取文件")
    public void fileByShortLink(@PathVariable Long fileId, HttpServletResponse response) {         response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        try {
            response.sendRedirect(minioService.getPreviewUrl(fileId));
        } catch (Exception e) {
            log.error("下载文件失败，文件ID: {}", fileId, e);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            try {
                response.getWriter().write("File not found or access denied.");
            } catch (IOException ioException) {
                log.error("Failed to write error message to response.", ioException);
            }
        }
    }

}
