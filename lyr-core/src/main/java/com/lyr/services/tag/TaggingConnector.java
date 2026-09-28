package com.lyr.services.tag;

import static java.util.Arrays.stream;

import com.google.common.annotations.VisibleForTesting;
import com.lyr.services.ServiceProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.resourcegroupstaggingapi.ResourceGroupsTaggingApiClient;
import software.amazon.awssdk.services.resourcegroupstaggingapi.model.GetResourcesRequest;
import software.amazon.awssdk.services.resourcegroupstaggingapi.model.GetResourcesResponse;
import software.amazon.awssdk.services.resourcegroupstaggingapi.model.ResourceTagMapping;
import software.amazon.awssdk.services.resourcegroupstaggingapi.model.Tag;

public final class TaggingConnector {

    private final ResourceGroupsTaggingApiClient client;

    private TaggingConnector(final ResourceGroupsTaggingApiClient taggingClient) {
        this.client = taggingClient;
    }

    public static TaggingConnector create() {
        return new TaggingConnector(ServiceProvider.getOrBuildResourceGroupsTaggingApiClient());
    }

    @VisibleForTesting
    static TaggingConnector create(final ResourceGroupsTaggingApiClient taggingClient) {
        return new TaggingConnector(taggingClient);
    }

    public Map<String, List<Tag>> getResourceTagMappingForResourceType(final String resourceType) {
        final var request =
                GetResourcesRequest.builder().resourceTypeFilters(resourceType).build();
        return client.getResourcesPaginator(request).stream()
                .map(GetResourcesResponse::resourceTagMappingList)
                .flatMap(List::stream)
                .collect(Collectors.toMap(ResourceTagMapping::resourceARN, ResourceTagMapping::tags));
    }

    public Map<String, List<Tag>> getResourceTagMappingForResources(final String... resourceArns) {
        final var request =
                GetResourcesRequest.builder().resourceARNList(resourceArns).build();
        final var tableArnToTagMap = client.getResourcesPaginator(request).stream()
                .map(GetResourcesResponse::resourceTagMappingList)
                .flatMap(List::stream)
                .collect(Collectors.toMap(ResourceTagMapping::resourceARN, ResourceTagMapping::tags));

        return stream(resourceArns)
                .collect(Collectors.toMap(
                        tableArn -> tableArn, tableArn -> tableArnToTagMap.getOrDefault(tableArn, new ArrayList<>())));
    }
}
