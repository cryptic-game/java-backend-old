package net.cryptic_game.backend.base.json;

import com.google.gson.JsonElement;

/**
 * This is an Interface to serialize any {@link Object} to an {@link JsonElement}.
 * <br>
 * Example:
 * <pre>{@code
 * public class User implements JsonSerializable {
 *     private String firstName;
 *     private String lastName;
 *     private String email;
 *
 *     { ... }
 *
 *     public JsonElement serialize() {
 *         final JsonObject json = new JsonObject();
 *         json.addProperty("first_name", this.firstName);
 *         json.addProperty("last_name", this.firstName);
 *         json.addProperty("email", this.firstName);
 *         return json;
 *     }
 * }
 * }</pre>
 *
 * @see JsonSerializable#serialize()
 * @see JsonElement
 */
public interface JsonSerializable {

    /**
     * Converts this to an {@link JsonElement}.
     *
     * @return an {@link JsonElement} with the values from this.
     * @see JsonSerializable
     * @see JsonElement
     */
    JsonElement serialize();
}
