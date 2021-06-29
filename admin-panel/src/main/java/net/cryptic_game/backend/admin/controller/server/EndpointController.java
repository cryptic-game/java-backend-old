package net.cryptic_game.backend.admin.controller.server;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.cryptic_game.backend.dto.server.ServerEndpoint;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "Endpoint Management")
@RequestMapping("server/endpoints")
public interface EndpointController {

    @GetMapping
    Flux<ServerEndpoint> findAll();

    @PostMapping("{id}")
    Mono<ServerEndpoint> disableEndpoint(@PathVariable("id") String id);

    @DeleteMapping("{id}")
    Mono<ServerEndpoint> enableEndpoint(@PathVariable("id") String id);
}
