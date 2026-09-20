package com.lyr.services.tag;

import static com.lyr.TestUtil.DUMMY_STRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.lyr.services.ServiceProvider;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import software.amazon.awssdk.services.resourcegroupstaggingapi.ResourceGroupsTaggingApiClient;
import software.amazon.awssdk.services.resourcegroupstaggingapi.model.GetResourcesRequest;
import software.amazon.awssdk.services.resourcegroupstaggingapi.model.GetResourcesResponse;
import software.amazon.awssdk.services.resourcegroupstaggingapi.model.ResourceTagMapping;
import software.amazon.awssdk.services.resourcegroupstaggingapi.model.Tag;
import software.amazon.awssdk.services.resourcegroupstaggingapi.paginators.GetResourcesIterable;

class TaggingConnectorTest {

    final ResourceGroupsTaggingApiClient mockedTaggingClient = Mockito.mock(ResourceGroupsTaggingApiClient.class);
    TaggingConnector testObject;

    @BeforeEach
    void beforeEach() {
        testObject = TaggingConnector.create(mockedTaggingClient);
    }

    @AfterEach
    void afterEach() {
        reset(mockedTaggingClient);
    }

    @Test
    void testThatTaggingConnectorGetsClientFromServiceProvider() {
        // Given
        try (final var mockedServiceProvider = mockStatic(ServiceProvider.class)) {
            // When
            TaggingConnector.create();
            // Then
            mockedServiceProvider.verify(ServiceProvider::getOrBuildResourceGroupsTaggingApiClient, times(1));
        }
    }

    @Test
    void testThatGetResourceTagMappingForResourceTypeReturnsListOfResourceTagMappings() {
        // Given
        final var tag1 = Tag.builder().key("Environment").value("Production").build();
        final var tag2 = Tag.builder().key("Owner").value("TeamA").build();

        final var resourceTagMapping1 = ResourceTagMapping.builder()
                .resourceARN("arn:aws:dynamodb:us-east-1:123456789012:table/table1")
                .tags(tag1, tag2)
                .build();

        final var resourceTagMapping2 = ResourceTagMapping.builder()
                .resourceARN("arn:aws:dynamodb:us-east-1:123456789012:table/table2")
                .tags(tag1)
                .build();

        final var listOfGetResourcesResponses = List.of(
                GetResourcesResponse.builder()
                        .resourceTagMappingList(resourceTagMapping1)
                        .build(),
                GetResourcesResponse.builder()
                        .resourceTagMappingList(resourceTagMapping2)
                        .build());

        final var mockedGetResourcesIterable = mock(GetResourcesIterable.class);

        final var expectedResourceTagMappings = List.of(resourceTagMapping1, resourceTagMapping2);

        // When
        when(mockedGetResourcesIterable.stream()).thenReturn(listOfGetResourcesResponses.stream());
        when(mockedTaggingClient.getResourcesPaginator((GetResourcesRequest) argThat(
                        req -> ((GetResourcesRequest) req).resourceTypeFilters().contains(DUMMY_STRING))))
                .thenReturn(mockedGetResourcesIterable);

        final var actualResult = testObject.getResourceTagMappingForResourceType(DUMMY_STRING);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResourceTagMappings);
    }

    @Test
    void testThatGetResourceTagMappingForResourceTypeReturnsEmptyListWhenNoResources() {
        // Given
        final var listOfGetResourcesResponses =
                List.of(GetResourcesResponse.builder().build());

        final var mockedGetResourcesIterable = mock(GetResourcesIterable.class);

        // When
        when(mockedGetResourcesIterable.stream()).thenReturn(listOfGetResourcesResponses.stream());
        when(mockedTaggingClient.getResourcesPaginator(any(GetResourcesRequest.class)))
                .thenReturn(mockedGetResourcesIterable);

        final var actualResult = testObject.getResourceTagMappingForResourceType(DUMMY_STRING);

        // Then
        assertThat(actualResult).isEmpty();
    }

    @Test
    void testThatGetResourceTagMappingForResourcesReturnsListOfResourceTagMappings() {
        // Given
        final var arn1 = "arn:aws:dynamodb:us-east-1:123456789012:table/table1";
        final var arn2 = "arn:aws:dynamodb:us-east-1:123456789012:table/table2";

        final var tag1 = Tag.builder().key("Environment").value("Production").build();

        final var resourceTagMapping1 =
                ResourceTagMapping.builder().resourceARN(arn1).tags(tag1).build();

        final var resourceTagMapping2 =
                ResourceTagMapping.builder().resourceARN(arn2).tags(tag1).build();

        final var listOfGetResourcesResponses = List.of(GetResourcesResponse.builder()
                .resourceTagMappingList(resourceTagMapping1, resourceTagMapping2)
                .build());

        final var mockedGetResourcesIterable = mock(GetResourcesIterable.class);

        final var expectedResourceTagMappings = List.of(resourceTagMapping1, resourceTagMapping2);

        // When
        when(mockedGetResourcesIterable.stream()).thenReturn(listOfGetResourcesResponses.stream());
        when(mockedTaggingClient.getResourcesPaginator((GetResourcesRequest) argThat(
                        req -> ((GetResourcesRequest) req).resourceARNList().containsAll(List.of(arn1, arn2)))))
                .thenReturn(mockedGetResourcesIterable);

        final var actualResult = testObject.getResourceTagMappingForResources(arn1, arn2);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResourceTagMappings);
    }

    @Test
    void testThatGetResourceTagMappingForResourcesReturnsEmptyListWhenNoResources() {
        // Given
        final var listOfGetResourcesResponses =
                List.of(GetResourcesResponse.builder().build());

        final var mockedGetResourcesIterable = mock(GetResourcesIterable.class);

        // When
        when(mockedGetResourcesIterable.stream()).thenReturn(listOfGetResourcesResponses.stream());
        when(mockedTaggingClient.getResourcesPaginator(any(GetResourcesRequest.class)))
                .thenReturn(mockedGetResourcesIterable);

        final var actualResult = testObject.getResourceTagMappingForResources(DUMMY_STRING);

        // Then
        assertThat(actualResult).isEmpty();
    }
}
