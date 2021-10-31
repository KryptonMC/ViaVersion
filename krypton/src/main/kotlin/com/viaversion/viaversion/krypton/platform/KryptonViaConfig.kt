package com.viaversion.viaversion.krypton.platform

import com.viaversion.viaversion.configuration.AbstractViaConfig
import java.io.File
import java.net.URL

class KryptonViaConfig(configFile: File) : AbstractViaConfig(configFile) {

    init {
        reloadConfig()
    }

    override fun getDefaultConfigURL(): URL? = javaClass.classLoader.getResource("assets/viaversion/config.yml")

    override fun handleConfig(config: MutableMap<String, Any>?) {}

    override fun getUnsupportedOptions() = UNSUPPORTED

    companion object {

        private val UNSUPPORTED = listOf(
            "anti-xray-patch", "bungee-ping-interval", "bungee-ping-save", "bungee-servers", "velocity-ping-interval",
            "velocity-ping-save", "velocity-servers", "quick-move-action-fix", "change-1_9-hitbox", "change-1_14-hitbox",
            "blockconnection-method"
        )
    }
}
