package com.deathmotion.marlowcrystal.update;

import java.io.IOException;
import java.util.List;

public interface BuildSource {

    List<PublishedBuild> builds(String minecraftVersion) throws IOException, InterruptedException;
}
