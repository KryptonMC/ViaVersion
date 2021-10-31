package com.viaversion.viaversion.krypton.handlers

import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viaversion.exception.CancelCodecException
import com.viaversion.viaversion.exception.CancelEncoderException
import com.viaversion.viaversion.handlers.ChannelHandlerContextWrapper
import com.viaversion.viaversion.handlers.ViaCodecHandler
import com.viaversion.viaversion.util.PipelineUtil
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import java.lang.reflect.InvocationTargetException

class KryptonEncodeHandler(
    private val info: UserConnection,
    private val minecraftEncoder: MessageToByteEncoder<*>
) : MessageToByteEncoder<Any>(), ViaCodecHandler {

    override fun encode(ctx: ChannelHandlerContext, msg: Any, out: ByteBuf) {
        if (msg !is ByteBuf) {
            try {
                PipelineUtil.callEncode(minecraftEncoder, ChannelHandlerContextWrapper(ctx, this), msg, out)
            } catch (exception: InvocationTargetException) {
                if (exception.cause is Exception) {
                    throw exception.cause as Exception
                } else if (exception.cause is Error) {
                    throw exception.cause as Error
                }
            }
        } else {
            out.writeBytes(msg)
        }
        transform(out)
    }

    override fun transform(bytebuf: ByteBuf) {
        if (!info.checkClientboundPacket()) throw CancelEncoderException.generate(null)
        if (!info.shouldTransformPacket()) return
        info.transformClientbound(bytebuf, CancelEncoderException::generate)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        if (cause is CancelCodecException) return
        super.exceptionCaught(ctx, cause)
    }
}
