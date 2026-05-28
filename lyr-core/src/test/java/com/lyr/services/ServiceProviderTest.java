package com.lyr.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lyr.TestUtil;
import com.lyr.exception.services.BadAwsServiceConfigException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.sts.StsClient;

class ServiceProviderTest {

    static MockedStatic<ServiceProvider> mockedServiceProvider =
            Mockito.mockStatic(ServiceProvider.class, Mockito.CALLS_REAL_METHODS);

    @BeforeEach
    void beforeEach() {
        mockedServiceProvider.reset();
        mockedServiceProvider.when(ServiceProvider::validateConfiguration).thenAnswer(_ -> null);
        ServiceProvider.configure("default");
    }

    @AfterAll
    static void afterAll() {
        mockedServiceProvider.closeOnDemand();
    }

    @Test
    void testThatServiceProviderBuildsOnlyOneCloudWatchClient() {
        try (final var firstClient = ServiceProvider.getOrBuildCloudWatchClient();
                final var secondClient = ServiceProvider.getOrBuildCloudWatchClient()) {

            assertThat(firstClient).isSameAs(secondClient);
        }
    }

    @Test
    void testThatServiceProviderBuildsOnlyOneDynamoDbClient() {
        try (final var firstClient = ServiceProvider.getOrBuildDynamoDbClient();
                final var secondClient = ServiceProvider.getOrBuildDynamoDbClient()) {

            assertThat(firstClient).isSameAs(secondClient);
        }
    }

    @Test
    void testThatServiceProviderBuildsOnlyOneGlueClient() {
        try (final var firstClient = ServiceProvider.getOrBuildGlueClient();
                final var secondClient = ServiceProvider.getOrBuildGlueClient()) {

            assertThat(firstClient).isSameAs(secondClient);
        }
    }

    @Test
    void testThatServiceProviderBuildsOnlyOneLambdaClient() {
        try (final var firstClient = ServiceProvider.getOrBuildLambdaClient();
                final var secondClient = ServiceProvider.getOrBuildLambdaClient()) {

            assertThat(firstClient).isSameAs(secondClient);
        }
    }

    @Test
    void testThatServiceProviderBuildsOnlyOneSsmClient() {
        try (final var firstClient = ServiceProvider.getOrBuildSsmClient();
                final var secondClient = ServiceProvider.getOrBuildSsmClient()) {

            assertThat(firstClient).isSameAs(secondClient);
        }
    }

    @Test
    void testThatServiceProviderBuildsOnlyOneStsClient() {
        try (final var firstClient = ServiceProvider.getOrBuildStsClient();
                final var secondClient = ServiceProvider.getOrBuildStsClient()) {

            assertThat(firstClient).isSameAs(secondClient);
        }
    }

    @Test
    void testThatCloudWatchClientIsBuildWithEnvironmentVariableIfPresent() {
        // Given
        mockedServiceProvider.when(ServiceProvider::getAwsAccessKeyIdFromEnv).thenReturn(TestUtil.DUMMY_STRING);
        mockedServiceProvider.when(ServiceProvider::getAwsRegionFromEnv).thenReturn(TestUtil.DUMMY_STRING);

        // When
        try (final var ignored = ServiceProvider.buildCloudWatchClient()) {
            // Then
            mockedServiceProvider.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
            mockedServiceProvider.verify(ServiceProvider::getAwsRegionFromEnv, times(1));
        }
    }

    @Test
    void testThatCloudWatchClientIsBuildWithProfile() {
        // Given
        mockedServiceProvider.when(ServiceProvider::getAwsAccessKeyIdFromEnv).thenReturn("");

        // When
        try (final var ignored = ServiceProvider.buildCloudWatchClient()) {
            // Then
            mockedServiceProvider.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
            mockedServiceProvider.verify(ServiceProvider::getAwsRegionFromEnv, times(0));
        }
    }

    @Test
    void testThatDynamoDbClientIsBuildWithEnvironmentVariableIfPresent() {
        // Given
        mockedServiceProvider.when(ServiceProvider::getAwsAccessKeyIdFromEnv).thenReturn(TestUtil.DUMMY_STRING);
        mockedServiceProvider.when(ServiceProvider::getAwsRegionFromEnv).thenReturn(TestUtil.DUMMY_STRING);

        // When
        try (final var ignored = ServiceProvider.buildDynamoDbClient()) {
            // Then
            mockedServiceProvider.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
            mockedServiceProvider.verify(ServiceProvider::getAwsRegionFromEnv, times(1));
        }
    }

    @Test
    void testThatDynamoDbClientIsBuildWithProfile() {
        // Given
        mockedServiceProvider.when(ServiceProvider::getAwsAccessKeyIdFromEnv).thenReturn("");

        // When
        try (final var ignored = ServiceProvider.buildDynamoDbClient()) {
            // Then
            mockedServiceProvider.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
            mockedServiceProvider.verify(ServiceProvider::getAwsRegionFromEnv, times(0));
        }
    }

    @Test
    void testThatGlueClientIsBuildWithEnvironmentVariableIfPresent() {
        // Given
        mockedServiceProvider.when(ServiceProvider::getAwsAccessKeyIdFromEnv).thenReturn(TestUtil.DUMMY_STRING);
        mockedServiceProvider.when(ServiceProvider::getAwsRegionFromEnv).thenReturn(TestUtil.DUMMY_STRING);

        // When
        try (final var ignored = ServiceProvider.buildGlueClient()) {
            // Then
            mockedServiceProvider.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
            mockedServiceProvider.verify(ServiceProvider::getAwsRegionFromEnv, times(1));
        }
    }

