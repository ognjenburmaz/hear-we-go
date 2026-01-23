package com.streaming.ratings.service.impl;

import com.streaming.common.grpc.ContentServiceGrpcGrpc;
import com.streaming.common.grpc.SongExistsResponse;
import com.streaming.common.grpc.SongIdRequest;
import com.streaming.ratings.service.SongValidationService;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class SongValidationServiceImpl implements SongValidationService {

    @GrpcClient("content-service")
    private ContentServiceGrpcGrpc.ContentServiceGrpcBlockingStub contentStub;

    public boolean exists(String songId) {
        try {
            SongIdRequest request = SongIdRequest.newBuilder().setSongId(songId).build();
            SongExistsResponse response = contentStub.checkSongExists(request);
            return response.getExists();
        } catch (Exception e) {
            return false;
        }
    }
}