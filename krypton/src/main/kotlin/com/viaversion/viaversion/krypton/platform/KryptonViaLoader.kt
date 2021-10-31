package com.viaversion.viaversion.krypton.platform

import com.viaversion.viaversion.KryptonPlugin
import com.viaversion.viaversion.api.platform.ViaPlatformLoader
import com.viaversion.viaversion.krypton.listeners.UpdateListener

class KryptonViaLoader(private val plugin: KryptonPlugin) : ViaPlatformLoader {

    private val listeners = mutableSetOf<Any>()

    override fun load() {
        register(UpdateListener())
    }

    override fun unload() {
        listeners.forEach { plugin.server.eventManager.unregisterListener(plugin, it) }
        listeners.clear()
    }

    private fun register(listener: Any) {
        plugin.server.eventManager.register(plugin, storeListener(listener))
    }

    private fun <T : Any> storeListener(listener: T): T {
        listeners.add(listener)
        return listener
    }
}