    @Test
    void testThatGlueClientIsBuildWithProfile() {
        // Given
        mockedServiceProvider.when(ServiceProvider::getAwsAccessKeyIdFromEnv).thenReturn("");

        // When
        try (final var ignored = ServiceProvider.buildGlueClient()) {
            // Then
            mockedServiceProvider.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
            mockedServiceProvider.verify(ServiceProvider::getAwsRegionFromEnv, times(0));
        }
    }

    @Test
    void testThatLambdaClientIsBuildWithEnvironmentVariableIfPresent() {
        // Given
        mockedServiceProvider.when(ServiceProvider::getAwsAccessKeyIdFromEnv).thenReturn(TestUtil.DUMMY_STRING);
        mockedServiceProvider.when(ServiceProvider::getAwsRegionFromEnv).thenReturn(TestUtil.DUMMY_STRING);

        // When
        try (final var ignored = ServiceProvider.buildLambdaClient()) {
            // Then
            mockedServiceProvider.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
            mockedServiceProvider.verify(ServiceProvider::getAwsRegionFromEnv, times(1));
        }
    }

    @Test
    void testThatLambdaClientIsBuildWithProfile() {
        // Given
        mockedServiceProvider.when(ServiceProvider::getAwsAccessKeyIdFromEnv).thenReturn("");

        // When
        try (final var ignored = ServiceProvider.buildLambdaClient()) {
            // Then
            mockedServiceProvider.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
            mockedServiceProvider.verify(ServiceProvider::getAwsRegionFromEnv, times(0));
        }
    }

    @Test
    void testThatSsmClientIsBuildWithEnvironmentVariableIfPresent() {
        // Given
        mockedServiceProvider.when(ServiceProvider::getAwsAccessKeyIdFromEnv).thenReturn(TestUtil.DUMMY_STRING);
        mockedServiceProvider.when(ServiceProvider::getAwsRegionFromEnv).thenReturn(TestUtil.DUMMY_STRING);

        // When
        try (final var ignored = ServiceProvider.buildSsmClient()) {
            // Then
            mockedServiceProvider.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
            mockedServiceProvider.verify(ServiceProvider::getAwsRegionFromEnv, times(1));
        }
    }

    @Test
    void testThatSsmClientIsBuildWithProfile() {
        // Given
        mockedServiceProvider.when(ServiceProvider::getAwsAccessKeyIdFromEnv).thenReturn("");

        // When
        try (final var ignored = ServiceProvider.buildSsmClient()) {
            // Then
            mockedServiceProvider.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
            mockedServiceProvider.verify(ServiceProvider::getAwsRegionFromEnv, times(0));
        }
    }

    @Test
    void testThatStsClientIsBuildWithEnvironmentVariableIfPresent() {
        // Given
        mockedServiceProvider.when(ServiceProvider::getAwsAccessKeyIdFromEnv).thenReturn(TestUtil.DUMMY_STRING);
        mockedServiceProvider.when(ServiceProvider::getAwsRegionFromEnv).thenReturn(TestUtil.DUMMY_STRING);

        // When
        try (final var ignored = ServiceProvider.buildStsClient()) {
            // Then
            mockedServiceProvider.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
            mockedServiceProvider.verify(ServiceProvider::getAwsRegionFromEnv, times(1));
        }
    }

    @Test
    void testThatStsClientIsBuildWithProfile() {
        // Given
        mockedServiceProvider.when(ServiceProvider::getAwsAccessKeyIdFromEnv).thenReturn("");

        // When
        try (final var ignored = ServiceProvider.buildStsClient()) {
            // Then
            mockedServiceProvider.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
            mockedServiceProvider.verify(ServiceProvider::getAwsRegionFromEnv, times(0));
        }
    }

    @Test
    void testThatServiceProviderValidatesConfigurationByGettingCallerIdentityViaSts() {
        // Given
        mockedServiceProvider.when(ServiceProvider::validateConfiguration).thenCallRealMethod();
        try (final StsClient mockedStsClient = mock(StsClient.class)) {
            mockedServiceProvider.when(ServiceProvider::getOrBuildStsClient).thenReturn(mockedStsClient);

            // When
            ServiceProvider.configure("dummy");

            // Then
            verify(mockedStsClient, times(1)).getCallerIdentity();
        }
    }

    @Test
    void testThatServiceProviderThrowsAnExceptionWhenAnInvalidProfileIsUsedToConfigure() {
        // Given
        mockedServiceProvider.when(ServiceProvider::validateConfiguration).thenCallRealMethod();

        // When & Then
        assertThatThrownBy(() -> ServiceProvider.configure(null))
                .isInstanceOf(BadAwsServiceConfigException.class)
                .hasMessageContaining("Failed to configure AWS service provider as profile is invalid.");
    }

    @Test
    void testThatServiceProviderThrowsAnExceptionWhenConfigurationIsInvalid() {
        // Given
        mockedServiceProvider.when(ServiceProvider::validateConfiguration).thenCallRealMethod();
        try (final StsClient mockedStsClient = mock(StsClient.class)) {
            when(mockedStsClient.getCallerIdentity()).thenThrow(SdkClientException.class);
            mockedServiceProvider.when(ServiceProvider::getOrBuildStsClient).thenReturn(mockedStsClient);

            // When & Then
            assertThatThrownBy(() -> ServiceProvider.configure("dummy"))
                    .isInstanceOf(BadAwsServiceConfigException.class)
                    .hasMessageContaining("Failed to create a service using configured credentials.");
        }
    }
}
