package com.streaming.content.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface HdfsStorageService {
    String saveFile(MultipartFile file);

    void deleteFile(String pathString);
}
