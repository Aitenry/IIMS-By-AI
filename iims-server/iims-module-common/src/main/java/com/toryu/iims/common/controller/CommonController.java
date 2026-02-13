package com.toryu.iims.common.controller;

import com.toryu.iims.common.constant.ErrorCodeConstant;
import com.toryu.iims.common.constant.MessageConstant;
import com.toryu.iims.common.model.entity.file.FileUpload;
import com.toryu.iims.common.model.entity.file.FileWarehouse;
import com.toryu.iims.common.result.Result;
import com.toryu.iims.common.service.FileStorageService;
import com.toryu.iims.common.service.MinioService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/iims/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    private final MinioService minioService;

    private final FileStorageService fileStorageService;

    public CommonController(MinioService minioService, FileStorageService fileStorageService) {
        this.minioService = minioService;
        this.fileStorageService = fileStorageService;
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
    public void fileByShortLink(@PathVariable Long fileId, HttpServletResponse response) {
        try {
            // 从 MinIO 获取 InputStream（或其他方式）
            InputStream inputStream = minioService.getInputStream(fileId);
            if (inputStream == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("File not found.");
                return;
            }

            FileWarehouse objectById = fileStorageService.getObjectById(fileId);
            String fileType = objectById.getFileType();
            response.setContentType(fileType != null ? fileType : "application/octet-stream");

            // 将文件流写入响应输出流
            try (OutputStream out = response.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                out.flush();
            } finally {
                inputStream.close(); // 确保关闭输入流
            }

        } catch (Exception e) {
            log.error("获取文件失败，文件ID: {}", fileId, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                response.getWriter().write("Failed to retrieve file.");
            } catch (IOException ioException) {
                log.error("Failed to write error message to response.", ioException);
            }
        }
    }

}
