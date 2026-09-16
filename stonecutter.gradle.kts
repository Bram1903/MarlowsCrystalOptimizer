plugins {
    id("dev.kikugie.stonecutter")
    id("marlowcrystal.release")
}

stonecutter active "26.1-fabric"

stonecutter parameters {
    val (version, loader) = current.project.split('-', limit = 2)

    properties {
        tags(version, loader)
    }

    constants {
        match(loader, "fabric", "neoforge")
    }

    replacements {
        string(current.parsed >= "1.21.5") {
            replace("MobEffects.DAMAGE_BOOST", "MobEffects.STRENGTH")
        }
        string(current.parsed >= "1.21.11") {
            replace("ResourceLocation", "Identifier")
        }
        string(current.parsed >= "26.1") {
            replace("PayloadTypeRegistry.configurationS2C()", "PayloadTypeRegistry.clientboundConfiguration()")
        }
        string(current.parsed >= "26.1") {
            replace("PayloadTypeRegistry.playS2C()", "PayloadTypeRegistry.clientboundPlay()")
        }
        string(current.parsed >= "26.1") {
            replace("PayloadTypeRegistry.playC2S()", "PayloadTypeRegistry.serverboundPlay()")
        }
    }
}
