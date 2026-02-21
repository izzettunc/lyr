package com.example.services.ssm;

import com.example.services.ServiceProvider;
import com.google.common.annotations.VisibleForTesting;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.Parameter;
import software.amazon.awssdk.services.ssm.model.ParameterNotFoundException;

import java.util.Optional;

public class SsmConnector {

    private final SsmClient client;

    private SsmConnector(SsmClient client) {
        this.client = client;
    }

    public static SsmConnector create() {
        return new SsmConnector(ServiceProvider.getOrBuildSsmClient());
    }

    @VisibleForTesting
    static SsmConnector create(SsmClient client) {
        return new SsmConnector(client);
    }

    public Optional<Parameter> getParameter(String name) {
        try {
            return Optional.of(client.getParameter(GetParameterRequest.builder()
                    .name(name)
                    .build()).parameter());
        } catch (ParameterNotFoundException parameterNotFoundException) {
            return Optional.empty();
        }
    }

}
