package net.cryptic_game.backend.server.daemon;

import com.google.gson.JsonArray;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollectionData;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointList;
import net.cryptic_game.backend.base.api.endpoint.ApiParameterData;
import net.cryptic_game.backend.base.api.endpoint.ApiParser;
import net.cryptic_game.backend.base.daemon.Daemon;
import net.cryptic_game.backend.base.json.JsonUtils;
import net.cryptic_game.backend.base.utils.DaemonUtils;
import net.cryptic_game.backend.base.utils.HttpClientUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public final class DaemonHandler {

    private final ApiEndpointList endpointList;

    private Object daemonSendObject;
    private Method daemonSendMethod;
    private List<ApiParameterData> daemonSendMethodParameters;

    public DaemonHandler(final ApiEndpointList endpointList) {
        this.endpointList = endpointList;
    }

    public void registerDaemon(final String name, final String url) {
        new Thread(() -> {
            boolean retry;
            do retry = this.handleDaemonRegisterRequest(name, url);
            while (retry);
        }, "register-" + name).start();
    }

    private boolean handleDaemonRegisterRequest(final String name, final String url) {
        try {
            this.addEndpointCollections(DaemonUtils.parseDaemonEndpoints(new Daemon(name, url),
                    JsonUtils.fromJson(HttpClientUtils.sendRequest(url + "/daemon/endpoints").getData(), JsonArray.class)));

            log.info("Successfully registered daemon {} at {}.", name, url);
            return false;
        } catch (IOException e) {
            log.error("Couldn't get endpoints of the daemon {}: {}", name, e.getMessage());
            log.error("Retrying in 10 seconds...");

            try {
                Thread.sleep(10000);
            } catch (InterruptedException ignored) {
            }

            return true;
        }
    }

    public void addEndpointCollections(final Set<ApiEndpointCollectionData> endpointCollections) {
        this.endpointList.addCollections(endpointCollections.stream()
                .peek(collection -> collection.getEndpoints().forEach((name, endpoint) -> {
                    endpoint.setMethod(this.daemonSendMethod);
                    endpoint.setObject(this.daemonSendObject);
                    endpoint.setNormalParameters(false);
                    final List<ApiParameterData> parameters = new ArrayList<>(this.daemonSendMethodParameters);
                    parameters.addAll(endpoint.getParameters());
                    endpoint.setParameters(parameters);
                }))
                .collect(Collectors.toSet()));
    }

    public void setSend(final Object sendObject, final Method sendMethod) {
        this.daemonSendObject = sendObject;
        this.daemonSendMethod = sendMethod;
        this.daemonSendMethodParameters = ApiParser.parseParameters(daemonSendMethod.getParameters());
    }
}
