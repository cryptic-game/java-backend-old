package net.cryptic_game.backend.processor.config;

import com.google.auto.service.AutoService;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;

import org.yaml.snakeyaml.Yaml;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@SupportedAnnotationTypes("net.cryptic_game.backend.processor.config.ConfigValue")
public class ConfigProcessor extends AbstractProcessor {

    private final Map<String, Object> config;

    public ConfigProcessor() {
        final File workingDir = new File(System.getProperty("user.dir"), "config/gamedesign/parameter");
        this.config = this.loadConfig(workingDir);
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        annotations.stream()
                .flatMap(annotation -> ElementFilter.fieldsIn(roundEnv.getElementsAnnotatedWith(annotation)).stream())
                .forEach(this::process);

        return true;
    }

    private void process(final VariableElement element) {
        final ConfigValue configValue = element.getAnnotation(ConfigValue.class);
        final String name = configValue.value().isEmpty() ? element.getSimpleName().toString() : configValue.value();

        final Object value = this.getValue(this.config, new LinkedList<>(List.of(name.toLowerCase(Locale.ROOT).split("_"))));

        if (value == null) {
            throw new RuntimeException(String.format("Unable to find config value \"%s\".", name));
        }

        if (!element.asType().toString().equals(value.getClass().getName())) {
            throw new RuntimeException(String.format(
                    "Config value \"%s\" has wrong type. (Required: %s, Provided: %s)",
                    name,
                    element.asType().toString(),
                    value.getClass().getName()
            ));
        }

        try {
            final Method method = element.getClass()
                    .getMethod("setData", Object.class);

            method.setAccessible(true);
            method.invoke(element, value);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> loadConfig(final File configDir) {
        final File[] files = configDir.listFiles();
        if (files == null) {
            return Collections.emptyMap();
        }

        Map<String, Object> config = null;

        for (final File file : files) {
            final String name = file.getName().toLowerCase(Locale.ENGLISH);
            if (file.isDirectory() || !(name.endsWith(".yaml") || name.endsWith(".yml"))) continue;

            final Map<String, Object> data;

            try (Reader reader = new FileReader(file)) {
                data = new Yaml().load(reader);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (config == null) config = data;
            else this.merge(config, data);
        }

        return config;
    }

    private void merge(final Map<String, Object> first, final Map<String, Object> second) {
        second.forEach((secondKey, secondValue) -> {
            final Object firstValue = first.get(secondKey);

            if (firstValue instanceof Map && secondValue instanceof Map) {
                this.merge((Map<String, Object>) firstValue, (Map<String, Object>) secondValue);
                return;
            }

            first.put(secondKey, secondValue);
        });
    }

    private Object getValue(final Map<String, Object> config, final Queue<String> key) {
        if (config == null) return null;

        if (key.isEmpty()) throw new IllegalArgumentException();

        return key.size() == 1
                ? config.get(key.poll())
                : this.getValue((Map<String, Object>) config.get(key.poll()), key);
    }
}
