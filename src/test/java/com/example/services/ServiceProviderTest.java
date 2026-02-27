package com.example.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;

import com.example.TestUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ServiceProviderTest {

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
    void testThatCloudWatchClientIsBuildWithEnvironmentVariableIfPresent() {
        // Given
        try (final var serviceUtilMockedStatic =
                Mockito.mockStatic(ServiceProvider.class, Mockito.CALLS_REAL_METHODS)) {
            serviceUtilMockedStatic
                    .when(ServiceProvider::getAwsAccessKeyIdFromEnv)
                    .thenReturn(TestUtil.DUMMY_STRING);
            serviceUtilMockedStatic.when(ServiceProvider::getAwsRegionFromEnv).thenReturn(TestUtil.DUMMY_STRING);

            // When
            try (final var ignored = ServiceProvider.buildCloudWatchClient()) {
                // Then
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsRegionFromEnv, times(1));
            }
        }
    }

    @Test
    void testThatCloudWatchClientIsBuildWithProfile() {
        // Given
        try (final var serviceUtilMockedStatic =
                Mockito.mockStatic(ServiceProvider.class, Mockito.CALLS_REAL_METHODS)) {
            serviceUtilMockedStatic
                    .when(ServiceProvider::getAwsAccessKeyIdFromEnv)
                    .thenReturn("");

            // When
            try (final var ignored = ServiceProvider.buildCloudWatchClient()) {
                // Then
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsRegionFromEnv, times(0));
            }
        }
    }

    @Test
    void testThatDynamoDbClientIsBuildWithEnvironmentVariableIfPresent() {
        // Given
        try (final var serviceUtilMockedStatic =
                Mockito.mockStatic(ServiceProvider.class, Mockito.CALLS_REAL_METHODS)) {
            serviceUtilMockedStatic
                    .when(ServiceProvider::getAwsAccessKeyIdFromEnv)
                    .thenReturn(TestUtil.DUMMY_STRING);
            serviceUtilMockedStatic.when(ServiceProvider::getAwsRegionFromEnv).thenReturn(TestUtil.DUMMY_STRING);

            // When
            try (final var ignored = ServiceProvider.buildDynamoDbClient()) {
                // Then
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsRegionFromEnv, times(1));
            }
        }
    }

    @Test
    void testThatDynamoDbClientIsBuildWithProfile() {
        // Given
        try (final var serviceUtilMockedStatic =
                Mockito.mockStatic(ServiceProvider.class, Mockito.CALLS_REAL_METHODS)) {
            serviceUtilMockedStatic
                    .when(ServiceProvider::getAwsAccessKeyIdFromEnv)
                    .thenReturn("");

            // When
            try (final var ignored = ServiceProvider.buildDynamoDbClient()) {
                // Then
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsRegionFromEnv, times(0));
            }
        }
    }

    @Test
    void testThatGlueClientIsBuildWithEnvironmentVariableIfPresent() {
        // Given
        try (final var serviceUtilMockedStatic =
                Mockito.mockStatic(ServiceProvider.class, Mockito.CALLS_REAL_METHODS)) {
            serviceUtilMockedStatic
                    .when(ServiceProvider::getAwsAccessKeyIdFromEnv)
                    .thenReturn(TestUtil.DUMMY_STRING);
            serviceUtilMockedStatic.when(ServiceProvider::getAwsRegionFromEnv).thenReturn(TestUtil.DUMMY_STRING);

            // When
            try (final var ignored = ServiceProvider.buildGlueClient()) {
                // Then
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsRegionFromEnv, times(1));
            }
        }
    }

    @Test
    void testThatGlueClientIsBuildWithProfile() {
        // Given
        try (final var serviceUtilMockedStatic =
                Mockito.mockStatic(ServiceProvider.class, Mockito.CALLS_REAL_METHODS)) {
            serviceUtilMockedStatic
                    .when(ServiceProvider::getAwsAccessKeyIdFromEnv)
                    .thenReturn("");

            // When
            try (final var ignored = ServiceProvider.buildGlueClient()) {
                // Then
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsRegionFromEnv, times(0));
            }
        }
    }

    @Test
    void testThatLambdaClientIsBuildWithEnvironmentVariableIfPresent() {
        // Given
        try (final var serviceUtilMockedStatic =
                Mockito.mockStatic(ServiceProvider.class, Mockito.CALLS_REAL_METHODS)) {
            serviceUtilMockedStatic
                    .when(ServiceProvider::getAwsAccessKeyIdFromEnv)
                    .thenReturn(TestUtil.DUMMY_STRING);
            serviceUtilMockedStatic.when(ServiceProvider::getAwsRegionFromEnv).thenReturn(TestUtil.DUMMY_STRING);

            // When
            try (final var ignored = ServiceProvider.buildLambdaClient()) {
                // Then
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsRegionFromEnv, times(1));
            }
        }
    }

    @Test
    void testThatLambdaClientIsBuildWithProfile() {
        // Given
        try (final var serviceUtilMockedStatic =
                Mockito.mockStatic(ServiceProvider.class, Mockito.CALLS_REAL_METHODS)) {
            serviceUtilMockedStatic
                    .when(ServiceProvider::getAwsAccessKeyIdFromEnv)
                    .thenReturn("");

            // When
            try (final var ignored = ServiceProvider.buildLambdaClient()) {
                // Then
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsRegionFromEnv, times(0));
            }
        }
    }

    @Test
    void testThatSsmClientIsBuildWithEnvironmentVariableIfPresent() {
        // Given
        try (final var serviceUtilMockedStatic =
                Mockito.mockStatic(ServiceProvider.class, Mockito.CALLS_REAL_METHODS)) {
            serviceUtilMockedStatic
                    .when(ServiceProvider::getAwsAccessKeyIdFromEnv)
                    .thenReturn(TestUtil.DUMMY_STRING);
            serviceUtilMockedStatic.when(ServiceProvider::getAwsRegionFromEnv).thenReturn(TestUtil.DUMMY_STRING);

            // When
            try (final var ignored = ServiceProvider.buildSsmClient()) {
                // Then
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsRegionFromEnv, times(1));
            }
        }
    }

    @Test
    void testThatSsmClientIsBuildWithProfile() {
        // Given
        try (final var serviceUtilMockedStatic =
                Mockito.mockStatic(ServiceProvider.class, Mockito.CALLS_REAL_METHODS)) {
            serviceUtilMockedStatic
                    .when(ServiceProvider::getAwsAccessKeyIdFromEnv)
                    .thenReturn("");

            // When
            try (final var ignored = ServiceProvider.buildSsmClient()) {
                // Then
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsRegionFromEnv, times(0));
            }
        }
    }
}
