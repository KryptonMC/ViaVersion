package com.viaversion.viaversion.krypton.commands

import com.viaversion.viaversion.KryptonPlugin
import com.viaversion.viaversion.api.command.ViaCommandSender
import org.kryptonmc.api.adventure.toLegacySectionText
import org.kryptonmc.api.command.Sender
import org.kryptonmc.api.entity.player.Player
import java.util.UUID

class KryptonCommandSender(private val sender: Sender) : ViaCommandSender {

    override fun getName(): String {
        if (sender is Player) return sender.profile.name
        return sender.name.toLegacySectionText()
    }

    override fun getUUID(): UUID {
        if (sender is Player) return sender.uuid
        return UUID.fromString(name)
    }

    override fun hasPermission(permission: String): Boolean = sender.hasPermission(permission)

    override fun sendMessage(msg: String) {
        sender.sendMessage(KryptonPlugin.COMPONENT_SERIALIZER.deserialize(msg))
    }
}
