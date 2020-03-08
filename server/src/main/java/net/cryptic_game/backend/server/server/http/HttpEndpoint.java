package net.cryptic_game.backend.server.server.http;

import com.google.gson.JsonObject;

public abstract class HttpEndpoint {

  private final String name;

  public HttpEndpoint(final String name) {
    this.name = name;
  }

  public abstract JsonObject handleRequest() throws Exception;

  public String getName() {
    return this.name;
  }
}
