package net.cryptic_game.backend.data;

import javax.persistence.AttributeConverter;

public final class GroupsAttributeConverter implements AttributeConverter<Group, String> {

    @Override
    public String convertToDatabaseColumn(final Group attribute) {
        return attribute.getId();
    }

    @Override
    public Group convertToEntityAttribute(final String dbData) {
        return Group.byId(dbData);
    }
}
