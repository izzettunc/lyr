package com.lyr.services.tag;

import com.google.common.annotations.VisibleForTesting;
import com.lyr.services.ServiceProvider;
import java.util.List;
import software.amazon.awssdk.services.resourcegroupstaggingapi.ResourceGroupsTaggingApiClient;
import software.amazon.awssdk.services.resourcegroupstaggingapi.model.GetResourcesRequest;
import software.amazon.awssdk.services.resourcegroupstaggingapi.model.GetResourcesResponse;
import software.amazon.awssdk.services.resourcegroupstaggingapi.model.ResourceTagMapping;

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

    public List<ResourceTagMapping> getResourceTagMappingForResourceType(final String resourceType) {
        final var request =
                GetResourcesRequest.builder().resourceTypeFilters(resourceType).build();
        return client.getResourcesPaginator(request).stream()
                .map(GetResourcesResponse::resourceTagMappingList)
                .flatMap(List::stream)
                .toList();
    }

    public List<ResourceTagMapping> getResourceTagMappingForResources(final String... resourceArns) {
        final var request =
                GetResourcesRequest.builder().resourceARNList(resourceArns).build();
        return client.getResourcesPaginator(request).stream()
                .map(GetResourcesResponse::resourceTagMappingList)
                .flatMap(List::stream)
                .toList();
    }
}
