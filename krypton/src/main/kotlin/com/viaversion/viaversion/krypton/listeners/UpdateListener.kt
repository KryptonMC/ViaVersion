package com.viaversion.viaversion.krypton.listeners

import com.viaversion.viaversion.api.Via
import com.viaversion.viaversion.update.UpdateUtil
import org.kryptonmc.api.event.Listener
import org.kryptonmc.api.event.player.JoinEvent

class UpdateListener {

    @Listener
    fun onJoin(event: JoinEvent) {
        if (event.player.hasPermission("viaversion.update") && Via.getConfig().isCheckForUpdates) {
            UpdateUtil.sendUpdateMessage(event.player.uuid)
        }
    }
}
