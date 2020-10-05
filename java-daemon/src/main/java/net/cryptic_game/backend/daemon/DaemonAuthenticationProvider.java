package net.cryptic_game.backend.daemon;

import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.base.api.Group;
import net.cryptic_game.backend.base.api.data.ApiAuthenticationProvider;

import java.util.Collections;
import java.util.Set;

@RequiredArgsConstructor
public class DaemonAuthenticationProvider implements ApiAuthenticationProvider {

    private final String apiKey;

    @Override
    public Set<Group> getGroups() {
        return Set.of(Groups.AUTHORIZED);
    }

    @Override
    public Set<Group> resolveGroups(final String token) {
        return (apiKey.isBlank() || apiKey.equals(token)) ? Set.of(Groups.AUTHORIZED) : Collections.emptySet();
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
