package net.cryptic_game.backend.base.network.server.http;

import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.Cookie;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.netty.ByteBufFlux;
import reactor.netty.Connection;
import reactor.netty.http.server.HttpServerRequest;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

@RequiredArgsConstructor
final class CustomPathHttpServerRequest implements HttpServerRequest {

    private final String path;
    private final HttpServerRequest request;

    @Override
    public ByteBufFlux receive() {
        return this.request.receive();
    }

    @Override
    public Flux<?> receiveObject() {
        return this.request.receiveObject();
    }

    @Override
    public HttpServerRequest withConnection(final Consumer<? super Connection> withConnection) {
        return this.request.withConnection(withConnection);
    }

    @Override
    public String param(final CharSequence key) {
        return this.request.param(key);
    }

    @Override
    public Map<String, String> params() {
        return this.request.params();
    }

    @Override
    public HttpServerRequest paramsResolver(final Function<? super String, Map<String, String>> paramsResolver) {
        return this.request.paramsResolver(paramsResolver);
    }

    @Override
    public Flux<HttpContent> receiveContent() {
        return this.request.receiveContent();
    }

    @Override
    public InetSocketAddress hostAddress() {
        return this.request.hostAddress();
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return this.request.remoteAddress();
    }

    @Override
    public HttpHeaders requestHeaders() {
        return this.request.requestHeaders();
    }

    @Override
    public String scheme() {
        return this.request.scheme();
    }

    @Override
    public Map<CharSequence, Set<Cookie>> cookies() {
        return this.request.cookies();
    }

    @Override
    public boolean isKeepAlive() {
        return this.request.isKeepAlive();
    }

    @Override
    public boolean isWebsocket() {
        return this.request.isWebsocket();
    }

    @Override
    public HttpMethod method() {
        return this.request.method();
    }

    @Override
    public String path() {
        String path = fullPath();
        if (!path.isEmpty()) {
            if (path.charAt(0) == '/') {
                path = path.substring(1);
                if (path.isEmpty()) {
                    return path;
                }
            }
            if (path.charAt(path.length() - 1) == '/') {
                return path.substring(0, path.length() - 1);
            }
        }
        return path;
    }

    @Override
    public String fullPath() {
        return this.path;
    }

    @Override
    public String requestId() {
        return this.request.requestId();
    }

    @Override
    public String uri() {
        return this.request.uri();
    }

    @Override
    public HttpVersion version() {
        return this.request.version();
    }

    @Override
    public Map<CharSequence, List<Cookie>> allCookies() {
        return this.request.allCookies();
    }
}
