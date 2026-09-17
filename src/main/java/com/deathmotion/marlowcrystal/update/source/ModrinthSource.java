package com.deathmotion.marlowcrystal.update.source;

import com.deathmotion.marlowcrystal.update.BuildSource;
import com.deathmotion.marlowcrystal.update.PublishedBuild;
import com.deathmotion.marlowcrystal.update.ReleaseChannel;
import com.deathmotion.marlowcrystal.version.CurrentBuild;
import com.deathmotion.marlowcrystal.version.ModVersion;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class ModrinthSource implements BuildSource {

    private static final String PROJECT_ID = "ozpC8eDC";

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    @Override
    public List<PublishedBuild> builds(String minecraftVersion) throws IOException, InterruptedException {
        URI uri = URI.create("https://api.modrinth.com/v2/project/" + PROJECT_ID + "/version"
                + "?loaders=" + encode("[\"" + CurrentBuild.LOADER.id() + "\"]")
                + "&game_versions=" + encode("[\"" + minecraftVersion + "\"]"));

        List<PublishedBuild> builds = new ArrayList<>();
        for (JsonElement element : JsonHttp.get(uri).getAsJsonArray()) {
            JsonObject entry = element.getAsJsonObject();
            ModVersion.parse(entry.get("version_number").getAsString()).ifPresent(version -> builds.add(new PublishedBuild(
                    version,
                    "https://modrinth.com/mod/" + PROJECT_ID + "/version/" + entry.get("id").getAsString(),
                    ReleaseChannel.fromName(entry.get("version_type").getAsString())
            )));
        }
        return builds;
    }
}
