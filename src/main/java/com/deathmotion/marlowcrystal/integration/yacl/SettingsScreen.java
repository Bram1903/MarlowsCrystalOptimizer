//? if >=1.20.2 {
package com.deathmotion.marlowcrystal.integration.yacl;

import com.deathmotion.marlowcrystal.MarlowCrystal;
import com.deathmotion.marlowcrystal.config.Settings;
import com.deathmotion.marlowcrystal.config.UpdateSource;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class SettingsScreen {

    public static final String YACL_MOD_ID = "yet_another_config_lib_v3";

    private SettingsScreen() {
    }

    public static Screen create(Screen parent) {
        MarlowCrystal mod = MarlowCrystal.get();
        Settings settings = mod.settings();

        Option<Boolean> keepRender = Option.<Boolean>createBuilder()
                .name(Component.translatable("marlowcrystal.config.keep_render"))
                .description(OptionDescription.of(Component.translatable("marlowcrystal.config.keep_render.description")))
                .binding(false, settings::isKeepRender, settings::setKeepRender)
                .controller(TickBoxControllerBuilder::create)
                .build();

        Option<UpdateSource> updateSource = Option.<UpdateSource>createBuilder()
                .name(Component.translatable("marlowcrystal.config.update_source"))
                .description(OptionDescription.of(Component.translatable("marlowcrystal.config.update_source.description")))
                .binding(UpdateSource.MODRINTH, settings::getUpdateSource, settings::setUpdateSource)
                .controller(option -> EnumControllerBuilder.create(option)
                        .enumClass(UpdateSource.class)
                        .formatValue(source -> Component.literal(source.displayName())))
                .build();

        Option<Boolean> experimentalBuilds = Option.<Boolean>createBuilder()
                .name(Component.translatable("marlowcrystal.config.experimental_builds"))
                .description(OptionDescription.of(Component.translatable("marlowcrystal.config.experimental_builds.description")))
                .binding(false, settings::isExperimentalBuilds, settings::setExperimentalBuilds)
                .controller(option -> BooleanControllerBuilder.create(option).yesNoFormatter())
                .build();

        return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("marlowcrystal.config.title"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("marlowcrystal.config.category.crystals"))
                        .option(keepRender)
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("marlowcrystal.config.category.updates"))
                        .option(updateSource)
                        .option(experimentalBuilds)
                        .build())
                .save(mod::saveSettings)
                .build()
                .generateScreen(parent);
    }
}
//?}
