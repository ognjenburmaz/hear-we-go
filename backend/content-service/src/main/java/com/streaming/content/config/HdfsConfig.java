package com.streaming.content.config;

import org.apache.hadoop.fs.FileSystem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URI;

@Configuration
public class HdfsConfig {

    @Value("${HDFS_URI}")
    private String hdfsUri;

    @Bean
    public org.apache.hadoop.conf.Configuration hadoopConfiguration() {
        org.apache.hadoop.conf.Configuration configuration = new org.apache.hadoop.conf.Configuration();
        configuration.set("fs.defaultFS", hdfsUri);
        configuration.set("dfs.client.use.datanode.hostname", "true");
        return configuration;
    }

    @Bean
    public FileSystem fileSystem(org.apache.hadoop.conf.Configuration configuration) throws IOException, InterruptedException {
        return FileSystem.get(URI.create(hdfsUri), configuration, "root");
    }
}