package net.cryptic_game.backend.base.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.lang.annotation.Annotation;
import java.util.Collection;

@Slf4j
public final class ContextHandler {

    private final AnnotationConfigApplicationContext applicationContext;

    public <T> ContextHandler(final Class<T> clazz, final T instance, final Class<?>... beans) {
        this.applicationContext = new AnnotationConfigApplicationContext();
        this.applicationContext.registerBean(ContextHandler.class, () -> this);
        this.applicationContext.registerBean(clazz, () -> instance);
        if (beans.length != 0) this.applicationContext.register(beans);
    }

    public <T> T getBean(final Class<T> clazz) {
        return this.applicationContext.getBean(clazz);
    }

    public <T> Collection<T> getBeans(final Class<T> clazz) {
        return this.applicationContext.getBeansOfType(clazz).values();
    }

    public Collection<Object> getBeansWithAnnotation(final Class<? extends Annotation> annotation) {
        return this.applicationContext.getBeansWithAnnotation(annotation).values();
    }

    public void register(final Class<?>... classes) {
        if (classes.length != 0) this.applicationContext.register(classes);
    }

    public <T> void register(final Class<T> clazz, final T instance) {
        this.applicationContext.registerBean(clazz, () -> instance);
    }

    public boolean refresh() {
        try {
            this.applicationContext.refresh();
            return true;
        } catch (Throwable cause) {
            log.error("Unable to bootstrap modules.", cause);
            return false;
        }
    }

    public void setClassLoader(final ClassLoader loader) {
        this.applicationContext.setClassLoader(loader);
    }

    public void close() {
        this.applicationContext.close();
    }
}
