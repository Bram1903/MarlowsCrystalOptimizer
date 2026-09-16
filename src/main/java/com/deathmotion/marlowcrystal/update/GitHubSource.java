package com.deathmotion.marlowcrystal.update;

import com.deathmotion.marlowcrystal.versioning.MCOVersion;
import com.deathmotion.marlowcrystal.versioning.MCOVersions;
import com.deathmotion.marlowcrystal.versioning.ModLoader;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class GitHubSource implements UpdateSourceClient {

    private static final URI RELEASES = URI.create("https://api.github.com/repos/Bram1903/MarlowsCrystalOptimizer/releases?per_page=100");

    private static final Pattern RELEASE_VERSION = Pattern.compile("\\d+(?:\\.\\d+)*");

    private static final Pattern ASSET_RANGE = Pattern.compile("\\+mc(\\d+(?:\\.\\d+)*)(?:-(\\d+(?:\\.\\d+)*))?(?:-([a-z]+))?\\.jar$");

    // Fabric jars kept the name they had before other loaders were supported.
    private static final String LOADER_SUFFIX = MCOVersions.LOADER == ModLoader.FABRIC ? null : MCOVersions.LOADER.id();

    private static boolean supports(JsonArray assets, String minecraftVersion) {
        boolean ranged = false;
        for (JsonElement asset : assets != null ? assets : new JsonArray()) {
            Matcher matcher = ASSET_RANGE.matcher(asset.getAsJsonObject().get("name").getAsString());
            if (!matcher.find()) {
                continue;
            }

            ranged = true;
            if (!Objects.equals(matcher.group(3), LOADER_SUFFIX)) {
                continue;
            }
            if (!RELEASE_VERSION.matcher(minecraftVersion).matches()) {
                return true;
            }

            String lower = matcher.group(1);
            String upper = matcher.group(2) != null ? matcher.group(2) : lower;
            if (compare(minecraftVersion, lower) >= 0
                    && (compare(minecraftVersion, upper) <= 0 || minecraftVersion.startsWith(upper + "."))) {
                return true;
            }
        }
        // Releases before 2.0.0 named their jars by hand, and they were all Fabric.
        return !ranged && MCOVersions.LOADER == ModLoader.FABRIC;
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
    public List<PublishedBuild> builds(String minecraftVersion) throws IOException, InterruptedException {
        List<PublishedBuild> builds = new ArrayList<>();
        for (JsonElement element : JsonHttp.get(RELEASES).getAsJsonArray()) {
            JsonObject release = element.getAsJsonObject();
            Optional<MCOVersion> version = MCOVersion.parse(release.get("tag_name").getAsString());
            if (version.isPresent() && supports(release.getAsJsonArray("assets"), minecraftVersion)) {
                builds.add(new PublishedBuild(
                        version.get(),
                        release.get("html_url").getAsString(),
                        release.get("prerelease").getAsBoolean() ? ReleaseChannel.BETA : ReleaseChannel.RELEASE
                ));
            }
        }
        return builds;
    }
}
