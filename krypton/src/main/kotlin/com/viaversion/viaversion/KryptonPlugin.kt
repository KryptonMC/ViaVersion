package com.viaversion.viaversion

import com.google.gson.JsonObject
import com.google.inject.Inject
import com.viaversion.viaversion.api.Via
import com.viaversion.viaversion.api.ViaAPI
import com.viaversion.viaversion.api.command.ViaCommandSender
import com.viaversion.viaversion.api.configuration.ConfigurationProvider
import com.viaversion.viaversion.api.configuration.ViaVersionConfig
import com.viaversion.viaversion.api.data.MappingDataLoader
import com.viaversion.viaversion.api.platform.PlatformTask
import com.viaversion.viaversion.api.platform.ViaPlatform
import com.viaversion.viaversion.dump.PluginInfo
import com.viaversion.viaversion.krypton.commands.KryptonCommandHandler
import com.viaversion.viaversion.krypton.commands.KryptonCommandSender
import com.viaversion.viaversion.krypton.platform.KryptonViaAPI
import com.viaversion.viaversion.krypton.platform.KryptonViaConfig
import com.viaversion.viaversion.krypton.platform.KryptonViaInjector
import com.viaversion.viaversion.krypton.platform.KryptonViaLoader
import com.viaversion.viaversion.krypton.platform.KryptonViaTask
import com.viaversion.viaversion.krypton.util.LoggerWrapper
import com.viaversion.viaversion.util.ChatColorUtil
import com.viaversion.viaversion.util.GsonUtil
import com.viaversion.viaversion.util.VersionInfo
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.apache.logging.log4j.Logger
import org.kryptonmc.api.Server
import org.kryptonmc.api.command.meta.simpleCommandMeta
import org.kryptonmc.api.entity.player.Player
import org.kryptonmc.api.event.Listener
import org.kryptonmc.api.event.server.ServerStartEvent
import org.kryptonmc.api.event.server.ServerStopEvent
import org.kryptonmc.api.plugin.annotation.DataFolder
import org.kryptonmc.api.plugin.annotation.Plugin
import org.kryptonmc.api.scheduling.Scheduler
import java.io.File
import java.nio.file.Path
import java.util.UUID
import java.util.concurrent.TimeUnit

@Plugin(
    "viaversion",
    "ViaVersion",
    VersionInfo.VERSION,
    "Allow newer Minecraft versions to connect to an older server version.",
    ["_MylesC", "creeper123123321", "Gerrygames", "kennytv", "Matsv"]
)
class KryptonPlugin @Inject constructor(
    val server: Server,
    private val scheduler: Scheduler,
    logger: Logger,
    @DataFolder private val folder: Path
) : ViaPlatform<Player> {

    private val api = KryptonViaAPI()
    private val config = KryptonViaConfig(folder.resolve("config.yml").toFile())
    private val logger = LoggerWrapper(logger)
    private val folderFile = folder.toFile()

    @Listener
    fun onStart(event: ServerStartEvent) {
        val commandHandler = KryptonCommandHandler()
        server.commandManager.register(commandHandler, simpleCommandMeta("viaversion") {
            alias("viaver")
            alias("vvkrypton")
        })
        Via.init(ViaManagerImpl.builder()
            .platform(this)
            .commandHandler(commandHandler)
            .loader(KryptonViaLoader(this))
            .injector(KryptonViaInjector(server.platform))
            .build())

        if (server.pluginManager.isLoaded("viabackwards")) MappingDataLoader.enableMappingsCache()
        (Via.getManager() as ViaManagerImpl).init()
    }

    @Listener
    fun onStop(event: ServerStopEvent) {
        (Via.getManager() as ViaManagerImpl).destroy()
    }

    override fun getApi(): ViaAPI<Player> = api

    override fun getConf(): ViaVersionConfig = config

    override fun getConfigurationProvider(): ConfigurationProvider = config

    override fun getDataFolder(): File = folderFile

    override fun getDump(): JsonObject {
        val extra = JsonObject()
        val plugins = server.pluginManager.plugins.map {
            PluginInfo(
                true,
                it.description.name.ifEmpty { it.description.id },
                it.description.version,
                it.instance?.javaClass?.canonicalName ?: "Unknown",
                it.description.authors.toList()
            )
        }
        extra.add("plugins", GsonUtil.getGson().toJsonTree(plugins))
        return extra
    }

    override fun getLogger(): java.util.logging.Logger = logger

    override fun getOnlinePlayers(): Array<ViaCommandSender> = server.players.stream()
        .map(::KryptonCommandSender)
        .toArray { arrayOfNulls<ViaCommandSender>(it) }

    override fun getPlatformName(): String = server.platform.name

    override fun getPlatformVersion(): String = server.platform.version

    override fun getPluginVersion(): String = VersionInfo.VERSION

    override fun isOldClientsAllowed(): Boolean = true

    override fun isPluginEnabled(): Boolean = true

    override fun kickPlayer(uuid: UUID, message: String): Boolean {
        server.player(uuid)?.disconnect(LegacyComponentSerializer.legacySection().deserialize(message)) ?: return false
        return true
    }

    override fun onReload() = Unit

    override fun runAsync(runnable: Runnable): PlatformTask<*> = runSync(runnable)

    override fun runRepeatingSync(runnable: Runnable, ticks: Long): PlatformTask<*> = KryptonViaTask(
        scheduler.schedule(this, 0L, ticks * 50, TimeUnit.MILLISECONDS) { runnable.run() }
    )

    override fun runSync(runnable: Runnable): PlatformTask<*> = runSync(runnable, 0L)

    override fun runSync(runnable: Runnable, ticks: Long): PlatformTask<*> = KryptonViaTask(
        scheduler.schedule(this, ticks * 50L, TimeUnit.MILLISECONDS) { runnable.run() }
    )

    override fun sendMessage(uuid: UUID, message: String) {
        server.player(uuid)?.sendMessage(COMPONENT_SERIALIZER.deserialize(message))
    }

    companion object {

        val COMPONENT_SERIALIZER = LegacyComponentSerializer.builder()
            .character(ChatColorUtil.COLOR_CHAR)
            .extractUrls()
            .build()
    }
}
