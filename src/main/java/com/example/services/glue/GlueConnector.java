package com.example.services.glue;

import com.example.services.ServiceProvider;
import software.amazon.awssdk.services.glue.GlueClient;
import software.amazon.awssdk.services.glue.model.ListSessionsRequest;
import software.amazon.awssdk.services.glue.model.ListSessionsResponse;
import java.util.List;

public class GlueConnector {
    private final GlueClient client;

    private GlueConnector(GlueClient client) {
        this.client = client;
    }

    public static GlueConnector create() {
        return new GlueConnector(ServiceProvider.getOrBuildGlueClient());
    }

    public static GlueConnector create(GlueClient client) {
        return new GlueConnector(client);
    }

    public List<ListSessionsResponse> getSessionHistory(){
        return client.listSessionsPaginator(ListSessionsRequest.builder().build()).stream().toList();
    }
}
