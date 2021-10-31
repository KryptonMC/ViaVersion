package com.viaversion.viaversion.krypton.platform

import com.viaversion.viaversion.ViaAPIBase
import io.netty.buffer.ByteBuf
import org.kryptonmc.api.entity.player.Player

class KryptonViaAPI : ViaAPIBase<Player>() {

    override fun getPlayerVersion(player: Player): Int = getPlayerVersion(player.uuid)

    override fun sendRawPacket(player: Player, packet: ByteBuf) {
        sendRawPacket(player.uuid, packet)
    }
}
