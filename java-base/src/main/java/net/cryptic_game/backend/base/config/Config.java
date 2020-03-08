package net.cryptic_game.backend.base.config;

import java.util.HashMap;

public class Config {

  private final HashMap<DefaultConfig, Object> values;

  public Config(final DefaultConfig defaultConfig) {
    this.values = new HashMap<>();
    BaseConfig.CONFIG.iniConfig(this);
    defaultConfig.iniConfig(this);
    this.loadEnvironmentVariables();
  }

  private void loadEnvironmentVariables() {
    this.values.forEach((key, value) -> {
      final String env = System.getenv(key.toString());
      if (env != null) {
        this.values.replace(key, env);
      }
    });
  }

  public void set(final DefaultConfig key, final String value) {
    this.values.put(key, value);
  }

  public void set(final DefaultConfig key, final int value) {
    this.values.put(key, value);
  }

  public void set(final DefaultConfig key, final boolean value) {
    this.values.put(key, value);
  }

  public Object get(final DefaultConfig key) {
    return this.values.get(key);
  }

  public String getAsString(final DefaultConfig key) {
    return String.valueOf(this.get(key));
  }

  public int getAsInt(final DefaultConfig key) {
    return Integer.parseInt(this.getAsString(key));
  }

  public boolean getAsBoolean(final DefaultConfig key) {
    return Boolean.parseBoolean(this.getAsString(key));
  }
}
