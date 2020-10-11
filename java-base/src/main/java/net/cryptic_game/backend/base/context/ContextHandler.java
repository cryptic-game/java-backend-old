package net.cryptic_game.backend.base.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.lang.annotation.Annotation;
import java.util.Map;

@Slf4j
public final class ContextHandler {

    private final AnnotationConfigApplicationContext applicationContext;

    public <T> ContextHandler(final Class<T> clazz, final T instance, final Class<?>... beans) {
        this.applicationContext = new AnnotationConfigApplicationContext();
        this.applicationContext.registerBean(clazz, () -> instance);
        if (beans.length != 0) this.applicationContext.register(beans);
    }

    public <T> T getBean(final Class<T> clazz) {
        return this.applicationContext.getBean(clazz);
    }

    public Map<String, Object> getBeansWithAnnotation(final Class<? extends Annotation> annotationType) throws BeansException {
        return this.applicationContext.getBeansWithAnnotation(annotationType);
    }

    public void register(final Class<?>... classes) {
        if (classes.length != 0) this.applicationContext.register(classes);
    }

    public boolean refresh() {
        try {
            this.applicationContext.refresh();
            return true;
        } catch (Throwable cause) {
            log.error("Unable to bootstrap application context.", cause);
            return false;
        }
    }

    public void close() {
        this.applicationContext.close();
    }
}
