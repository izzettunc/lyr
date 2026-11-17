package com.lyr.rule.lambda;

import static com.lyr.TestUtil.FUNCTION_1;
import static com.lyr.TestUtil.FUNCTION_2;
import static com.lyr.TestUtil.FUNCTION_3;
import static com.lyr.TestUtil.FUNCTION_4;
import static com.lyr.TestUtil.createImmutableListOfFindings;
import static com.lyr.TestUtil.in;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.lyr.rule.lambda.config.ScanLambdaFunctionWithHighErrorRateRuleConfig;
import com.lyr.services.cloudwatch.CloudWatchConnector;
import com.lyr.services.cloudwatch.CloudWatchUtil;
import com.lyr.services.lambda.LambdaConnector;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class ScanLambdaFunctionWithHighErrorRateRuleExecutionTest {

    static MockedStatic<CloudWatchUtil> mockedCloudWatchUtil = Mockito.mockStatic(CloudWatchUtil.class);
    static MockedStatic<CloudWatchConnector> mockedCloudWatchConnector = Mockito.mockStatic(CloudWatchConnector.class);
    static CloudWatchConnector mockedCloudWatchConnectorInstance = Mockito.mock(CloudWatchConnector.class);
    static MockedStatic<LambdaConnector> mockedLambdaConnector = Mockito.mockStatic(LambdaConnector.class);
    static LambdaConnector mockedLambdaConnectorInstance = Mockito.mock(LambdaConnector.class);
    static ScanLambdaFunctionWithHighErrorRateRuleExecution testObject;

    @BeforeEach
    public void beforeEach() {
        mockedLambdaConnector.when(LambdaConnector::create).thenReturn(mockedLambdaConnectorInstance);
        mockedCloudWatchConnector.when(CloudWatchConnector::create).thenReturn(mockedCloudWatchConnectorInstance);
        testObject = new ScanLambdaFunctionWithHighErrorRateRuleExecution();
    }

    @AfterEach
    void afterEach() {
        mockedLambdaConnector.reset();
        mockedCloudWatchConnector.reset();
        mockedCloudWatchUtil.reset();
        reset(mockedLambdaConnectorInstance, mockedCloudWatchConnectorInstance);
    }

    @AfterAll
    static void afterAll() {
        mockedLambdaConnector.closeOnDemand();
        mockedCloudWatchConnector.closeOnDemand();
        mockedCloudWatchUtil.closeOnDemand();
    }

    @Test
    void testThatRuleExecutesSuccessfully() {
        // Given
        final var config =
                ScanLambdaFunctionWithHighErrorRateRuleConfig.builder().build();

        final var listOfFunctions = List.of(FUNCTION_1, FUNCTION_2, FUNCTION_3, FUNCTION_4);
        final var expectedFindings = createImmutableListOfFindings(FUNCTION_1, FUNCTION_2, FUNCTION_3, FUNCTION_4);

        final var timeWindow = 1;
        final var numberOfInvocations = 10d;
        final var numberOfErrors = 10d;

        // When
        mockedCloudWatchUtil
                .when(() -> CloudWatchUtil.getAppropriateTimeWindowForPeriod(anyInt()))
                .thenReturn(timeWindow);
        when(mockedCloudWatchConnectorInstance.getTotalInvocationsOfALambdaFunction(any(), any()))
                .thenReturn(numberOfInvocations);
        when(mockedCloudWatchConnectorInstance.getTotalErrorsOfALambdaFunction(any(), any()))
                .thenReturn(numberOfErrors);
        when(mockedLambdaConnectorInstance.listLambdaFunctionNames()).thenReturn(listOfFunctions);

        final var actualFindings = testObject.execute(config);

        // Then
        assertThat(actualFindings)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedFindings);
    }

    @Test
    void testThatRuleReturnsEmptyListWhenNoFunctionsAvailable() {
        // Given
        final var config =
                ScanLambdaFunctionWithHighErrorRateRuleConfig.builder().build();

        final var expectedFindings = List.of();

        final var timeWindow = 1;

        // When
        mockedCloudWatchUtil
                .when(() -> CloudWatchUtil.getAppropriateTimeWindowForPeriod(anyInt()))
                .thenReturn(timeWindow);
        when(mockedLambdaConnectorInstance.listLambdaFunctionNames()).thenReturn(List.of());

        final var actualFindings = testObject.execute(config);

        // Then
        assertThat(actualFindings)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedFindings);

        verifyNoInteractions(mockedCloudWatchConnectorInstance);
    }

    @ParameterizedTest
    @CsvSource({"50, 1000, 500, 1000, 499", "10, 10, 5, 10, 0", "50, 10, 10, 0 ,0"})
    void testThatRuleReturnsOnlyTheOnesWithErrorRateHigherThanThreshold(
            final double threshold,
            final double numberOfInvocations,
            final double numberOfErrors,
            final double safeNumberOfInvocations,
            final double safeNumberOfErrors) {
        // Given threshold, number of invocations, number of errors
        final var config = ScanLambdaFunctionWithHighErrorRateRuleConfig.builder()
                .errorRateThreshold(threshold)
                .build();

        final var listOfFunctions = List.of(FUNCTION_1, FUNCTION_2, FUNCTION_3, FUNCTION_4);
        final var expectedFindings = createImmutableListOfFindings(FUNCTION_2, FUNCTION_3);

        final var timeWindow = 1;

        // When
        mockedCloudWatchUtil
                .when(() -> CloudWatchUtil.getAppropriateTimeWindowForPeriod(anyInt()))
                .thenReturn(timeWindow);

        when(mockedCloudWatchConnectorInstance.getTotalInvocationsOfALambdaFunction(in(FUNCTION_1, FUNCTION_4), any()))
                .thenReturn(safeNumberOfInvocations);
        when(mockedCloudWatchConnectorInstance.getTotalErrorsOfALambdaFunction(in(FUNCTION_1, FUNCTION_4), any()))
                .thenReturn(safeNumberOfErrors);

        when(mockedCloudWatchConnectorInstance.getTotalInvocationsOfALambdaFunction(in(FUNCTION_2, FUNCTION_3), any()))
                .thenReturn(numberOfInvocations);
        when(mockedCloudWatchConnectorInstance.getTotalErrorsOfALambdaFunction(in(FUNCTION_2, FUNCTION_3), any()))
                .thenReturn(numberOfErrors);

        when(mockedLambdaConnectorInstance.listLambdaFunctionNames()).thenReturn(listOfFunctions);

        final var actualFindings = testObject.execute(config);

        // Then
        assertThat(actualFindings)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedFindings);
    }
}
