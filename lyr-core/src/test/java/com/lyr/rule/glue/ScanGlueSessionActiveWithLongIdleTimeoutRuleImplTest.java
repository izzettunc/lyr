package com.lyr.rule.glue;

import static com.lyr.TestUtil.SESSION_1;
import static com.lyr.TestUtil.SESSION_2;
import static com.lyr.TestUtil.SESSION_3;
import static com.lyr.TestUtil.SESSION_4;
import static com.lyr.TestUtil.SESSION_LT_MAX_IDLE_TIMEOUT;
import static com.lyr.TestUtil.SESSION_PROVISIONING;
import static com.lyr.TestUtil.SESSION_READY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.lyr.report.model.Finding;
import com.lyr.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig;
import com.lyr.services.glue.GlueConnector;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import software.amazon.awssdk.services.glue.model.Session;
import software.amazon.awssdk.services.glue.model.SessionStatus;

class ScanGlueSessionActiveWithLongIdleTimeoutRuleImplTest {

    static MockedStatic<GlueConnector> mockedGlueConnector = Mockito.mockStatic(GlueConnector.class);
    static GlueConnector mockedGlueConnectorInstance = Mockito.mock(GlueConnector.class);
    static ScanGlueSessionActiveWithLongIdleTimeoutRuleExecution testObject;

    @BeforeEach
    public void beforeEach() {
        mockedGlueConnector.when(GlueConnector::create).thenReturn(mockedGlueConnectorInstance);
        testObject = new ScanGlueSessionActiveWithLongIdleTimeoutRuleExecution();
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
        final var maxIdleTimeout = 5;
        final var config = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                .maxIdleTimeoutInMinutes(maxIdleTimeout)
                .build();
        final var listOfSessions = List.of(
                Session.builder()
                        .id(SESSION_1)
                        .status(SessionStatus.READY)
                        .idleTimeout(maxIdleTimeout + maxIdleTimeout)
                        .build(),
                Session.builder()
                        .id(SESSION_2)
                        .status(SessionStatus.READY)
                        .idleTimeout(maxIdleTimeout + maxIdleTimeout)
                        .build(),
                Session.builder()
                        .id(SESSION_3)
                        .status(SessionStatus.READY)
                        .idleTimeout(maxIdleTimeout + maxIdleTimeout)
                        .build(),
                Session.builder()
                        .id(SESSION_4)
                        .status(SessionStatus.READY)
                        .idleTimeout(maxIdleTimeout + maxIdleTimeout)
                        .build());
        final var expectedResult = Stream.of(SESSION_1, SESSION_2, SESSION_3, SESSION_4)
                .map(Finding::byId)
                .collect(ImmutableList.toImmutableList());

        // When
        when(mockedGlueConnectorInstance.getSessionHistory()).thenReturn(listOfSessions);
        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void testThatScanGlueSessionActiveWithLongIdleTimeoutRuleReturnsEmptyListWhenNoSessionFound() {
        // Given
        final var maxIdleTimeout = 5;
        final var config = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                .maxIdleTimeoutInMinutes(maxIdleTimeout)
                .build();
        final List<Session> listOfSessions = List.of();
        final var expectedResult = ImmutableList.of();

        // When
        when(mockedGlueConnectorInstance.getSessionHistory()).thenReturn(listOfSessions);
        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void testThatScanGlueSessionActiveWithLongIdleTimeoutRuleReturnsOnlyTheSessionsThatAreActive() {
        // Given
        final var maxIdleTimeout = 5;
        final var twiceMaxIdleTimeout = 2 * maxIdleTimeout;
        final var config = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                .maxIdleTimeoutInMinutes(maxIdleTimeout)
                .build();
        final var listOfSessions = List.of(
                Session.builder()
                        .id(SESSION_READY)
                        .status(SessionStatus.READY)
                        .idleTimeout(twiceMaxIdleTimeout)
                        .build(),
                Session.builder()
                        .id("sessionFailed")
                        .status(SessionStatus.FAILED)
                        .idleTimeout(twiceMaxIdleTimeout)
                        .build(),
                Session.builder()
                        .id("sessionTimeout")
                        .status(SessionStatus.TIMEOUT)
                        .idleTimeout(twiceMaxIdleTimeout)
                        .build(),
                Session.builder()
                        .id("sessionStopped")
                        .status(SessionStatus.STOPPED)
                        .idleTimeout(twiceMaxIdleTimeout)
                        .build(),
                Session.builder()
                        .id("sessionStopping")
                        .status(SessionStatus.STOPPING)
                        .idleTimeout(twiceMaxIdleTimeout)
                        .build(),
                Session.builder()
                        .id(SESSION_PROVISIONING)
                        .status(SessionStatus.PROVISIONING)
                        .idleTimeout(twiceMaxIdleTimeout)
                        .build());
        final var expectedResult = Stream.of(SESSION_READY, SESSION_PROVISIONING)
                .map(Finding::byId)
                .collect(ImmutableList.toImmutableList());

        // When
        when(mockedGlueConnectorInstance.getSessionHistory()).thenReturn(listOfSessions);
        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void
            testThatScanGlueSessionActiveWithLongIdleTimeoutRuleReturnsOnlyTheSessionsThatHaveIdleTimeoutLongerThanMaxIdleTimeout() {
        final var maxIdleTimeout = 5;
        final var lessThanMaxIdleTimeout = maxIdleTimeout - 3;
        final var twiceMaxIdleTimeout = 2 * maxIdleTimeout;
        final var config = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                .maxIdleTimeoutInMinutes(maxIdleTimeout)
                .build();
        final var listOfSessions = List.of(
                Session.builder()
                        .id(SESSION_1)
                        .status(SessionStatus.READY)
                        .idleTimeout(twiceMaxIdleTimeout)
                        .build(),
                Session.builder()
                        .id(SESSION_LT_MAX_IDLE_TIMEOUT)
                        .status(SessionStatus.PROVISIONING)
                        .idleTimeout(lessThanMaxIdleTimeout)
                        .build(),
                Session.builder()
                        .id(SESSION_LT_MAX_IDLE_TIMEOUT)
                        .status(SessionStatus.READY)
                        .idleTimeout(lessThanMaxIdleTimeout)
                        .build(),
                Session.builder()
                        .id(SESSION_2)
                        .status(SessionStatus.PROVISIONING)
                        .idleTimeout(twiceMaxIdleTimeout)
                        .build(),
                Session.builder()
                        .id(SESSION_3)
                        .status(SessionStatus.PROVISIONING)
                        .idleTimeout(twiceMaxIdleTimeout)
                        .build(),
                Session.builder()
                        .id(SESSION_4)
                        .status(SessionStatus.READY)
                        .idleTimeout(twiceMaxIdleTimeout)
                        .build());
        final var expectedResult = Stream.of(SESSION_1, SESSION_2, SESSION_3, SESSION_4)
                .map(Finding::byId)
                .collect(ImmutableList.toImmutableList());

        // When
        when(mockedGlueConnectorInstance.getSessionHistory()).thenReturn(listOfSessions);
        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }
}
