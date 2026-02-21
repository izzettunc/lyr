package com.example.services;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.StringUtils;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.glue.GlueClient;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.ssm.SsmClient;

public class ServiceProvider {
    private static volatile SsmClient ssmClient;
    private static volatile LambdaClient lambdaClient;
    private static volatile GlueClient glueClient;
    private static volatile DynamoDbClient dynamoDbClient;
    private static volatile CloudWatchClient cloudWatchClient;

    public static CloudWatchClient getOrBuildCloudWatchClient() {
        if (cloudWatchClient == null) {
            synchronized (ServiceProvider.class) {
                if (cloudWatchClient == null) {
                    cloudWatchClient = buildCloudWatchClient();
                }
            }
        }
        return cloudWatchClient;
    }

    public static DynamoDbClient getOrBuildDynamoDbClient() {
        if (dynamoDbClient == null) {
            synchronized (ServiceProvider.class) {
                if (dynamoDbClient == null) {
                    dynamoDbClient = buildDynamoDbClient();
                }
            }
        }
        return dynamoDbClient;
    }

    public static GlueClient getOrBuildGlueClient() {
        if (glueClient == null) {
            synchronized (ServiceProvider.class) {
                if (glueClient == null) {
                    glueClient = buildGlueClient();
                }
            }
        }
        return glueClient;
    }

    public static LambdaClient getOrBuildLambdaClient() {
        if (lambdaClient == null) {
            synchronized (ServiceProvider.class) {
                if (lambdaClient == null) {
                    lambdaClient = buildLambdaClient();
                }
            }
        }
        return lambdaClient;
    }

    public static SsmClient getOrBuildSsmClient() {
        if (ssmClient == null) {
            synchronized (ServiceProvider.class) {
                if (ssmClient == null) {
                    ssmClient = buildSsmClient();
                }
            }
        }
        return ssmClient;
    }

    @VisibleForTesting
    static CloudWatchClient buildCloudWatchClient() {
        if (!StringUtils.isBlank(getAwsAccessKeyIdFromEnv())) {
            return CloudWatchClient.builder()
                    .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                    .region(Region.of(getAwsRegionFromEnv()))
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        } else {
            return CloudWatchClient.builder()
                    .credentialsProvider(ProfileCredentialsProvider.create())
                    .region(Region.EU_WEST_1)
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        }
    }

    @VisibleForTesting
    static DynamoDbClient buildDynamoDbClient() {
        if (!StringUtils.isBlank(getAwsAccessKeyIdFromEnv())) {
            return DynamoDbClient.builder()
                    .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                    .region(Region.of(getAwsRegionFromEnv()))
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        } else {
            return DynamoDbClient.builder()
                    .credentialsProvider(ProfileCredentialsProvider.create())
                    .region(Region.EU_WEST_1)
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        }
    }

    @VisibleForTesting
    static GlueClient buildGlueClient() {
        if (!StringUtils.isBlank(getAwsAccessKeyIdFromEnv())) {
            return GlueClient.builder()
                    .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                    .region(Region.of(getAwsRegionFromEnv()))
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

    @VisibleForTesting
    static LambdaClient buildLambdaClient() {
        if (!StringUtils.isBlank(getAwsAccessKeyIdFromEnv())) {
            return LambdaClient.builder()
                    .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                    .region(Region.of(getAwsRegionFromEnv()))
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        } else {
            return LambdaClient.builder()
                    .credentialsProvider(ProfileCredentialsProvider.create())
                    .region(Region.EU_WEST_1)
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        }
    }

    @VisibleForTesting
    static SsmClient buildSsmClient() {
        if (!StringUtils.isBlank(getAwsAccessKeyIdFromEnv())) {
            return SsmClient.builder()
                    .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                    .region(Region.of(getAwsRegionFromEnv()))
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

    static String getAwsAccessKeyIdFromEnv() {
        return System.getenv(SdkSystemSetting.AWS_ACCESS_KEY_ID.environmentVariable());
    }

    static String getAwsRegionFromEnv() {
        return System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable());
    }
}
