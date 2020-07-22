package net.cryptic_game.backend.server.daemon;

import com.google.gson.JsonObject;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollectionData;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointList;
import net.cryptic_game.backend.base.api.endpoint.ApiParameterData;
import net.cryptic_game.backend.base.api.endpoint.ApiParser;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import net.cryptic_game.backend.base.daemon.Daemon;
import net.cryptic_game.backend.base.daemon.DaemonEndpointCollectionData;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonUtils;
import net.cryptic_game.backend.base.utils.ApiUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public final class DaemonHandler {

    private final ApiEndpointList endpointList;
    private final HashMap<String, ClientRespond> channel;
    private final Set<Daemon> daemons;
    private Object daemonSendObject;
    private Method daemonSendMethod;
    private List<ApiParameterData> daemonSendMethodParameters;

    public DaemonHandler(final ApiEndpointList endpointList) {
        this.endpointList = endpointList;
        this.daemons = new HashSet<>();
        this.channel = new HashMap<>();
    }

    public void addDaemon(final Daemon daemon) {
        this.daemons.add(daemon);
    }

    public void removeDaemon(final Channel channel) {
        final Daemon daemon = this.daemons.stream().filter(d -> d.getChannel().equals(channel)).findFirst().orElse(null);
        if (daemon != null) {
            this.daemons.remove(daemon);

            final Iterator<Map.Entry<String, ApiEndpointCollectionData>> iterator = this.endpointList.getCollections().entrySet().iterator();
            iterator.forEachRemaining(entry -> {
                if (entry.getValue() instanceof DaemonEndpointCollectionData) {
                    final DaemonEndpointCollectionData endpointCollection = (DaemonEndpointCollectionData) entry.getValue();
                    if (endpointCollection.getDaemon().equals(daemon)) {
                        iterator.remove();
                        endpointCollection.getEndpoints().forEach((name, endpoint) -> this.endpointList.getEndpoints().remove(name));
                    }
                }
            });

            log.info("Removed daemon \"" + daemon.getName() + "\"");
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

    public Set<Daemon> getDaemons() {
        return this.daemons;
    }

    public void respondToClient(final JsonObject json) {
        final ClientRespond respond = this.channel.remove(json.get("tag").getAsString());
        if (respond == null) return;
        final JsonBuilder info = JsonBuilder.create(json.get("info").getAsJsonObject());
        if (JsonUtils.fromJson(JsonUtils.fromJson(json.get("info"), JsonObject.class).get("code"), int.class) == ApiResponseType.INTERNAL_SERVER_ERROR.getCode()) {
            final ApiResponseType type = ApiResponseType.BAD_GATEWAY;
            info.add("code", type.getCode());
            info.add("name", type.name());
        }
        ApiUtils.response(respond.getChannel(), respond.getTag(), info.build(), json.get("data"));
    }

    public void addWebSocketRespond(final String tag, final String userTag, final Channel channel) {
        this.channel.put(tag, new ClientRespond(userTag, channel));
    }

    public boolean isRequestOpen(final String tag) {
        return this.channel.containsKey(tag);
    }

    public void setSend(final Object sendObject, final Method sendMethod) {
        this.daemonSendObject = sendObject;
        this.daemonSendMethod = sendMethod;
        this.daemonSendMethodParameters = ApiParser.parseParameters(daemonSendMethod.getParameters());
    }
}
