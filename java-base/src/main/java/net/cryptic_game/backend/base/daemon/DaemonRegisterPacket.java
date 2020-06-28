package net.cryptic_game.backend.base.daemon;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.netty.channel.Channel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollectionData;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointData;
import net.cryptic_game.backend.base.api.endpoint.ApiParameterSpecialType;
import net.cryptic_game.backend.base.json.JsonTransient;
import net.cryptic_game.backend.base.json.JsonUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode
public final class DaemonRegisterPacket {

    @JsonTransient
    private final Daemon daemon;
    private final String name;
    private final Set<ApiEndpointCollectionData> collections;

    public DaemonRegisterPacket(final String name) {
        this.daemon = null;
        this.name = name;
        this.collections = new HashSet<>();
    }

    public DaemonRegisterPacket(final Channel channel, final String name, final JsonArray collections) {
        this.daemon = new Daemon(channel, name);
        this.name = name;
        this.collections = JsonUtils.fromArray(collections, new HashSet<>(), json -> {
            final JsonObject jsonObject = JsonUtils.fromJson(json, JsonObject.class);

            final String collectionName = JsonUtils.fromJson(jsonObject.get("name"), String.class);
            final String collectionDescription = JsonUtils.fromJson(jsonObject.get("description"), String.class);

            final Map<String, ApiEndpointData> endpoints = JsonUtils.fromArray(
                    JsonUtils.fromJson(jsonObject.get("endpoints"), JsonArray.class),
                    new TreeSet<>(), DaemonEndpointData.class)
                    .stream()
                    .peek(endpoint -> {
                        endpoint.setDaemon(this.daemon);
                        endpoint.getParameters().forEach(parameter -> {
                            if (parameter.getSpecial() == null) parameter.setSpecial(ApiParameterSpecialType.NORMAL);
                        });
                    })
                    .collect(Collectors.toMap(ApiEndpointData::getName, endpoint -> endpoint));

            final DaemonEndpointCollectionData collection = new DaemonEndpointCollectionData(collectionName, collectionDescription, null, endpoints);
            collection.setDaemon(this.daemon);
            return collection;
        });
    }

    public void addEndpointCollections(final Collection<ApiEndpointCollectionData> collections) {
        this.collections.addAll(collections);
    }
}
