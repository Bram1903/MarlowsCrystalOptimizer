package com.deathmotion.marlowcrystal.update;

import com.deathmotion.marlowcrystal.versioning.MCOVersion;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class GitHubSource implements UpdateSourceClient {

    private static final URI LATEST_RELEASE = URI.create("https://api.github.com/repos/Bram1903/MarlowsCrystalOptimizer/releases/latest");

    private static final Pattern RELEASE_VERSION = Pattern.compile("\\d+(?:\\.\\d+)*");

    private static final Pattern ASSET_RANGE = Pattern.compile("\\+mc(\\d+(?:\\.\\d+)*)(?:-(\\d+(?:\\.\\d+)*))?\\.jar$");

    private static boolean supports(JsonArray assets, String minecraftVersion) {
        if (assets == null || !RELEASE_VERSION.matcher(minecraftVersion).matches()) {
            return true;
        }

        boolean ranged = false;
        for (JsonElement asset : assets) {
            Matcher matcher = ASSET_RANGE.matcher(asset.getAsJsonObject().get("name").getAsString());
            if (!matcher.find()) {
                continue;
            }

            ranged = true;
            String lower = matcher.group(1);
            String upper = matcher.group(2) != null ? matcher.group(2) : lower;
            if (compare(minecraftVersion, lower) >= 0
                    && (compare(minecraftVersion, upper) <= 0 || minecraftVersion.startsWith(upper + "."))) {
                return true;
            }
        }
        return !ranged;
    }

    private static int compare(String left, String right) {
        String[] a = left.split("\\.");
        String[] b = right.split("\\.");
        for (int i = 0; i < Math.max(a.length, b.length); i++) {
            int c = Integer.compare(i < a.length ? Integer.parseInt(a[i]) : 0, i < b.length ? Integer.parseInt(b[i]) : 0);
            if (c != 0) {
                return c;
            }
        }
        return 0;
    }

    @Override
    public UpdateResult check(MCOVersion currentVersion, String minecraftVersion) throws IOException, InterruptedException {
        JsonObject release = JsonHttp.get(LATEST_RELEASE).getAsJsonObject();

        Optional<MCOVersion> latest = MCOVersion.parse(release.get("tag_name").getAsString());
        if (latest.isEmpty() || !supports(release.getAsJsonArray("assets"), minecraftVersion)) {
            return UpdateResult.none();
        }

        return new UpdateResult(
                latest.get().isNewerThan(currentVersion),
                latest.get(),
                release.get("html_url").getAsString(),
                release.get("prerelease").getAsBoolean() ? ReleaseChannel.BETA : ReleaseChannel.RELEASE
        );
    }
}
