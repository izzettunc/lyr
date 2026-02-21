package com.example.services.glue;

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

    GlueClient mockedGlueClient = Mockito.mock(GlueClient.class);
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
        try (var mockedServiceProvider = mockStatic(ServiceProvider.class)) {
            // When
            GlueConnector.create();
            // Then
            mockedServiceProvider.verify(ServiceProvider::getOrBuildGlueClient, times(1));
        }
    }

    @Test
    void testThatGetSessionHistoryReturnsListOfListSessionsResponse() {
        // Given
        var listOfListSessionsResponse = List.of(
                ListSessionsResponse.builder()
                        .sessions(
                                Session.builder().id("12").build(),
                                Session.builder().id("34").build())
                        .build(),
                ListSessionsResponse.builder()
                        .sessions(
                                Session.builder().id("56").build(),
                                Session.builder().id("78").build())
                        .build());
        var mockedListSessionsIterable = mock(ListSessionsIterable.class);

        // When
        when(mockedListSessionsIterable.stream()).thenReturn(listOfListSessionsResponse.stream());
        when(mockedGlueClient.listSessionsPaginator(any(ListSessionsRequest.class)))
                .thenReturn(mockedListSessionsIterable);

        var actualResult = testObject.getSessionHistory();

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(listOfListSessionsResponse);
    }
}
