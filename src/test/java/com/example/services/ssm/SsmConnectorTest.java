package com.example.services.ssm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.example.TestUtil;
import com.example.services.ServiceProvider;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.Parameter;
import software.amazon.awssdk.services.ssm.model.ParameterNotFoundException;

class SsmConnectorTest {
    final SsmClient mockedSsmClient = Mockito.mock(SsmClient.class);
    SsmConnector testObject;

    @BeforeEach
    void beforeEach() {
        testObject = SsmConnector.create(mockedSsmClient);
    }

    @AfterEach
    void afterEach() {
        reset(mockedSsmClient);
    }

    @Test
    void testThatSsmConnectorGetsSsmClientFromServiceProvider() {
        // Given
        try (final var mockedServiceProvider = mockStatic(ServiceProvider.class)) {
            // When
            SsmConnector.create();
            // Then
            mockedServiceProvider.verify(ServiceProvider::getOrBuildSsmClient, times(1));
        }
    }

    @Test
    void testThatGetParameterReturnsOptionalOfParameterWhenFound() {
        // Given
        final var expectedParameter = Parameter.builder()
                .name(TestUtil.DUMMY_STRING)
                .value(TestUtil.DUMMY_STRING)
                .build();
        final var getParameterResponse =
                GetParameterResponse.builder().parameter(expectedParameter).build();

        // When
        when(mockedSsmClient.getParameter(any(GetParameterRequest.class))).thenReturn(getParameterResponse);
        final var actualResult = testObject.getParameter(TestUtil.DUMMY_STRING);

        // Then
        assertThat(actualResult).isEqualTo(Optional.of(expectedParameter));
    }

    @Test
    void testThatGetParameterReturnsOptionalOfParameterWhenNotFound() {
        // Given nothing
        // When
        when(mockedSsmClient.getParameter(any(GetParameterRequest.class))).thenThrow(ParameterNotFoundException.class);
        final var actualResult = testObject.getParameter(TestUtil.DUMMY_STRING);

        // Then
        assertThat(actualResult).isEqualTo(Optional.empty());
    }
}
