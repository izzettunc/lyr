package com.example.services.ssm;

import com.example.services.ServiceProvider;
import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.Parameter;
import software.amazon.awssdk.services.ssm.model.ParameterNotFoundException;

public final class SsmConnector {

    private final SsmClient client;

    private SsmConnector(final SsmClient ssmClient) {
        this.client = ssmClient;
    }

    public static SsmConnector create() {
        return new SsmConnector(ServiceProvider.getOrBuildSsmClient());
    }

    @VisibleForTesting
    static SsmConnector create(final SsmClient ssmClient) {
        return new SsmConnector(ssmClient);
    }

    public Optional<Parameter> getParameter(final String name) {
        try {
            return Optional.of(
                    client.getParameter(GetParameterRequest.builder().name(name).build())
                            .parameter());
        } catch (final ParameterNotFoundException parameterNotFoundException) {
            return Optional.empty();
        }
    }
}
