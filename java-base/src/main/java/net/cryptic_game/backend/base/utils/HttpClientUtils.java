package net.cryptic_game.backend.base.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import net.cryptic_game.backend.base.json.JsonUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.net.URL;

public final class HttpClientUtils {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static final OkHttpClient CLIENT = new OkHttpClient();

    private HttpClientUtils() {
        throw new UnsupportedOperationException();
    }

    public static ApiResponse sendRequest(final URL url, final JsonElement json) throws IOException {
        try (Response response = sendReqeust(new Request.Builder().url(url)
                .post(RequestBody.create(json.toString(), JSON)).build())) {
            final ResponseBody responseBody = response.body();
            if (responseBody == null) return new ApiResponse(ApiResponseType.getByCode(response.code()));

            final JsonObject jsonBody = JsonUtils.fromJson(JsonParser.parseString(responseBody.string()), JsonObject.class);
            final JsonObject info = JsonUtils.fromJson(jsonBody.get("info"), JsonObject.class);

            return new ApiResponse(
                    ApiResponseType.getByCode(response.code()),
                    info == null ? null : JsonUtils.fromJson(info.get("message"), String.class),
                    JsonUtils.fromJson(jsonBody.get("data"), JsonElement.class)
            );
        }
    }

    public static Response sendReqeust(final Request request) throws IOException {
        return CLIENT.newCall(request).execute();
    }
}
