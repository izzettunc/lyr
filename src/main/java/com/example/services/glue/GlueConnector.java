package com.example.services.glue;

import org.apache.commons.lang3.StringUtils;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glue.GlueClient;
import software.amazon.awssdk.services.glue.model.ListSessionsRequest;
import software.amazon.awssdk.services.glue.model.ListSessionsResponse;
import java.util.List;

public class GlueConnector {
    private static GlueClient client;
    private static GlueConnector instance;

    private GlueConnector() {
        client = buildClient();
    }

    public static GlueConnector getInstance() {
        if (instance == null) {
            instance = new GlueConnector();
        }

        return instance;
    }

    public List<ListSessionsResponse> getSessionHistory(){
        return client.listSessionsPaginator(ListSessionsRequest.builder().build()).stream().toList();
    }


    private static GlueClient buildClient() {
        if (!StringUtils.isBlank(System.getenv(SdkSystemSetting.AWS_ACCESS_KEY_ID.environmentVariable()))) {
            return GlueClient.builder()
                    .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                    .region(Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable())))
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        } else {
            return GlueClient.builder()
                    .credentialsProvider(ProfileCredentialsProvider.create())
                    .region(Region.EU_WEST_1)
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        }
    }
}
