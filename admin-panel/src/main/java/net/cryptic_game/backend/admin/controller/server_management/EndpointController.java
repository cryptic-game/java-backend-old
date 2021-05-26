package net.cryptic_game.backend.admin.controller.server_management;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.cryptic_game.backend.admin.dto.server_management.DisabledEndpoint;
import net.cryptic_game.backend.admin.dto.server_management.Endpoint;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import java.util.Set;

@Tag(name = "Endpoint Management")
@RequestMapping("server_management/disabled_endpoints")
public interface EndpointController {

    @GetMapping(value = "all_server_endpoints", produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<String> findAll();

    @GetMapping("")
    Set<DisabledEndpoint> findAllDisabledEndpoints();

    @GetMapping("{path}")
    Mono<Endpoint> getInfo(@PathVariable("path") String path);

    @DeleteMapping("{path}")
    Mono<Endpoint> enableEndpoint(@PathVariable("path") String path);

    @PutMapping("{path}")
    Mono<Endpoint> disableEndpoint(@PathVariable("path") String path, @RequestBody DisabledEndpoint disabledEndpoint);

    @PostMapping("{path}")
    Mono<Endpoint> edit(@PathVariable("path") String path, @RequestBody DisabledEndpoint disabledEndpoint);

}
