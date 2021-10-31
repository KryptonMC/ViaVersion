package com.viaversion.viaversion.krypton.platform

import com.google.gson.JsonObject
import com.viaversion.viaversion.api.platform.ViaInjector
import com.viaversion.viaversion.connection.UserConnectionImpl
import com.viaversion.viaversion.krypton.handlers.KryptonDecodeHandler
import com.viaversion.viaversion.krypton.handlers.KryptonEncodeHandler
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl
import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.handler.codec.MessageToByteEncoder
import net.kyori.adventure.key.Key
import org.kryptonmc.api.Platform
import org.kryptonmc.krypton.NettyProcess

class KryptonViaInjector(private val platform: Platform) : ViaInjector {

    override fun inject() {
        NettyProcess.addListener(KEY) {
            val connection = UserConnectionImpl(it)
            ProtocolPipelineImpl(connection)
            val encoder = KryptonEncodeHandler(connection, it.pipeline()["encoder"] as MessageToByteEncoder<*>)
            val decoder = KryptonDecodeHandler(connection, it.pipeline()["decoder"] as ByteToMessageDecoder)
            it.pipeline().replace("encoder", "encoder", encoder)
            it.pipeline().replace("decoder", "decoder", decoder)
        }
    }

    override fun uninject() {
        NettyProcess.removeListener(KEY)
    }

    override fun getServerProtocolVersion(): Int = platform.protocolVersion

    override fun getDump(): JsonObject = JsonObject()

    companion object {

        private val KEY = Key.key("viaversion", "injector")
    }
}
