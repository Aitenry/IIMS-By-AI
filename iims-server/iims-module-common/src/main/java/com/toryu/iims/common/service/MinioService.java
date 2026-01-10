package com.toryu.iims.common.service;

import com.toryu.iims.common.model.entity.file.FileUpload;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface MinioService {

    FileUpload uploadFile(MultipartFile file, Integer itemType) throws Exception;

    String getPreviewUrl(Long fileId);

    String generateShortLink(Long fileId);

    String getBase64EncodedFile(String objectName);

    InputStream getInputStream(Long fileId);

}
