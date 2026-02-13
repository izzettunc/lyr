package com.example.services.ssm;

import org.apache.commons.lang3.StringUtils;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.Parameter;
import software.amazon.awssdk.services.ssm.model.ParameterNotFoundException;

import java.util.Optional;

public class SsmConnector {

    private static SsmClient client;
    private static SsmConnector instance;

    private SsmConnector() {
        client = buildClient();
    }

    public static SsmConnector getInstance() {
        if (null == instance) {
            instance = new SsmConnector();
        }

        return instance;
    }

    public Optional<Parameter> getParameter(String name) {
        try {
            return Optional.of(client.getParameter(GetParameterRequest.builder()
                    .name(name)
                    .build()).parameter());
        } catch (ParameterNotFoundException parameterNotFoundException) {
            return Optional.empty();
        }
    }

    private static SsmClient buildClient() {
        if (!StringUtils.isBlank(System.getenv(SdkSystemSetting.AWS_ACCESS_KEY_ID.environmentVariable()))) {
            return SsmClient.builder()
                    .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                    .region(Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable())))
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        } else {
            return SsmClient.builder()
                    .credentialsProvider(ProfileCredentialsProvider.create())
                    .region(Region.EU_WEST_1)
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        }
    }
}
