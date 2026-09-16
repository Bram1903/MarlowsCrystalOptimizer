//? if neoforge {
/*package com.deathmotion.marlowcrystal.loader.neoforge;

import com.deathmotion.marlowcrystal.MarlowCrystal;
import com.deathmotion.marlowcrystal.loader.LoaderAccess;
import com.deathmotion.marlowcrystal.update.ReleaseChannel;
import com.deathmotion.marlowcrystal.update.UpdateResult;
import com.deathmotion.marlowcrystal.util.Logger;
import net.neoforged.fml.ModList;
import net.neoforged.fml.VersionChecker;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforgespi.language.IModInfo;
import org.apache.maven.artifact.versioning.ComparableVersion;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

public final class NeoForgeLoaderAccess implements LoaderAccess {

    private static final Logger LOGGER = new Logger();

    private static VersionChecker.CheckResult checkResult(UpdateResult result) {
        if (!result.available() || result.latestVersion() == null || result.downloadUrl() == null) {
            return new VersionChecker.CheckResult(VersionChecker.Status.UP_TO_DATE, null, Map.of(), null);
        }

        VersionChecker.Status status = result.channel() == ReleaseChannel.RELEASE
                ? VersionChecker.Status.OUTDATED
                : VersionChecker.Status.BETA_OUTDATED;
        return new VersionChecker.CheckResult(status, new ComparableVersion(result.latestVersion().toDisplayString()),
                Map.of(), result.downloadUrl());
    }

    @Override
    public Path configDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public Optional<String> minecraftVersion() {
        return ModList.get()
                .getModContainerById("minecraft")
                .map(container -> container.getModInfo().getVersion().toString());
    }

    // NeoForge only fills its mod list from the update JSON a mod declares, which cannot follow the update source or
    // experimental builds settings. Its results table has been the same private field from NeoForge 20.4 to 26.3.
    @Override
    @SuppressWarnings("unchecked")
    public void showUpdate(UpdateResult result) {
        try {
            Field field = VersionChecker.class.getDeclaredField("results");
            field.setAccessible(true);
            Map<IModInfo, VersionChecker.CheckResult> results = (Map<IModInfo, VersionChecker.CheckResult>) field.get(null);
            ModList.get().getModContainerById(MarlowCrystal.MOD_ID)
                    .ifPresent(container -> results.put(container.getModInfo(), checkResult(result)));
        } catch (ReflectiveOperationException | RuntimeException e) {
            LOGGER.warn("Could not show the update check in the mod list: " + e.getMessage());
        }
    }
}
*///?}
