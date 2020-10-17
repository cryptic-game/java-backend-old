package net.cryptic_game.backend.server.server.websocket;

import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.data.redis.repositories.SessionRepository;
import net.cryptic_game.backend.data.sql.repositories.user.UserRepository;

@RequiredArgsConstructor
@ApiEndpointCollection(id = "user", apiType = ApiType.WEBSOCKET)
public final class WebSocketUserEndpoints {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

//    TODO
//    @ApiEndpoint(id="login")
//    public ApiResponse login(@ApiParameter(id= "client", type = ApiParameterType.CLIENT) final ApiClient client,
//                             @ApiParameter(id="username") final String username,
//                             @ApiParameter(id="password") final String password) {
//        Session session = client.get(Session.class);
//        if (session != null) {
//            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "ALREADY_LOGGED_IN");
//        }
//
//        if (!Constants.USERNAME.matcher(username).matches()) {
//            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "INVALID_USERNAME");
//        }
//
//        final User user = this.userRepository.findByUsername(username).orElse(null);
//
//        if (user == null) {
//            return new ApiResponse(HttpResponseStatus.UNAUTHORIZED, "INVALID_USERNAME");
//        }
//        if (!user.verifyPassword(password)) {
//            return new ApiResponse(HttpResponseStatus.UNAUTHORIZED, "INVALID_PASSWORD");
//        }
//
//        session = sessionRepository.createSession(user);
//
//        client.add(session);
//
//        return new ApiResponse(HttpResponseStatus.OK, session);
//    }
//
//    @ApiEndpoint(id="register")
//    public ApiResponse register(@ApiParameter(id="client", type = ApiParameterType.CLIENT) final ApiClient client,
//                                @ApiParameter(id="username") final String username,
//                                @ApiParameter(id="password") final String password) {
//        Session session = client.get(Session.class);
//        if (session != null) {
//            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "ALREADY_LOGGED_IN");
//        }
//
//        if (!Constants.USERNAME.matcher(username).matches()) {
//            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "INVALID_USERNAME");
//        }
//
//        if (!ValidationUtils.checkPassword(password)) {
//            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "INVALID_PASSWORD");
//        }
//
//        if (this.userRepository.findByUsername(username).isPresent()) {
//            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "USER_ALREADY_EXISTS");
//        }
//
//        final User user = this.userRepository.createUser(username, password);
//        session = this.sessionRepository.createSession(user);
//        client.add(session);
//
//        return new ApiResponse(HttpResponseStatus.OK, session);
//
//    }
//
//    @ApiEndpoint(id="session")
//    public ApiResponse session(@ApiParameter(id= "client", type = ApiParameterType.CLIENT) final ApiClient client,
//                               @ApiParameter(id="session") final UUID sessionId,
//                               @ApiParameter(id="user_id") final UUID userId) {
//        Session session = client.get(Session.class);
//        if (session != null) {
//            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "ALREADY_LOGGED_IN");
//        }
//
//        final User user = this.userRepository.findById(userId).orElse(null);
//        if (user == null) {
//            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "USER_NOT_FOUND");
//        }
//
//
//        session = this.sessionRepository.findById(sessionId).orElse(null);
//        if (session == null || !session.getUserId().equals(userId)) {
//            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "INVALID_SESSION");
//        }
//
//        this.sessionRepository.save(session);
//
//        client.add(session);
//        final OffsetDateTime now = OffsetDateTime.now();
//        user.setLast(now);
//        this.userRepository.save(user);
//
//        return new ApiResponse(HttpResponseStatus.OK, session);
//    }
//
//    @ApiEndpoint(id="change_password")
//    public ApiResponse changePassword(@ApiParameter(id= "client", type = ApiParameterType.CLIENT) final ApiClient client,
//                                      @ApiParameter(id="password") final String password,
//                                      @ApiParameter(id="new") final String newPassword) {
//        Session session = client.get(Session.class);
//        if (session == null) {
//            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "NOT_LOGGED_IN");
//        }
//
//        if (!ValidationUtils.checkPassword(newPassword)) {
//            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "INVALID_PASSWORD");
//        }
//
//        final User user = this.userRepository.findById(session.getUserId()).orElseThrow();
//
//        if (!user.verifyPassword(password)) {
//            return new ApiResponse(HttpResponseStatus.UNAUTHORIZED, "INVALID_PASSWORD");
//        }
//
//        user.setPassword(newPassword);
//        this.userRepository.save(user);
//
//        return new ApiResponse(HttpResponseStatus.OK);
//    }
//
//    @ApiEndpoint(id="logout")
//    public ApiResponse logout(@ApiParameter(id= "client", type = ApiParameterType.CLIENT) final ApiClient client,
//                              @ApiParameter(id= "session", required = false) final UUID sessionId) {
//        Session session = client.get(Session.class);
//        if (session == null) {
//            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "NOT_LOGGED_IN");
//        }
//
//        if (sessionId == null) {
//            this.sessionRepository.delete(session);
//            client.remove(Session.class);
//            return new ApiResponse(HttpResponseStatus.OK);
//        }
//
//        session = this.sessionRepository.findById(sessionId).orElse(null);
//
//        if (session == null) {
//            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "SESSION_NOT_FOUND");
//        }
//
//        this.sessionRepository.delete(session);
//        return new ApiResponse(HttpResponseStatus.OK);
//    }
//
//    @ApiEndpoint(id="delete")
//    public ApiResponse delete(@ApiParameter(id="client", type = ApiParameterType.CLIENT) final ApiClient client,
//                              @ApiParameter(id="password") final String password) {
//        Session session = client.get(Session.class);
//        if (session == null) {
//            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "NOT_LOGGED_IN");
//        }
//
//        final User user = this.userRepository.findById(session.getUserId()).orElseThrow();
//
//        if (!user.verifyPassword(password)) {
//            return new ApiResponse(HttpResponseStatus.UNAUTHORIZED, "INVALID_PASSWORD");
//        }
//
//        client.remove(Session.class);
//        this.userRepository.delete(user);
//
//        return new ApiResponse(HttpResponseStatus.OK);
//    }
//
//    @ApiEndpoint(id="get")
//    public ApiResponse get(@ApiParameter(id= "client", type = ApiParameterType.CLIENT) final ApiClient client,
//                           @ApiParameter(id="id") final UUID userId) {
//        final Session session = client.get(Session.class);
//        if (session == null) {
//            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "NOT_LOGGED_IN");
//        }
//
//        final User user = this.userRepository.findById(userId).orElse(null);
//
//        if (user == null) {
//            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "USER_NOT_FOUND");
//        }
//
//        return new ApiResponse(HttpResponseStatus.OK, user.serializePublic());
//    }
}
