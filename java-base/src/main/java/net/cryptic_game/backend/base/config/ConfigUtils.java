package net.cryptic_game.backend.base.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class ConfigUtils {

    private static final Logger log = LoggerFactory.getLogger(ConfigUtils.class);
    private static final Yaml yaml = new Yaml();
    private static final String SPACING = "  ";

    private ConfigUtils() {
        throw new UnsupportedOperationException();
    }

    static void parseEnvironment(final Set<Object> objects) {
        final Map<String, String> environment = System.getenv();

        objects.forEach(object -> {
            final Class<?> clazz = object.getClass();
            if (!clazz.isAnnotationPresent(Config.class)) return;
            final String name = clazz.getAnnotation(Config.class).value().toLowerCase().strip();

            final Map<String, Object> values = new HashMap<>();
            environment.forEach((key, value) -> {
                final String keyName = key.strip().toLowerCase();
                if (!name.isEmpty()) {
                    if (keyName.startsWith(name)) {
                        values.put(keyName.substring(name.length() + 1), value.strip().toLowerCase());
                    }
                } else {
                    values.put(keyName, value.strip().toLowerCase());
                }
            });

            ConfigUtils.parseConfigPart(values, object, name);
        });
    }

    static void parseStrings(final Set<Object> objects, final List<String> lines) {
        final Map<String, Object> values = yaml.load(String.join(System.lineSeparator(), lines));

        objects.stream()
                .filter(object -> object.getClass().isAnnotationPresent(Config.class))
                .map(object -> new AbstractMap.SimpleEntry<>(object.getClass().getAnnotation(Config.class).value().strip().toLowerCase(), object))
                .forEach(object -> {
                    final Map<String, Object> currentValues = object.getKey().isBlank() ? values : values == null ? new HashMap<>() : (Map<String, Object>) (values.get(object.getKey()));
                    if (currentValues != null) {
                        ConfigUtils.parseConfigPart(currentValues, object.getValue(), object.getKey());
                    }
                });
    }

    private static void parseConfigPart(final Map<String, Object> values, final Object object, final String name) {
        for (Field field : object.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(ConfigValue.class)) continue;

            final Object value = values.get(field.getAnnotation(ConfigValue.class).value().strip().toLowerCase());

            if (Modifier.isFinal(field.getModifiers())) {
                log.error("Unable to access field \"{}\" in Config \"{}\" because it is \"final\".", field.getName(), name);
            }

            if (value != null) {
                try {
                    field.setAccessible(true);
                    field.set(object, value);
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    log.error("Unable to access field \"{}\" in Config \"{}\".", field.getName(), name);
                }
            }
        }
    }

    static void save(final Set<Object> objects, final OutputStream outputStream) {
        try (final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            objects.forEach(object -> {
                final Class<?> clazz = object.getClass();
                if (!clazz.isAnnotationPresent(Config.class)) return;
                final Config config = clazz.getAnnotation(Config.class);
                final boolean lowLevelConfig = config.value().length() == 0;

                try {
                    if (!config.comment().isBlank()) {
                        writer.write("# " + config.comment().replaceAll(System.lineSeparator(), System.lineSeparator() + "# "));
                        writer.newLine();
                    }
                    if (!lowLevelConfig) {
                        writer.write(config.value() + ":");
                        writer.newLine();
                    }

                    for (final Field field : clazz.getDeclaredFields()) {
                        if (!field.isAnnotationPresent(ConfigValue.class)) continue;
                        final ConfigValue configValue = field.getAnnotation(ConfigValue.class);
                        if (!configValue.comment().isBlank()) {
                            writer.newLine();
                            if (!lowLevelConfig) writer.write(SPACING);
                            writer.write("# " + configValue.comment().replaceAll("\n", System.lineSeparator() + "# "));
                            writer.newLine();
                        }
                        try {
                            if (!lowLevelConfig) writer.write(SPACING);
                            field.setAccessible(true);
                            writer.write(configValue.value() + ": " + field.get(object));
                            field.setAccessible(false);
                            writer.newLine();
                        } catch (IllegalAccessException e) {
                            log.error("Unable to access Config Field \"{}\".", field.getName());
                        }
                    }
                    writer.newLine();
                } catch (IOException e) {
                    log.error("Unable to dump Config to OutputStream.", e);
                }
            });
        } catch (IOException e) {
            log.error("Unable to dump Config to OutputStream.", e);
        }
    }
}
