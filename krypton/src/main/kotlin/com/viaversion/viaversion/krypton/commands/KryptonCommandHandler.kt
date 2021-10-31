package com.viaversion.viaversion.krypton.commands

import com.viaversion.viaversion.commands.ViaCommandHandler
import org.kryptonmc.api.command.Sender
import org.kryptonmc.api.command.SimpleCommand

class KryptonCommandHandler : ViaCommandHandler(), SimpleCommand {

    override fun execute(sender: Sender, args: Array<String>) {
        onCommand(KryptonCommandSender(sender), args)
    }

    override fun suggest(sender: Sender, args: Array<String>): List<String> = onTabComplete(KryptonCommandSender(sender), args)
}
