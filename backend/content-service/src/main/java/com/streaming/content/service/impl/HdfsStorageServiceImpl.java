package com.streaming.content.service.impl;

import com.streaming.content.service.HdfsStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class HdfsStorageServiceImpl implements HdfsStorageService {

    private final FileSystem fileSystem;
    private final String BASE_PATH = "/music/";

    public String saveFile(MultipartFile file) {
        Path directoryPath = new Path(BASE_PATH);
        try {
            if (!fileSystem.exists(directoryPath)) {
                fileSystem.mkdirs(directoryPath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFileName = UUID.randomUUID().toString() + extension;

        Path filePath = new Path(BASE_PATH + uniqueFileName);

        try (InputStream inputStream = file.getInputStream();
             FSDataOutputStream outputStream = fileSystem.create(filePath)) {

            inputStream.transferTo(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("Saved file to HDFS at: {}", filePath);
        return filePath.toString();
    }

    public void deleteFile(String pathString) {
        Path path = new Path(pathString);
        try {
            if (fileSystem.exists(path)) {
                fileSystem.delete(path, false);
                log.info("Deleted file from HDFS: {}", pathString);
            }
        } catch (IOException e) {
            log.error("Error deleting file from HDFS", e);
        }
    }
}