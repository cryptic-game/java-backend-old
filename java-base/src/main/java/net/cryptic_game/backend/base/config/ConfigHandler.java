package net.cryptic_game.backend.base.config;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public final class ConfigHandler {

    private final boolean useEnvironmentConfig;
    private final File configFile;

    private final Set<Object> configs;

    public ConfigHandler(final boolean useEnvironmentConfig) {
        this.useEnvironmentConfig = useEnvironmentConfig;
        this.configFile = new File("config.yaml");

        try {
            if (!this.useEnvironmentConfig && this.configFile.createNewFile()) {
                log.info("Config file \"{}\" was successfully created.", this.configFile.getName());
            }
        } catch (IOException e) {
            log.error("Unable to create Config file \"" + this.configFile.getName() + "\".", e);
        }
        this.configs = new HashSet<>();
    }

    public void loadConfig() {
        if (this.useEnvironmentConfig) ConfigUtils.parseEnvironment(this.configs);
        else {
            final List<String> content = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(new FileReader(this.configFile))) {
                String line;
                while ((line = reader.readLine()) != null) if (!line.isBlank()) content.add(line);
            } catch (IOException e) {
                log.error("Unable to read Config file \"" + this.configFile.getName() + "\".", e);
            }

            ConfigUtils.parseStrings(this.configs, content);

            try {
                ConfigUtils.save(this.configs, new FileOutputStream(this.configFile));
            } catch (FileNotFoundException e) {
                log.error("Unable to save Config \"" + this.configFile.getName() + "\"", e);
            }
        }
    }

    public <T> T addConfig(final T config) {
        this.configs.add(config);
        return config;
    }
}
