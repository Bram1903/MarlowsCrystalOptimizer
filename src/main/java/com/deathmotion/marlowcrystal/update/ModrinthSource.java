package com.deathmotion.marlowcrystal.update;

import com.deathmotion.marlowcrystal.versioning.MCOVersion;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public final class ModrinthSource implements UpdateSourceClient {

    private static final String PROJECT_ID = "ozpC8eDC";

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    @Override
    public UpdateResult check(MCOVersion currentVersion, String minecraftVersion) throws IOException, InterruptedException {
        URI uri = URI.create("https://api.modrinth.com/v2/project/" + PROJECT_ID + "/version"
                + "?loaders=" + encode("[\"fabric\"]")
                + "&game_versions=" + encode("[\"" + minecraftVersion + "\"]"));

        MCOVersion latest = null;
        JsonObject latestEntry = null;
        for (JsonElement element : JsonHttp.get(uri).getAsJsonArray()) {
            JsonObject entry = element.getAsJsonObject();
            Optional<MCOVersion> version = MCOVersion.parse(entry.get("version_number").getAsString());
            if (version.isPresent() && (latest == null || version.get().isNewerThan(latest))) {
                latest = version.get();
                latestEntry = entry;
            }
        }

        if (latestEntry == null) {
            return UpdateResult.none();
        }

        return new UpdateResult(
                latest.isNewerThan(currentVersion),
                latest,
                "https://modrinth.com/mod/" + PROJECT_ID + "/version/" + latestEntry.get("id").getAsString(),
                ReleaseChannel.fromName(latestEntry.get("version_type").getAsString())
        );
    }
}
