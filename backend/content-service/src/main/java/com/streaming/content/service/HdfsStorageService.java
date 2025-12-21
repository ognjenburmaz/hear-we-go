package com.streaming.content.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface HdfsStorageService {
    String saveFile(MultipartFile file) throws IOException;

    void deleteFile(String pathString);
}
