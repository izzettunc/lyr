package com.lyr.rule.lambda;

import static com.lyr.TestUtil.FUNCTION_1;
import static com.lyr.TestUtil.FUNCTION_2;
import static com.lyr.TestUtil.FUNCTION_3;
import static com.lyr.TestUtil.FUNCTION_4;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.lyr.report.model.Finding;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithXrayTracingNotEnabledRuleConfig;
import com.lyr.services.lambda.LambdaConnector;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import software.amazon.awssdk.services.lambda.model.FunctionConfiguration;
import software.amazon.awssdk.services.lambda.model.TracingConfigResponse;
import software.amazon.awssdk.services.lambda.model.TracingMode;

class ScanLambdaFunctionWithXrayTracingNotEnabledRuleExecutionTest {

    static MockedStatic<LambdaConnector> mockedLambdaConnector = Mockito.mockStatic(LambdaConnector.class);
    static LambdaConnector mockedLambdaConnectorInstance = Mockito.mock(LambdaConnector.class);
    static ScanLambdaFunctionWithXrayTracingNotEnabledRuleExecution testObject;

    @BeforeEach
    public void beforeEach() {
        mockedLambdaConnector.when(LambdaConnector::create).thenReturn(mockedLambdaConnectorInstance);
        testObject = new ScanLambdaFunctionWithXrayTracingNotEnabledRuleExecution();
    }

    @AfterEach
    void afterEach() {
        mockedLambdaConnector.reset();
        reset(mockedLambdaConnectorInstance);
    }

    @AfterAll
    static void afterAll() {
        mockedLambdaConnector.closeOnDemand();
    }

    @Test
    void testThatRuleExecutesSuccessfully() {
        // Given
        final var config =
                ScanLambdaFunctionWithXrayTracingNotEnabledRuleConfig.builder().build();

        final var listOfFunctionConfigurations = List.of(
                createFunctionConfiguration(FUNCTION_1),
                createFunctionConfiguration(FUNCTION_2),
                createFunctionConfiguration(FUNCTION_3),
                createFunctionConfiguration(FUNCTION_4));

        final var expectedResult = Stream.of(FUNCTION_1, FUNCTION_2, FUNCTION_3, FUNCTION_4)
                .map(Finding::byId)
                .collect(ImmutableList.toImmutableList());

        // When
        when(mockedLambdaConnectorInstance.listLambdaFunctionConfigurations()).thenReturn(listOfFunctionConfigurations);
        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void testThatRuleReturnsOnlyFunctionsThatDidNotEnableXrayTracing() {
        // Given
        final var config =
                ScanLambdaFunctionWithXrayTracingNotEnabledRuleConfig.builder().build();

        final var listOfFunctionConfigurations = List.of(
                createFunctionConfiguration(FUNCTION_1, TracingMode.ACTIVE),
                createFunctionConfiguration(FUNCTION_2),
                createFunctionConfiguration(FUNCTION_3, TracingMode.ACTIVE),
                createFunctionConfiguration(FUNCTION_4));

        final var expectedResult =
                Stream.of(FUNCTION_2, FUNCTION_4).map(Finding::byId).collect(ImmutableList.toImmutableList());

        // When
        when(mockedLambdaConnectorInstance.listLambdaFunctionConfigurations()).thenReturn(listOfFunctionConfigurations);
        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void testThatRuleReturnsEmptyListWhenNoLambdaFunctionsArePresent() {
        // Given
        final var config =
                ScanLambdaFunctionWithXrayTracingNotEnabledRuleConfig.builder().build();

        final List<FunctionConfiguration> listOfFunctionConfigurations = List.of();

        final var expectedResult = ImmutableList.of();

        // When
        when(mockedLambdaConnectorInstance.listLambdaFunctionConfigurations()).thenReturn(listOfFunctionConfigurations);
        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    private static FunctionConfiguration createFunctionConfiguration(final String functionName) {
        return createFunctionConfiguration(functionName, TracingMode.PASS_THROUGH);
    }

    private static FunctionConfiguration createFunctionConfiguration(
            final String functionName, final TracingMode tracingMode) {
        return FunctionConfiguration.builder()
                .functionName(functionName)
                .tracingConfig(TracingConfigResponse.builder().mode(tracingMode).build())
                .build();
    }
}
