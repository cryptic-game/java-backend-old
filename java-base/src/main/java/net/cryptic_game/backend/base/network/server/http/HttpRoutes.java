package net.cryptic_game.backend.base.network.server.http;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.api.data.ApiAuthenticationProvider;
import net.cryptic_game.backend.base.network.server.http.route.HttpRoute;
import net.cryptic_game.backend.base.utils.HttpUtils;
import org.reactivestreams.Publisher;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;

public class HttpRoutes {

    private final Set<Entry> routes;
    private final ApiAuthenticationProvider authenticationProvider;

    public HttpRoutes(final ApiAuthenticationProvider authenticationProvider) {
        this.routes = new HashSet<>();
        this.authenticationProvider = authenticationProvider;
    }

    public void addRoute(final String path, final HttpRoute route) {
        this.addRoute(null, path, route);
    }

    public void addRoute(final HttpMethod method, final String path, final HttpRoute route) {
        this.routes.add(new Entry(method, path, route));
    }

    Handler toHandler() {
        return new Handler(this.routes, this.authenticationProvider);
    }

    @Data
    private static final class Entry {
        private final HttpMethod method;
        private final String path;
        private final HttpRoute route;
    }

    @Slf4j
    @RequiredArgsConstructor
    private static final class Handler implements BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> {

        private final Set<Entry> routes;
        private final ApiAuthenticationProvider authenticationProvider;

        @Override
        public Publisher<Void> apply(final HttpServerRequest request, final HttpServerResponse response) {
            for (final Entry entry : this.routes) {
                if ((entry.getMethod() == null || entry.getMethod().equals(request.method()))
                        && (request.path() + "/").startsWith(entry.getPath())) {
                    return this.executeRoute(request, response, entry.getRoute(), this.authenticationProvider);
                }
            }

            return HttpUtils.sendStatus(response, HttpResponseStatus.NOT_FOUND);
        }

        private Publisher<Void> executeRoute(final HttpServerRequest request, final HttpServerResponse response, final HttpRoute route, final ApiAuthenticationProvider authenticationProvider) {
            try {
                return route.execute(request, response, authenticationProvider);
            } catch (Throwable cause) {
                log.error("Error while handling a http request.", cause);
                return HttpUtils.sendStatus(response, HttpResponseStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
}
