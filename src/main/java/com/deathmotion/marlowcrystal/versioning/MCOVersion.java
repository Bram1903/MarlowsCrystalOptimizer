package com.deathmotion.marlowcrystal.versioning;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record MCOVersion(int major, int minor, int patch, boolean snapshot,
                         @Nullable String commit) implements Comparable<MCOVersion> {

    private static final Pattern VERSION_PATTERN = Pattern.compile("v?(\\d+)\\.(\\d+)(?:\\.(\\d+))?(-SNAPSHOT)?(?:\\+([0-9a-z.-]+))?", Pattern.CASE_INSENSITIVE);

    private static final Pattern COMMIT_PATTERN = Pattern.compile("[0-9a-f]{7,40}");

    public static @NotNull MCOVersion of(int major, int minor, int patch) {
        return new MCOVersion(major, minor, patch, false, null);
    }

    public static @NotNull Optional<MCOVersion> parse(@NotNull String version) {
        Matcher matcher = VERSION_PATTERN.matcher(version.trim());
        if (!matcher.matches()) return Optional.empty();

        String metadata = matcher.group(5);
        return Optional.of(new MCOVersion(
                Integer.parseInt(matcher.group(1)),
                Integer.parseInt(matcher.group(2)),
                matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0,
                matcher.group(4) != null,
                metadata != null && COMMIT_PATTERN.matcher(metadata).matches() ? metadata : null
        ));
    }

    @Override
    public int compareTo(@NotNull MCOVersion other) {
        int c = Integer.compare(major, other.major);
        if (c != 0) return c;
        c = Integer.compare(minor, other.minor);
        if (c != 0) return c;
        c = Integer.compare(patch, other.patch);
        if (c != 0) return c;
        return Boolean.compare(other.snapshot, snapshot);
    }

    public boolean isNewerThan(@NotNull MCOVersion other) {
        return compareTo(other) > 0;
    }

    @SuppressWarnings("unused") // counterpart of isNewerThan, kept so the comparison API is symmetric
    public boolean isOlderThan(@NotNull MCOVersion other) {
        return compareTo(other) < 0;
    }

    public @NotNull String toStringWithoutSnapshot() {
        return major + "." + minor + "." + patch;
    }

    public @NotNull String toDisplayString() {
        return snapshot ? toStringWithoutSnapshot() + "-SNAPSHOT" : toStringWithoutSnapshot();
    }

    @Override
    public @NotNull String toString() {
        return toDisplayString() + (commit != null ? "+" + commit : "");
    }
}
