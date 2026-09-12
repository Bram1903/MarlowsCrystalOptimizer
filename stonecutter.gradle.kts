plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "26.1"

stonecutter parameters {
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

