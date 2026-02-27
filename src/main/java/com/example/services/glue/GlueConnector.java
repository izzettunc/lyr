package com.example.services.glue;

import com.example.services.ServiceProvider;
import java.util.List;
import software.amazon.awssdk.services.glue.GlueClient;
import software.amazon.awssdk.services.glue.model.ListSessionsRequest;
import software.amazon.awssdk.services.glue.model.ListSessionsResponse;

public final class GlueConnector {
    private final GlueClient client;

    private GlueConnector(final GlueClient glueClient) {
        this.client = glueClient;
    }

    public static GlueConnector create() {
        return new GlueConnector(ServiceProvider.getOrBuildGlueClient());
    }

    public static GlueConnector create(final GlueClient glueClient) {
        return new GlueConnector(glueClient);
    }

    public List<ListSessionsResponse> getSessionHistory() {
        return client.listSessionsPaginator(ListSessionsRequest.builder().build()).stream()
                .toList();
    }
}
