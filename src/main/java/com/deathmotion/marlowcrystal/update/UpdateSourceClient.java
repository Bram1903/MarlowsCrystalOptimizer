package com.deathmotion.marlowcrystal.update;

import com.deathmotion.marlowcrystal.versioning.MCOVersion;

import java.io.IOException;

public interface UpdateSourceClient {

    UpdateResult check(MCOVersion currentVersion, String minecraftVersion) throws IOException, InterruptedException;
}
