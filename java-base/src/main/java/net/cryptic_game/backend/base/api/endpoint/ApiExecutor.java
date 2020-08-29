package net.cryptic_game.backend.base.api.endpoint;

import com.google.gson.JsonObject;
import io.micrometer.core.instrument.Metrics;
import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.api.ApiException;
import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.json.JsonTypeMappingException;
import net.cryptic_game.backend.base.json.JsonUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class ApiExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(ApiExecutor.class);

    private ApiExecutor() {
        throw new UnsupportedOperationException();
    }

    public static ApiResponse execute(final Map<String, ApiEndpointData> endpoints, final String endpointName, final JsonObject data, final ApiClient client, final String tag) {
        final ApiEndpointData endpoint = endpoints.get(endpointName);
        if (endpoint == null) return new ApiResponse(ApiResponseType.NOT_FOUND, "ENDPOINT");
        else return executeMethod(endpoint, data, client, tag);
    }

    private static ApiResponse executeMethod(final ApiEndpointData methodData, final JsonObject data, final ApiClient client, final String tag) {
        try {
            final ApiParameterData sqlSessionParameter = methodData.getParameters().stream()
                    .filter(p -> p.getJavaType() != null && p.getJavaType().equals(Session.class)).findAny().orElse(null);
            final Session sqlSession = sqlSessionParameter == null ? null : AppBootstrap.getInstance().getSqlConnection().openSession();
            boolean hasSqlSession = sqlSession != null;
            boolean transactional = hasSqlSession && sqlSessionParameter.getSpecial().equals(ApiParameterSpecialType.SQL_SESSION_TRANSACTIONAL);
            try {
                if (transactional) sqlSession.beginTransaction();
                final long start = System.currentTimeMillis();
                try {
                    return (ApiResponse) methodData.getMethod().invoke(methodData.getObject(), getParameters(methodData.getParameters(),
                            methodData.isNormalParameters(),
                            data == null ? new JsonObject() : data,
                            client,
                            tag,
                            methodData,
                            sqlSession));
                } finally {
                    Metrics.timer(methodData.getName()).record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
                }
            } finally {
                if (hasSqlSession) {
                    if (transactional) {
                        sqlSession.getTransaction().commit();
                    }
                    sqlSession.close();
                }
            }
        } catch (ApiException e) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            LOG.error("Error while executing endpoint \"" + methodData.getName() + "\".", e);
            return new ApiResponse(ApiResponseType.INTERNAL_SERVER_ERROR);
        }
    }

    private static Object[] getParameters(
            final List<ApiParameterData> parameters,
            final boolean normalParameters,
            final JsonObject data,
            final ApiClient client,
            final String tag,
            final ApiEndpointData endpoint,
            final Session sqlSession
    ) throws ApiException {
        int size = 0;
        for (final ApiParameterData parameter : parameters) {
            if (parameter.getSpecial().equals(ApiParameterSpecialType.NORMAL) || parameter.getSpecial().equals(ApiParameterSpecialType.USER)) {
                if (normalParameters) size++;
            } else size++;
        }

        final Object[] objects = new Object[size];
        int current = 0;

        for (final ApiParameterData parameter : parameters) {
            switch (parameter.getSpecial()) {
                case NORMAL:
                    if (!parameter.isOptional() && (!data.has(parameter.getName()) || data.get(parameter.getName()).isJsonNull())) {
                        throw new ApiException("Parameter \"" + parameter.getName() + "\" is missing.");
                    } else if (normalParameters) {
                        try {
                            objects[current] = JsonUtils.fromJson(data.get(parameter.getName()), parameter.getJavaType());
                        } catch (JsonTypeMappingException e) {
                            throw new ApiException("Invalid format of parameter \"" + parameter.getName() + "\".", e);
                        }
                    }
                    break;
                case TAG:
                    objects[current] = tag;
                    break;
                case CLIENT:
                    objects[current] = client;
                    break;
                case ENDPOINT:
                    objects[current] = endpoint;
                    break;
                case DATA:
                    objects[current] = data;
                    break;
                case USER:
                    if (!parameter.isOptional() && (!data.has(parameter.getName()) || data.get(parameter.getName()).isJsonNull())) {
                        throw new ApiException("Parameter \"" + parameter.getName() + "\" is missing.");
                    } else if (normalParameters) {
                        try {
                            objects[current] = JsonUtils.fromJson(data.get(parameter.getName()), parameter.getJavaType());
                        } catch (JsonTypeMappingException e) {
                            throw new ApiException("Invalid format of parameter \"" + parameter.getName() + "\".", e);
                        }
                    }
                    break;
                case SQL_SESSION:
                case SQL_SESSION_TRANSACTIONAL:
                    objects[current] = sqlSession;
                    break;
                default:
                    throw new IllegalArgumentException(parameter.getSpecial().toString());
            }
            current++;
        }

        return objects;
    }
}
