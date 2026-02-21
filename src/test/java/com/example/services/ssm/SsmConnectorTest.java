package com.example.services.ssm;

import com.example.services.ServiceProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.Parameter;
import software.amazon.awssdk.services.ssm.model.ParameterNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class SsmConnectorTest {
    SsmClient mockedSsmClient = Mockito.mock(SsmClient.class);
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
        try (var mockedServiceProvider = mockStatic(ServiceProvider.class)) {
            // When
            SsmConnector.create();
            // Then
            mockedServiceProvider.verify(ServiceProvider::getOrBuildSsmClient, times(1));
        }
    }

    @Test
    void testThatGetParameterReturnsOptionalOfParameterWhenFound() {
        // Given
        var expectedParameter = Parameter.builder().name("dummy").value("dummy").build();
        var getParameterResponse = GetParameterResponse.builder().parameter(expectedParameter).build();

        // When
        when(mockedSsmClient.getParameter(any(GetParameterRequest.class))).thenReturn(getParameterResponse);
        var actualResult = testObject.getParameter("dummy");

        // Then
        assertThat(actualResult)
                .isEqualTo(Optional.of(expectedParameter));
    }

    @Test
    void testThatGetParameterReturnsOptionalOfParameterWhenNotFound() {
        // Given nothing
        // When
        when(mockedSsmClient.getParameter(any(GetParameterRequest.class))).thenThrow(ParameterNotFoundException.class);
        var actualResult = testObject.getParameter("dummy");

        // Then
        assertThat(actualResult)
                .isEqualTo(Optional.empty());
    }

}