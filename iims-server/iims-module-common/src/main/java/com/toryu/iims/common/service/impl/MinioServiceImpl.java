package com.toryu.iims.common.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.toryu.iims.common.mapper.FileStorageMapper;
import com.toryu.iims.common.model.entity.file.FileUpload;
import com.toryu.iims.common.model.entity.file.FileWarehouse;
import com.toryu.iims.common.properties.MinioProperties;
import com.toryu.iims.common.service.MinioService;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class MinioServiceImpl implements MinioService {

    private final MinioProperties minioProperties;
    private final MinioClient minioClient;
    private final FileStorageMapper fileStorageMapper;

    public MinioServiceImpl(
            MinioProperties minioProperties, MinioClient minioClient,
            FileStorageMapper fileStorageMapper) {
        this.minioProperties = minioProperties;
        this.minioClient = minioClient;
        this.fileStorageMapper = fileStorageMapper;
    }

    /**
     * 上传文件
     *
     * @param file MultipartFile
     * @return url 访问路径
     * @throws Exception 向外抛异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileUpload uploadFile(MultipartFile file, Integer itemType) throws Exception {
        // 判断文件是否为空
        if (file == null || file.getSize() == 0) {
            log.error("==> 上传文件异常：文件大小为空 ...");
            throw new RuntimeException("文件大小不能为空");
        }

        String fileKey = DigestUtils.md5Hex(file.getInputStream());
        FileWarehouse fileInfoByFileKey = fileStorageMapper.getFileInfoByFileKey(fileKey);
        if (Objects.nonNull(fileInfoByFileKey)) {
            log.info("==> 上传文件已存在：使用快传方案...");
            return FileUpload.builder().fileId(fileInfoByFileKey.getId())
                    .url(minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                            .bucket(fileInfoByFileKey.getFileBucket())
                            .object(fileInfoByFileKey.getFilePath())
                            .method(Method.GET)
                            .build())).build();
        }

        // 文件的原始名称
        String originalFileName = file.getOriginalFilename();
        // 文件的 Content-Type
        String contentType = file.getContentType();

        // 生成存储对象的名称（将 UUID 字符串中的 - 替换成空字符串）
        String key = UUID.randomUUID().toString().replace("-", "");
        // 获取文件的后缀，如 .jpg
        assert originalFileName != null;
        String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));

        // 拼接上文件后缀，即为要存储的文件名
        String fileRename = String.format("%s%s", key, suffix);

        log.info("==> 开始上传文件至 Minio, ObjectName: {}", fileRename);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String datePath = now.format(formatter);

        // 构造对象名称（带模拟的文件夹路径）
        String objectName = String.format("%s/%s/%s", StpUtil.getLoginIdAsLong(), datePath, fileRename);
        // 上传文件至 Minio
        String bucketName = minioProperties.getBucketName();
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(contentType)
                .build());
        FileWarehouse fileWarehouse = FileWarehouse.builder()
                .itemType(itemType)
                .fileName(originalFileName)
                .fileRename(fileRename)
                .filePath(objectName)
                .fileSize(file.getSize())
                .fileType(file.getContentType())
                .fileBucket(bucketName)
                .fileKey(fileKey)
                .fileDate(LocalDateTime.now())
                .build();

        fileStorageMapper.insert(fileWarehouse);

        // 返回文件的访问链接
        log.info("==> 上传文件至 Minio 成功，ObjectName: {}", objectName);
        FileUpload fileUpload = new FileUpload();
        fileUpload.setFileId(fileWarehouse.getId());
        fileUpload.setUrl(
                minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .object(objectName)
                        .method(Method.GET)
                        .build())

        );
        return fileUpload;
    }

    @Override
    public String getPreviewUrl(Long fileId) {
        try {
            String objectName = fileStorageMapper.getObjectNameById(fileId);
            if (StringUtils.isBlank(objectName)) return "";
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(objectName)
                    .method(Method.GET)
                    .build());
        } catch (Exception e) {
            log.error("Error while getting file from MinIO and converting to PreviewUrl", e);
            return null;
        }
    }

    @Override
    public String getBase64EncodedFile(String objectName) {
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .object(objectName)
                        .build())) {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = stream.read(buffer)) != -1) {
                byteStream.write(buffer, 0, bytesRead);
            }
            byte[] fileBytes = byteStream.toByteArray();

            // 将字节数组转为Base64字符串
            return Base64.getEncoder().encodeToString(fileBytes);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException
                 | InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException
                 | XmlParserException e) {
            log.error("Error while getting file from MinIO and converting to Base64", e);
            return null;
        }
    }

    @Override
    public InputStream getInputStream(Long fileId) {
        try {
            String objectName = fileStorageMapper.getObjectNameById(fileId);
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(objectName)
                            .build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException
                 | InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException
                 | XmlParserException e) {
            log.error("Error while getting file from MinIO and converting to InputStream", e);
            return null;
        }
    }

}
