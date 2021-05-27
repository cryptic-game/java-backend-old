package net.cryptic_game.backend.admin.controller.server_management;

import com.nimbusds.jose.shaded.json.JSONObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.cryptic_game.backend.admin.dto.server_management.DisabledEndpoint;
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

    @GetMapping("all_server_endpoints")
    Mono<JSONObject> findAll();

    @GetMapping("")
    Set<DisabledEndpoint> findAllDisabledEndpoints();

    @GetMapping("{path}")
    Mono<JSONObject> getInfo(@PathVariable("path") String path);

    @DeleteMapping("{path}")
    Mono<JSONObject> enableEndpoint(@PathVariable("path") String path);

    @PutMapping("{path}")
    Mono<JSONObject> disableEndpoint(@PathVariable("path") String path, @RequestBody DisabledEndpoint disabledEndpoint);

    @PostMapping("{path}")
    Mono<JSONObject> edit(@PathVariable("path") String path, @RequestBody DisabledEndpoint disabledEndpoint);

}
