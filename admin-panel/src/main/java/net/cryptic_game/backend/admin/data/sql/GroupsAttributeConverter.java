package net.cryptic_game.backend.admin.data.sql;

import net.cryptic_game.backend.admin.Groups;

import javax.persistence.AttributeConverter;

public class GroupsAttributeConverter implements AttributeConverter<Groups, String> {

    @Override
    public String convertToDatabaseColumn(final Groups attribute) {
        return attribute.getId();
    }

    @Override
    public Groups convertToEntityAttribute(String dbData) {
        return Groups.byId(dbData);
    }
}
