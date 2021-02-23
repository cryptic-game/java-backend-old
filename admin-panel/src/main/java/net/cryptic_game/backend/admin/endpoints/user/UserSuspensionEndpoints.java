package net.cryptic_game.backend.admin.endpoints.user;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.admin.authentication.AdminPanelAuthenticator;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiParameterType;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.base.api.handler.rest.RestApiRequest;
import net.cryptic_game.backend.base.json.JsonUtils;
import net.cryptic_game.backend.data.Permission;
import net.cryptic_game.backend.data.redis.repositories.SessionRepository;
import net.cryptic_game.backend.data.sql.entities.user.User;
import net.cryptic_game.backend.data.sql.entities.user.UserSuspension;
import net.cryptic_game.backend.data.sql.entities.user.UserSuspensionRevoked;
import net.cryptic_game.backend.data.sql.repositories.user.AdminUserRepository;
import net.cryptic_game.backend.data.sql.repositories.user.UserRepository;
import net.cryptic_game.backend.data.sql.repositories.user.UserSuspensionRepository;
import net.cryptic_game.backend.data.sql.repositories.user.UserSuspensionRevokedRepository;
import org.springframework.data.domain.PageRequest;

import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@ApiEndpointCollection(id = "user/suspension", description = "manages user suspensions", type = ApiType.REST, authenticator = AdminPanelAuthenticator.class)
public class UserSuspensionEndpoints {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final AdminUserRepository adminUserRepository;
    private final UserSuspensionRepository userSuspensionRepository;
    private final UserSuspensionRevokedRepository userSuspensionRevokedRepository;

    @ApiEndpoint(id = "list", authentication = Permission.USER_MANAGEMENT)
    public ApiResponse list(@ApiParameter(id = "user_id", required = false) final UUID userId,
                            @ApiParameter(id = "page") final int page,
                            @ApiParameter(id = "page_size") final int pageSize) {
        if (userId == null) return new ApiResponse(HttpResponseStatus.OK, this.userSuspensionRepository.findAll(PageRequest.of(page, pageSize)));

        final Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) return new ApiResponse(HttpResponseStatus.NOT_FOUND, "USER");
        return new ApiResponse(HttpResponseStatus.OK, this.userSuspensionRepository.findAllByUserId(userId, PageRequest.of(page, pageSize)));
    }

    @ApiEndpoint(id = "create", authentication = Permission.USER_MANAGEMENT)
    public ApiResponse create(@ApiParameter(id = "request", type = ApiParameterType.REQUEST) final RestApiRequest request,
                              @ApiParameter(id = "user_id") final UUID userId,
                              @ApiParameter(id = "expires") final OffsetDateTime expires,
                              @ApiParameter(id = "reason") final String reason) {
        final Optional<User> user = this.userRepository.findById(userId);
        final OffsetDateTime now = OffsetDateTime.now();

        if (!expires.isAfter(now)) return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "EXPIRE_NOT_IN_FUTURE");
        if (user.isEmpty()) return new ApiResponse(HttpResponseStatus.NOT_FOUND, "USER");

        long adminUserId = JsonUtils.fromJson(JsonUtils.fromJson(JsonParser.parseString(
                new String(Base64.getDecoder().decode(request.getContext().getHttpRequest()
                        .requestHeaders().get(HttpHeaderNames.AUTHORIZATION).split(Pattern.quote("."))[1])))
                , JsonObject.class).get("user_id"), long.class);

        final UserSuspension suspension = new UserSuspension(user.get(), this.adminUserRepository.findById(adminUserId).orElseThrow(), now, expires, reason);
        StreamSupport.stream(this.sessionRepository.findAll().spliterator(), true)
                .filter(Objects::nonNull)
                .filter(session -> session.getUserId().equals(userId))
                .forEach(this.sessionRepository::delete);

        return new ApiResponse(HttpResponseStatus.OK, this.userSuspensionRepository.save(suspension));
    }

    @ApiEndpoint(id = "revoke", authentication = Permission.USER_MANAGEMENT)
    public ApiResponse revoke(@ApiParameter(id = "request", type = ApiParameterType.REQUEST) final RestApiRequest request,
                              @ApiParameter(id = "id") final UUID id,
                              @ApiParameter(id = "reason") final String reason) {
        final UserSuspension suspension = this.userSuspensionRepository.findById(id).orElse(null);
        final OffsetDateTime now = OffsetDateTime.now();

        if (suspension == null) return new ApiResponse(HttpResponseStatus.NOT_FOUND, "USER_SUSPENSION");
        if (!suspension.getExpires().isAfter(now)) return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "EXPIRE_NOT_IN_FUTURE");

        long adminUserId = JsonUtils.fromJson(JsonUtils.fromJson(JsonParser.parseString(
                new String(Base64.getDecoder().decode(request.getContext().getHttpRequest()
                        .requestHeaders().get(HttpHeaderNames.AUTHORIZATION).split(Pattern.quote("."))[1])))
                , JsonObject.class).get("user_id"), long.class);

        return new ApiResponse(HttpResponseStatus.OK, this.userSuspensionRevokedRepository.save(
                new UserSuspensionRevoked(suspension, this.adminUserRepository.findById(adminUserId).orElseThrow(), now, reason)
        ));
    }
}
