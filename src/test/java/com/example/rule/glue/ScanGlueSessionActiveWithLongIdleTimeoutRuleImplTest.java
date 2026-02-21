package com.example.rule.glue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import com.example.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig;
import com.example.rule.outcome.ScanOutcome;
import com.example.services.glue.GlueConnector;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import software.amazon.awssdk.services.glue.model.ListSessionsResponse;
import software.amazon.awssdk.services.glue.model.Session;
import software.amazon.awssdk.services.glue.model.SessionStatus;

class ScanGlueSessionActiveWithLongIdleTimeoutRuleImplTest {

    static MockedStatic<GlueConnector> mockedGlueConnector = Mockito.mockStatic(GlueConnector.class);
    static GlueConnector mockedGlueConnectorInstance = Mockito.mock(GlueConnector.class);
    static ScanGlueSessionActiveWithLongIdleTimeoutRuleImpl testObject;

    @BeforeEach
    public void beforeEach() {
        mockedGlueConnector.when(GlueConnector::create).thenReturn(mockedGlueConnectorInstance);
        testObject = new ScanGlueSessionActiveWithLongIdleTimeoutRuleImpl();
    }

    @AfterEach
    public void afterEach() {
        mockedGlueConnector.reset();
        reset(mockedGlueConnectorInstance);
    }

    @AfterAll
    public static void afterAll() {
        mockedGlueConnector.closeOnDemand();
    }

    @Test
    void testThatScanGlueSessionActiveWithLongIdleTimeoutRuleExecutesSuccessfully() {
        // Given
        var maxIdleTimeout = 5;
        var config = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                .maxIdleTimeoutInMinutes(maxIdleTimeout)
                .build();
        var listOfListSessionsResponse = List.of(
                ListSessionsResponse.builder()
                        .sessions(
                                Session.builder()
                                        .id("session1")
                                        .status(SessionStatus.READY)
                                        .idleTimeout(maxIdleTimeout + 5)
                                        .build(),
                                Session.builder()
                                        .id("session2")
                                        .status(SessionStatus.READY)
                                        .idleTimeout(maxIdleTimeout + 5)
                                        .build())
                        .build(),
                ListSessionsResponse.builder()
                        .sessions(
                                Session.builder()
                                        .id("session3")
                                        .status(SessionStatus.READY)
                                        .idleTimeout(maxIdleTimeout + 5)
                                        .build(),
                                Session.builder()
                                        .id("session4")
                                        .status(SessionStatus.READY)
                                        .idleTimeout(maxIdleTimeout + 5)
                                        .build())
                        .build());
        var expectedResult = Stream.of("session1", "session2", "session3", "session4")
                .map(ScanOutcome::new)
                .collect(ImmutableList.toImmutableList());

        // When
        when(mockedGlueConnectorInstance.getSessionHistory()).thenReturn(listOfListSessionsResponse);
        var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void testThatScanGlueSessionActiveWithLongIdleTimeoutRuleReturnsEmptyListWhenNoSessionFound() {
        // Given
        var maxIdleTimeout = 5;
        var config = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                .maxIdleTimeoutInMinutes(maxIdleTimeout)
                .build();
        var listOfListSessionsResponse = List.of(ListSessionsResponse.builder().build());
        var expectedResult = ImmutableList.of();

        // When
        when(mockedGlueConnectorInstance.getSessionHistory()).thenReturn(listOfListSessionsResponse);
        var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void testThatScanGlueSessionActiveWithLongIdleTimeoutRuleReturnsOnlyTheSessionsThatAreActive() {
        // Given
        var maxIdleTimeout = 5;
        var config = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                .maxIdleTimeoutInMinutes(maxIdleTimeout)
                .build();
        var listOfListSessionsResponse = List.of(
                ListSessionsResponse.builder()
                        .sessions(
                                Session.builder()
                                        .id("sessionReady")
                                        .status(SessionStatus.READY)
                                        .idleTimeout(maxIdleTimeout + 5)
                                        .build(),
                                Session.builder()
                                        .id("sessionFailed")
                                        .status(SessionStatus.FAILED)
                                        .idleTimeout(maxIdleTimeout + 5)
                                        .build(),
                                Session.builder()
                                        .id("sessionTimeout")
                                        .status(SessionStatus.TIMEOUT)
                                        .idleTimeout(maxIdleTimeout + 5)
                                        .build())
                        .build(),
                ListSessionsResponse.builder()
                        .sessions(
                                Session.builder()
                                        .id("sessionStopped")
                                        .status(SessionStatus.STOPPED)
                                        .idleTimeout(maxIdleTimeout + 5)
                                        .build(),
                                Session.builder()
                                        .id("sessionStopping")
                                        .status(SessionStatus.STOPPING)
                                        .idleTimeout(maxIdleTimeout + 5)
                                        .build())
                        .build(),
                ListSessionsResponse.builder()
                        .sessions(Session.builder()
                                .id("sessionProvisioning")
                                .status(SessionStatus.PROVISIONING)
                                .idleTimeout(maxIdleTimeout + 5)
                                .build())
                        .build());
        var expectedResult = Stream.of("sessionReady", "sessionProvisioning")
                .map(ScanOutcome::new)
                .collect(ImmutableList.toImmutableList());

        // When
        when(mockedGlueConnectorInstance.getSessionHistory()).thenReturn(listOfListSessionsResponse);
        var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void
            testThatScanGlueSessionActiveWithLongIdleTimeoutRuleReturnsOnlyTheSessionsThatHaveIdleTimeoutLongerThanMaxIdleTimeout() {
        var maxIdleTimeout = 5;
        var config = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                .maxIdleTimeoutInMinutes(maxIdleTimeout)
                .build();
        var listOfListSessionsResponse = List.of(
                ListSessionsResponse.builder()
                        .sessions(
                                Session.builder()
                                        .id("session1")
                                        .status(SessionStatus.READY)
                                        .idleTimeout(maxIdleTimeout + 5)
                                        .build(),
                                Session.builder()
                                        .id("sessionLTMaxIdleTimeout")
                                        .status(SessionStatus.PROVISIONING)
                                        .idleTimeout(maxIdleTimeout - 3)
                                        .build())
                        .build(),
                ListSessionsResponse.builder()
                        .sessions(
                                Session.builder()
                                        .id("sessionLTMaxIdleTimeout")
                                        .status(SessionStatus.READY)
                                        .idleTimeout(maxIdleTimeout - 3)
                                        .build(),
                                Session.builder()
                                        .id("session2")
                                        .status(SessionStatus.PROVISIONING)
                                        .idleTimeout(maxIdleTimeout + 5)
                                        .build())
                        .build(),
                ListSessionsResponse.builder()
                        .sessions(Session.builder()
                                .id("session3")
                                .status(SessionStatus.PROVISIONING)
                                .idleTimeout(maxIdleTimeout + 5)
                                .build())
                        .build(),
                ListSessionsResponse.builder()
                        .sessions(Session.builder()
                                .id("session4")
                                .status(SessionStatus.READY)
                                .idleTimeout(maxIdleTimeout + 5)
                                .build())
                        .build());
        var expectedResult = Stream.of("session1", "session2", "session3", "session4")
                .map(ScanOutcome::new)
                .collect(ImmutableList.toImmutableList());

        // When
        when(mockedGlueConnectorInstance.getSessionHistory()).thenReturn(listOfListSessionsResponse);
        var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }
}
