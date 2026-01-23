package com.streaming.subscriptions.service.impl;

import com.streaming.common.grpc.ArtistExistsResponse;
import com.streaming.common.grpc.ArtistIdRequest;
import com.streaming.common.grpc.ContentServiceGrpcGrpc;
import com.streaming.subscriptions.service.ContentValidationService;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class ContentValidationServiceImpl implements ContentValidationService {

    @GrpcClient("content-service")
    private ContentServiceGrpcGrpc.ContentServiceGrpcBlockingStub contentStub;

    public boolean doesArtistExist(String artistId) {
        try {
            ArtistIdRequest request = ArtistIdRequest.newBuilder()
                    .setArtistId(artistId)
                    .build();

            ArtistExistsResponse response = contentStub.checkArtistExists(request);
            return response.getExists();
        } catch (Exception e) {
            System.err.println("gRPC Call failed: " + e.getMessage());
            return false;
        }
    }
}