package com.lyr.cli.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

class VersionProviderTest {

    private static final String EXPECTED_VERSION_AS_UNKNOWN = "unknown";
    Package mockedPackage = mock(Package.class);
    VersionProvider testObject;

    @BeforeEach
    void beforeEach() {
        reset(mockedPackage);
        testObject = new VersionProvider();
    }

    @Test
    void testThatVersionProviderReturnsVersionIfVersionIsInManifestAsImplementationVersion() {
        // Given
        final var dummyVersion = "dummy-1.2.3";
        try (final var mockedVersionProvider = mockStatic(VersionProvider.class, Mockito.CALLS_REAL_METHODS)) {
            // When
            mockedVersionProvider.when(VersionProvider::getPackage).thenReturn(mockedPackage);
            when(mockedPackage.getImplementationVersion()).thenReturn(dummyVersion);

            final var result = VersionProvider.getVersionFromManifest();

            // Then
            assertThat(result).isEqualTo(dummyVersion);
        }
    }

    @Test
    void testThatVersionProviderReturnsUnknownAsVersionIfPackageIsNull() {
        // Given
        try (final var mockedVersionProvider = mockStatic(VersionProvider.class, Mockito.CALLS_REAL_METHODS)) {
            // When
            mockedVersionProvider.when(VersionProvider::getPackage).thenReturn(null);

            final var result = VersionProvider.getVersionFromManifest();

            // Then
            assertThat(result).isEqualTo(EXPECTED_VERSION_AS_UNKNOWN);
        }
    }

    @ParameterizedTest
    @MethodSource("invalidImplementationVersionValues")
    void testThatVersionProviderReturnsUnknownAsVersionIfVersionIsBlank(String invalidImplementationVersion) {
        // Given invalidImplementationVersion
        try (final var mockedVersionProvider = mockStatic(VersionProvider.class, Mockito.CALLS_REAL_METHODS)) {
            // When
            mockedVersionProvider.when(VersionProvider::getPackage).thenReturn(mockedPackage);
            when(mockedPackage.getImplementationVersion()).thenReturn(invalidImplementationVersion);

            final var result = VersionProvider.getVersionFromManifest();

            // Then
            assertThat(result).isEqualTo(EXPECTED_VERSION_AS_UNKNOWN);
        }
    }

    @Test
    void testThatVersionProviderUsesGetVersionFromManifestForProvidingVersion() {
        // Given
        final var dummyVersion = "dummy-1.2.3";
        final var dummySchemaVersion = "dummy-9.8.7";
        final var expectedVersion = new String[] {"Version " + dummyVersion, "Schema version " + dummySchemaVersion};
        try (final var mockedVersionProvider = mockStatic(VersionProvider.class)) {
            mockedVersionProvider.when(VersionProvider::getVersionFromManifest).thenReturn(dummyVersion);
            mockedVersionProvider.when(VersionProvider::getSchemaVersion).thenReturn(dummySchemaVersion);

            final var result = testObject.getVersion();
            assertThat(result).isEqualTo(expectedVersion);
            mockedVersionProvider.verify(VersionProvider::getVersionFromManifest, times(1));
            mockedVersionProvider.verify(VersionProvider::getSchemaVersion, times(1));
        }
    }

    public static Stream<String> invalidImplementationVersionValues() {
        return Stream.of("", "   ", null);
    }
}
