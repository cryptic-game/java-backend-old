package net.cryptic_game.backend.base.api.data;

import net.cryptic_game.backend.base.api.Group;

import java.util.Set;

public interface ApiAuthenticationProvider {

    Set<Group> getGroups();

    Set<Group> resolveGroups(String jwt);

    boolean isPermitted(Set<Group> required, Set<Group> provided);
}
