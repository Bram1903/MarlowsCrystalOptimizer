package com.deathmotion.marlowcrystal.util;

import com.deathmotion.marlowcrystal.packet.impl.VersionPacket;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.deathmotion.marlowcrystal.MarlowCrystal.MOD_ID;

public class VersionUtil {

    private static final Pattern SEMVER_3_WITH_OPTIONAL_SNAPSHOT = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)(?:-SNAPSHOT)?$", Pattern.CASE_INSENSITIVE);

    public static String getModVersion() {
        return FabricLoader.getInstance()
                .getModContainer(MOD_ID)
                .map(ModContainer::getMetadata)
                .map(metadata -> metadata.getVersion().getFriendlyString())
                .orElse("unknown");
    }

    public static VersionPacket createVersionPacket() {
        String v = getModVersion();
        return parseToVersionPacket(v);
    }

    public static VersionPacket parseToVersionPacket(String versionString) {
        if (versionString == null) {
            return new VersionPacket(0, 0, 0, false);
        }

        String v = versionString.trim();
        boolean snapshot = v.toUpperCase(Locale.ROOT).endsWith("-SNAPSHOT");

        Matcher m = SEMVER_3_WITH_OPTIONAL_SNAPSHOT.matcher(v);
        if (!m.matches()) {
            return new VersionPacket(0, 0, 0, snapshot);
        }

        int major = Integer.parseInt(m.group(1));
        int minor = Integer.parseInt(m.group(2));
        int patch = Integer.parseInt(m.group(3));
        return new VersionPacket(major, minor, patch, snapshot);
    }
}
