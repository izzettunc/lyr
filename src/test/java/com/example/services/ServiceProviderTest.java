package com.example.services;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;

class ServiceProviderTest {

    @Test
    void testThatServiceProviderBuildsOnlyOneCloudWatchClient() {
        var firstClient = ServiceProvider.getOrBuildCloudWatchClient();
        var secondClient = ServiceProvider.getOrBuildCloudWatchClient();

        assertThat(firstClient).isSameAs(secondClient);
    }

    @Test
    void testThatServiceProviderBuildsOnlyOneDynamoDbClient() {
        var firstClient = ServiceProvider.getOrBuildDynamoDbClient();
        var secondClient = ServiceProvider.getOrBuildDynamoDbClient();

        assertThat(firstClient).isSameAs(secondClient);
    }

    @Test
    void testThatServiceProviderBuildsOnlyOneGlueClient() {
        var firstClient = ServiceProvider.getOrBuildGlueClient();
        var secondClient = ServiceProvider.getOrBuildGlueClient();

        assertThat(firstClient).isSameAs(secondClient);
    }

    @Test
    void testThatServiceProviderBuildsOnlyOneLambdaClient() {
        var firstClient = ServiceProvider.getOrBuildLambdaClient();
        var secondClient = ServiceProvider.getOrBuildLambdaClient();

        assertThat(firstClient).isSameAs(secondClient);
    }

    @Test
    void testThatServiceProviderBuildsOnlyOneSsmClient() {
        var firstClient = ServiceProvider.getOrBuildSsmClient();
        var secondClient = ServiceProvider.getOrBuildSsmClient();

        assertThat(firstClient).isSameAs(secondClient);
    }

