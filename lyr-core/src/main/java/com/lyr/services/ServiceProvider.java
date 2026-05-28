package com.lyr.services;

import static com.lyr.exception.services.BadAwsServiceConfigException.BAD_AWS_SERVICE_CONFIG_EXCEPTION_MESSAGE;

import com.google.common.annotations.VisibleForTesting;
import com.lyr.exception.NotYetConfiguredException;
import com.lyr.exception.services.BadAwsServiceConfigException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.glue.GlueClient;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.sts.StsClient;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceProvider {
    private static volatile SsmClient ssmClient;
    private static volatile StsClient stsClient;
    private static volatile LambdaClient lambdaClient;
    private static volatile GlueClient glueClient;
    private static volatile DynamoDbClient dynamoDbClient;
    private static volatile CloudWatchClient cloudWatchClient;

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
    }

    @VisibleForTesting
    static void validateConfiguration() {
        if (StringUtils.isEmpty(profile)) {
            throw new BadAwsServiceConfigException(
                    "Failed to configure AWS service provider as profile is invalid. Profile: " + profile);
        }

        try {
            getOrBuildStsClient().getCallerIdentity();

            log.atInfo()
                    .addArgument(profile)
                    .addArgument(() -> awsProfileRegionProvider.getRegion())
                    .log("Successfully configured AWS service provider. Profile: {}, Region: {}");
        } catch (final SdkClientException sdkClientException) {
            throw new BadAwsServiceConfigException(
                    String.format(BAD_AWS_SERVICE_CONFIG_EXCEPTION_MESSAGE, sdkClientException.getMessage()),
                    sdkClientException);
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

    @SuppressFBWarnings(value = "MS_EXPOSE_REP", justification = "Intentional implementation suggested by AWS")
    public static CloudWatchClient getOrBuildCloudWatchClient() {
        checkIfServiceProviderIsConfigured();

        if (cloudWatchClient == null) {
            synchronized (ServiceProvider.class) {
                if (cloudWatchClient == null) {
                    cloudWatchClient = buildCloudWatchClient();
                }
            }
        }
        return cloudWatchClient;
    }

    @SuppressFBWarnings(value = "MS_EXPOSE_REP", justification = "Intentional implementation suggested by AWS")
    public static DynamoDbClient getOrBuildDynamoDbClient() {
        checkIfServiceProviderIsConfigured();

        if (dynamoDbClient == null) {
            synchronized (ServiceProvider.class) {
                if (dynamoDbClient == null) {
                    dynamoDbClient = buildDynamoDbClient();
                }
            }
        }
        return dynamoDbClient;
    }

    @SuppressFBWarnings(value = "MS_EXPOSE_REP", justification = "Intentional implementation suggested by AWS")
    public static GlueClient getOrBuildGlueClient() {
        checkIfServiceProviderIsConfigured();

        if (glueClient == null) {
            synchronized (ServiceProvider.class) {
                if (glueClient == null) {
                    glueClient = buildGlueClient();
                }
            }
        }
        return glueClient;
    }

    @SuppressFBWarnings(value = "MS_EXPOSE_REP", justification = "Intentional implementation suggested by AWS")
    public static LambdaClient getOrBuildLambdaClient() {
        checkIfServiceProviderIsConfigured();

        if (lambdaClient == null) {
            synchronized (ServiceProvider.class) {
                if (lambdaClient == null) {
                    lambdaClient = buildLambdaClient();
                }
            }
        }
        return lambdaClient;
    }

    @SuppressFBWarnings(value = "MS_EXPOSE_REP", justification = "Intentional implementation suggested by AWS")
    public static SsmClient getOrBuildSsmClient() {
        checkIfServiceProviderIsConfigured();

        if (ssmClient == null) {
            synchronized (ServiceProvider.class) {
                if (ssmClient == null) {
                    ssmClient = buildSsmClient();
                }
            }
        }
        return ssmClient;
    }

    public static StsClient getOrBuildStsClient() {
        checkIfServiceProviderIsConfigured();

        if (stsClient == null) {
            synchronized (ServiceProvider.class) {
                if (stsClient == null) {
                    stsClient = buildStsClient();
                }
            }
        }
        return stsClient;
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

    private static void checkIfServiceProviderIsConfigured() {
        if (profile == null) {
            throw new NotYetConfiguredException("Service provider can not be accessed as it is not yet configured.");
        }
    }
}
