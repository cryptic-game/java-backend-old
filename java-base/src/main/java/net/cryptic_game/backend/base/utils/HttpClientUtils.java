package net.cryptic_game.backend.base.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiResponseStatus;
import net.cryptic_game.backend.base.json.JsonUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
public final class HttpClientUtils {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static final OkHttpClient CLIENT = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build();

    private HttpClientUtils() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public static ApiResponse sendRequest(@NotNull final String url) throws IOException {
        try (Response response = sendReqeust(new Request.Builder().url(url).build())) {
            return getApiResponse(response);
        }
    }

    @NotNull
    public static ApiResponse sendRequest(@NotNull final String url, @NotNull final JsonElement json) throws IOException {
        try (Response response = sendReqeust(new Request.Builder().url(url)
                .post(RequestBody.create(json.toString(), JSON)).build())) {
            return getApiResponse(response);
        }
    }

    @NotNull
    public static Response sendReqeust(@NotNull final Request request) throws IOException {
        return CLIENT.newCall(request).execute();
    }

    public static void sendAsyncRequest(@NotNull final String url, @NotNull final JsonElement json) {
        sendAsyncRequest(url, json, new ApiCallback() {
            @Override
            public void onFailure(@NotNull final IOException e) {
            }

            @Override
            public void onResponse(@NotNull final ApiResponse response) {
            }
        });
    }

    public static void sendAsyncRequest(@NotNull final String url, @NotNull final ApiCallback callback) {
        sendAsyncRequest(new Request.Builder().url(url).build(), callback);
    }

    public static void sendAsyncRequest(@NotNull final String url, @NotNull final JsonElement json, @NotNull final ApiCallback callback) {
        sendAsyncRequest(new Request.Builder().url(url)
                .post(RequestBody.create(json.toString(), JSON)).build(), callback);
    }

    public static void sendAsyncRequest(@NotNull final Request request, @NotNull final ApiCallback callback) {
        CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull final Call call, @NotNull final IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NotNull final Call call, @NotNull final Response response) throws IOException {
                callback.onResponse(getApiResponse(response));
            }
        });
    }

    @NotNull
    public static ApiResponse getApiResponse(@NotNull final Response response) throws IOException {
        final ResponseBody responseBody = response.body();
        final ApiResponseStatus code = ApiResponseStatus.getByCode(response.code());
        if (code == null) {
            log.error("Status code {} could not be found.", response.code());
            return new ApiResponse(ApiResponseStatus.INTERNAL_SERVER_ERROR);
        }
        if (responseBody == null) return new ApiResponse(code);

        final JsonObject jsonBody = JsonUtils.fromJson(JsonParser.parseString(responseBody.string()), JsonObject.class);
        final JsonObject info = JsonUtils.fromJson(jsonBody.get("info"), JsonObject.class);

        return new ApiResponse(
                code,
                info == null ? JsonUtils.fromJson(jsonBody.get("data"), JsonElement.class) : JsonUtils.fromJson(info.get("message"), String.class)
        );
    }

    public interface ApiCallback {

        void onFailure(@NotNull IOException e);

        void onResponse(@NotNull ApiResponse response);
    }
}
