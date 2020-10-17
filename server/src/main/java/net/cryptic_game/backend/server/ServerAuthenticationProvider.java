package net.cryptic_game.backend.server;

import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Set;

@RequiredArgsConstructor
public final class ServerAuthenticationProvider implements ApiAuthenticationProvider {

    private final String apiKey;

    @Override
    public Set<Group> getGroups() {
        return Set.of(Groups.AUTHORIZED);
    }

    @Override
    public Set<Group> resolveGroups(final String token) {
        return (this.apiKey.isBlank() || this.apiKey.equals(token)) ? Set.of(Groups.AUTHORIZED) : Collections.emptySet();
    }

    @Override
    public boolean isPermitted(final Set<Group> required, final Set<Group> provided) {
        return !provided.isEmpty();
    }

    @Override
    public boolean usesGroups() {
        return false;
    }
}