    @Test
    void testThatCloudWatchClientIsBuildWithEnvironmentVariableIfPresent() {
        // Given
        try (var serviceUtilMockedStatic = Mockito.mockStatic(ServiceProvider.class, Mockito.CALLS_REAL_METHODS)) {
            serviceUtilMockedStatic.when(ServiceProvider::getAwsAccessKeyIdFromEnv).thenReturn("dummy");
            serviceUtilMockedStatic.when(ServiceProvider::getAwsRegionFromEnv).thenReturn("dummy");

            // When
            try(var ignored = ServiceProvider.buildCloudWatchClient()) {
                // Then
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsRegionFromEnv, times(1));
            }
        }
    }

    @Test
    void testThatCloudWatchClientIsBuildWithProfile() {
        // Given
        try (var serviceUtilMockedStatic = Mockito.mockStatic(ServiceProvider.class, Mockito.CALLS_REAL_METHODS)) {
            serviceUtilMockedStatic.when(ServiceProvider::getAwsAccessKeyIdFromEnv).thenReturn("");

            // When
            try(var ignored = ServiceProvider.buildCloudWatchClient()) {
                // Then
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsRegionFromEnv, times(0));
            }
        }
    }

    @Test
    void testThatDynamoDbClientIsBuildWithEnvironmentVariableIfPresent() {
        // Given
        try (var serviceUtilMockedStatic = Mockito.mockStatic(ServiceProvider.class, Mockito.CALLS_REAL_METHODS)) {
            serviceUtilMockedStatic.when(ServiceProvider::getAwsAccessKeyIdFromEnv).thenReturn("dummy");
            serviceUtilMockedStatic.when(ServiceProvider::getAwsRegionFromEnv).thenReturn("dummy");

            // When
            try(var ignored = ServiceProvider.buildDynamoDbClient()) {
                // Then
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsRegionFromEnv, times(1));
            }
        }
    }

    @Test
    void testThatDynamoDbClientIsBuildWithProfile() {
        // Given
        try (var serviceUtilMockedStatic = Mockito.mockStatic(ServiceProvider.class, Mockito.CALLS_REAL_METHODS)) {
            serviceUtilMockedStatic.when(ServiceProvider::getAwsAccessKeyIdFromEnv).thenReturn("");

            // When
            try(var ignored = ServiceProvider.buildDynamoDbClient()) {
                // Then
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsRegionFromEnv, times(0));
            }
        }
    }

    @Test
    void testThatGlueClientIsBuildWithEnvironmentVariableIfPresent() {
        // Given
        try (var serviceUtilMockedStatic = Mockito.mockStatic(ServiceProvider.class, Mockito.CALLS_REAL_METHODS)) {
            serviceUtilMockedStatic.when(ServiceProvider::getAwsAccessKeyIdFromEnv).thenReturn("dummy");
            serviceUtilMockedStatic.when(ServiceProvider::getAwsRegionFromEnv).thenReturn("dummy");

            // When
            try(var ignored = ServiceProvider.buildGlueClient()) {
                // Then
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsRegionFromEnv, times(1));
            }
        }
    }

    @Test
    void testThatGlueClientIsBuildWithProfile() {
        // Given
        try (var serviceUtilMockedStatic = Mockito.mockStatic(ServiceProvider.class, Mockito.CALLS_REAL_METHODS)) {
            serviceUtilMockedStatic.when(ServiceProvider::getAwsAccessKeyIdFromEnv).thenReturn("");

            // When
            try(var ignored = ServiceProvider.buildGlueClient()) {
                // Then
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsRegionFromEnv, times(0));
            }
        }
    }
    @Test
    void testThatLambdaClientIsBuildWithEnvironmentVariableIfPresent() {
        // Given
        try (var serviceUtilMockedStatic = Mockito.mockStatic(ServiceProvider.class, Mockito.CALLS_REAL_METHODS)) {
            serviceUtilMockedStatic.when(ServiceProvider::getAwsAccessKeyIdFromEnv).thenReturn("dummy");
            serviceUtilMockedStatic.when(ServiceProvider::getAwsRegionFromEnv).thenReturn("dummy");

            // When
            try(var ignored = ServiceProvider.buildLambdaClient()) {
                // Then
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsRegionFromEnv, times(1));
            }
        }
    }

    @Test
    void testThatLambdaClientIsBuildWithProfile() {
        // Given
        try (var serviceUtilMockedStatic = Mockito.mockStatic(ServiceProvider.class, Mockito.CALLS_REAL_METHODS)) {
            serviceUtilMockedStatic.when(ServiceProvider::getAwsAccessKeyIdFromEnv).thenReturn("");

            // When
            try(var ignored = ServiceProvider.buildLambdaClient()) {
                // Then
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsRegionFromEnv, times(0));
            }
        }
    }

    @Test
    void testThatSsmClientIsBuildWithEnvironmentVariableIfPresent() {
        // Given
        try (var serviceUtilMockedStatic = Mockito.mockStatic(ServiceProvider.class, Mockito.CALLS_REAL_METHODS)) {
            serviceUtilMockedStatic.when(ServiceProvider::getAwsAccessKeyIdFromEnv).thenReturn("dummy");
            serviceUtilMockedStatic.when(ServiceProvider::getAwsRegionFromEnv).thenReturn("dummy");

            // When
            try(var ignored = ServiceProvider.buildSsmClient()) {
                // Then
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsRegionFromEnv, times(1));
            }
        }
    }

    @Test
    void testThatSsmClientIsBuildWithProfile() {
        // Given
        try (var serviceUtilMockedStatic = Mockito.mockStatic(ServiceProvider.class, Mockito.CALLS_REAL_METHODS)) {
            serviceUtilMockedStatic.when(ServiceProvider::getAwsAccessKeyIdFromEnv).thenReturn("");

            // When
            try(var ignored = ServiceProvider.buildSsmClient()) {
                // Then
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsAccessKeyIdFromEnv, times(1));
                serviceUtilMockedStatic.verify(ServiceProvider::getAwsRegionFromEnv, times(0));
            }
        }
    }

}