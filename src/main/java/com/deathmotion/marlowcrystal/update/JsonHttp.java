package com.deathmotion.marlowcrystal.update;

import com.deathmotion.marlowcrystal.versioning.MCOVersions;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

final class JsonHttp {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private static final String USER_AGENT = "Bram1903/MarlowsCrystalOptimizer/" + MCOVersions.CURRENT
            + " (+https://github.com/Bram1903/MarlowsCrystalOptimizer)";

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(TIMEOUT)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private JsonHttp() {
    }

    static JsonElement get(URI uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(TIMEOUT)
                .header("Accept", "application/json")
                .header("User-Agent", USER_AGENT)
                .GET()
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() != 200) {
            throw new IOException(uri.getHost() + " answered with HTTP " + response.statusCode());
        }
        return JsonParser.parseString(response.body());
    }
}
