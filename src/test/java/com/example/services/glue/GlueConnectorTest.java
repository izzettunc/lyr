package com.example.services.glue;

import static com.example.TestUtil.SESSION_1;
import static com.example.TestUtil.SESSION_2;
import static com.example.TestUtil.SESSION_3;
import static com.example.TestUtil.SESSION_4;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.example.services.ServiceProvider;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import software.amazon.awssdk.services.glue.GlueClient;
import software.amazon.awssdk.services.glue.model.ListSessionsRequest;
import software.amazon.awssdk.services.glue.model.ListSessionsResponse;
import software.amazon.awssdk.services.glue.model.Session;
import software.amazon.awssdk.services.glue.paginators.ListSessionsIterable;

class GlueConnectorTest {

    final GlueClient mockedGlueClient = Mockito.mock(GlueClient.class);
    GlueConnector testObject;

    @BeforeEach
    void beforeEach() {
        testObject = GlueConnector.create(mockedGlueClient);
    }

    @AfterEach
    void afterEach() {
        reset(mockedGlueClient);
    }

    @Test
    void testThatGlueConnectorGetsSsmClientFromServiceProvider() {
        // Given
        try (final var mockedServiceProvider = mockStatic(ServiceProvider.class)) {
            // When
            GlueConnector.create();
            // Then
            mockedServiceProvider.verify(ServiceProvider::getOrBuildGlueClient, times(1));
        }
    }

    @Test
    void testThatGetSessionHistoryReturnsListOfListSessionsResponse() {
        // Given
        final var listOfListSessionsResponse = List.of(
                ListSessionsResponse.builder()
                        .sessions(
                                Session.builder().id(SESSION_1).build(),
                                Session.builder().id(SESSION_2).build())
                        .build(),
                ListSessionsResponse.builder()
                        .sessions(
                                Session.builder().id(SESSION_3).build(),
                                Session.builder().id(SESSION_4).build())
                        .build());

        final var mockedListSessionsIterable = mock(ListSessionsIterable.class);

        final var expectedListOfSessions = List.of(
                Session.builder().id(SESSION_1).build(),
                Session.builder().id(SESSION_2).build(),
                Session.builder().id(SESSION_3).build(),
                Session.builder().id(SESSION_4).build());

        // When
        when(mockedListSessionsIterable.stream()).thenReturn(listOfListSessionsResponse.stream());
        when(mockedGlueClient.listSessionsPaginator(any(ListSessionsRequest.class)))
                .thenReturn(mockedListSessionsIterable);

        final var actualResult = testObject.getSessionHistory();

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedListOfSessions);
    }
}
