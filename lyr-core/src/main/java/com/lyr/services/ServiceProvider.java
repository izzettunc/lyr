package com.lyr.services;

import static com.lyr.exception.services.BadAwsServiceConfigException.BAD_AWS_SERVICE_CONFIG_EXCEPTION_MESSAGE;

import com.google.common.annotations.VisibleForTesting;
import com.lyr.exception.NotYetConfiguredException;
import com.lyr.exception.services.BadAwsServiceConfigException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.AwsProfileRegionProvider;
import software.amazon.awssdk.services.backup.BackupClient;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.glue.GlueClient;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.resourcegroupstaggingapi.ResourceGroupsTaggingApiClient;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.sts.StsClient;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceProvider {
    private static EnvironmentVariableCredentialsProvider environmentVariableCredentialsProvider;
    private static ProfileCredentialsProvider profileCredentialsProvider;
    private static AwsProfileRegionProvider awsProfileRegionProvider;

    private static String profile;

    public static void configure(final String awsProfile) {
        profile = awsProfile;
        environmentVariableCredentialsProvider = EnvironmentVariableCredentialsProvider.create();
        profileCredentialsProvider = ProfileCredentialsProvider.create(profile);
        awsProfileRegionProvider = new AwsProfileRegionProvider(ProfileFile::defaultProfileFile, profile);

        validateConfiguration();

        log.atInfo()
                .addArgument(profile)
                .addArgument(() -> awsProfileRegionProvider.getRegion())
                .log("Successfully configured AWS service provider. Profile: {}, Region: {}");
    }

    @VisibleForTesting
    static void validateConfiguration() {
        if (StringUtils.isEmpty(profile)) {
            throw new BadAwsServiceConfigException(
                    "Failed to configure AWS service provider as profile is invalid. Profile: " + profile);
        }

        try {
            getOrBuildStsClient().getCallerIdentity();
        } catch (final SdkClientException sdkClientException) {
            throw new BadAwsServiceConfigException(
                    String.format(BAD_AWS_SERVICE_CONFIG_EXCEPTION_MESSAGE, sdkClientException.getMessage()),
                    sdkClientException);
        }
    }

    private static void checkIfServiceProviderIsConfigured() {
        if (profile == null) {
            throw new NotYetConfiguredException("Service provider can not be accessed as it is not yet configured.");
        }
    }

    @VisibleForTesting
    static String getAwsAccessKeyIdFromEnv() {
        return System.getenv(SdkSystemSetting.AWS_ACCESS_KEY_ID.environmentVariable());
    }

    @VisibleForTesting
    static String getAwsRegionFromEnv() {
        return System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable());
    }

    public static CloudWatchClient getOrBuildCloudWatchClient() {
        checkIfServiceProviderIsConfigured();

        return CloudWatchClientHolder.INSTANCE;
    }

    public static CloudWatchLogsClient getOrBuildCloudWatchLogsClient() {
        checkIfServiceProviderIsConfigured();

        return CloudWatchLogsClientHolder.INSTANCE;
    }

    public static DynamoDbClient getOrBuildDynamoDbClient() {
        checkIfServiceProviderIsConfigured();

        return DynamoDbClientHolder.INSTANCE;
    }

    public static GlueClient getOrBuildGlueClient() {
        checkIfServiceProviderIsConfigured();

        return GlueClientHolder.INSTANCE;
    }

    public static LambdaClient getOrBuildLambdaClient() {
        checkIfServiceProviderIsConfigured();

        return LambdaClientHolder.INSTANCE;
    }

    public static SsmClient getOrBuildSsmClient() {
        checkIfServiceProviderIsConfigured();

        return SsmClientHolder.INSTANCE;
    }

    public static StsClient getOrBuildStsClient() {
        checkIfServiceProviderIsConfigured();

        return StsClientHolder.INSTANCE;
    }

    public static BackupClient getOrBuildBackupClient() {
        checkIfServiceProviderIsConfigured();

        return BackupClientHolder.INSTANCE;
    }

    public static ResourceGroupsTaggingApiClient getOrBuildResourceGroupsTaggingApiClient() {
        checkIfServiceProviderIsConfigured();

        return ResourceGroupsTaggingApiClientHolder.INSTANCE;
    }

    @VisibleForTesting
    static CloudWatchClient buildCloudWatchClient() {
        if (!StringUtils.isBlank(getAwsAccessKeyIdFromEnv())) {
            return CloudWatchClient.builder()
                    .credentialsProvider(environmentVariableCredentialsProvider)
                    .region(Region.of(getAwsRegionFromEnv()))
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        } else {
            return CloudWatchClient.builder()
                    .credentialsProvider(profileCredentialsProvider)
                    .region(awsProfileRegionProvider.getRegion())
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        }
    }

    @VisibleForTesting
    static CloudWatchLogsClient buildCloudWatchLogsClient() {
        if (!StringUtils.isBlank(getAwsAccessKeyIdFromEnv())) {
            return CloudWatchLogsClient.builder()
                    .credentialsProvider(environmentVariableCredentialsProvider)
                    .region(Region.of(getAwsRegionFromEnv()))
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        } else {
            return CloudWatchLogsClient.builder()
                    .credentialsProvider(profileCredentialsProvider)
                    .region(awsProfileRegionProvider.getRegion())
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        }
    }

    @VisibleForTesting
    static DynamoDbClient buildDynamoDbClient() {
        if (!StringUtils.isBlank(getAwsAccessKeyIdFromEnv())) {
            return DynamoDbClient.builder()
                    .credentialsProvider(environmentVariableCredentialsProvider)
                    .region(Region.of(getAwsRegionFromEnv()))
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        } else {
            return DynamoDbClient.builder()
                    .credentialsProvider(profileCredentialsProvider)
                    .region(awsProfileRegionProvider.getRegion())
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        }
    }

    @VisibleForTesting
    static GlueClient buildGlueClient() {
        if (!StringUtils.isBlank(getAwsAccessKeyIdFromEnv())) {
            return GlueClient.builder()
                    .credentialsProvider(environmentVariableCredentialsProvider)
                    .region(Region.of(getAwsRegionFromEnv()))
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        } else {
            return GlueClient.builder()
                    .credentialsProvider(profileCredentialsProvider)
                    .region(awsProfileRegionProvider.getRegion())
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        }
    }

    @VisibleForTesting
    static LambdaClient buildLambdaClient() {
        if (!StringUtils.isBlank(getAwsAccessKeyIdFromEnv())) {
            return LambdaClient.builder()
                    .credentialsProvider(environmentVariableCredentialsProvider)
                    .region(Region.of(getAwsRegionFromEnv()))
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        } else {
            return LambdaClient.builder()
                    .credentialsProvider(profileCredentialsProvider)
                    .region(awsProfileRegionProvider.getRegion())
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        }
    }

    @VisibleForTesting
    static SsmClient buildSsmClient() {
        if (!StringUtils.isBlank(getAwsAccessKeyIdFromEnv())) {
            return SsmClient.builder()
                    .credentialsProvider(environmentVariableCredentialsProvider)
                    .region(Region.of(getAwsRegionFromEnv()))
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        } else {
            return SsmClient.builder()
                    .credentialsProvider(profileCredentialsProvider)
                    .region(awsProfileRegionProvider.getRegion())
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        }
    }

    @VisibleForTesting
    static StsClient buildStsClient() {
        if (!StringUtils.isBlank(getAwsAccessKeyIdFromEnv())) {
            return StsClient.builder()
                    .credentialsProvider(environmentVariableCredentialsProvider)
                    .region(Region.of(getAwsRegionFromEnv()))
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        } else {
            return StsClient.builder()
                    .credentialsProvider(profileCredentialsProvider)
                    .region(awsProfileRegionProvider.getRegion())
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        }
    }

    @VisibleForTesting
    static BackupClient buildBackupClient() {
        if (!StringUtils.isBlank(getAwsAccessKeyIdFromEnv())) {
            return BackupClient.builder()
                    .credentialsProvider(environmentVariableCredentialsProvider)
                    .region(Region.of(getAwsRegionFromEnv()))
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        } else {
            return BackupClient.builder()
                    .credentialsProvider(profileCredentialsProvider)
                    .region(awsProfileRegionProvider.getRegion())
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        }
    }

    @VisibleForTesting
    static ResourceGroupsTaggingApiClient buildResourceGroupsTaggingApiClient() {
        if (!StringUtils.isBlank(getAwsAccessKeyIdFromEnv())) {
            return ResourceGroupsTaggingApiClient.builder()
                    .credentialsProvider(environmentVariableCredentialsProvider)
                    .region(Region.of(getAwsRegionFromEnv()))
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        } else {
            return ResourceGroupsTaggingApiClient.builder()
                    .credentialsProvider(profileCredentialsProvider)
                    .region(awsProfileRegionProvider.getRegion())
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build();
        }
    }

    private static final class DynamoDbClientHolder {
        static final DynamoDbClient INSTANCE = buildDynamoDbClient();
    }

    private static final class GlueClientHolder {
        static final GlueClient INSTANCE = buildGlueClient();
    }

    private static final class SsmClientHolder {
        static final SsmClient INSTANCE = buildSsmClient();
    }

    private static final class StsClientHolder {
        static final StsClient INSTANCE = buildStsClient();
    }

    private static final class BackupClientHolder {
        static final BackupClient INSTANCE = buildBackupClient();
    }

    private static final class ResourceGroupsTaggingApiClientHolder {
        static final ResourceGroupsTaggingApiClient INSTANCE = buildResourceGroupsTaggingApiClient();
    }

    private static final class LambdaClientHolder {
        static final LambdaClient INSTANCE = buildLambdaClient();
    }

    private static final class CloudWatchClientHolder {
        static final CloudWatchClient INSTANCE = buildCloudWatchClient();
    }

    private static final class CloudWatchLogsClientHolder {
        static final CloudWatchLogsClient INSTANCE = buildCloudWatchLogsClient();
    }
}
