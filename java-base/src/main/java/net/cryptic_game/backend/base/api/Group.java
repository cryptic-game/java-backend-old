package net.cryptic_game.backend.base.api;

import java.util.Set;

public interface Group {

    String getId();

    String getDisplayName();

    Set<Group> getChildren();
}
