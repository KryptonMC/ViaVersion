package com.viaversion.viaversion.krypton.handlers

import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viaversion.exception.CancelCodecException
import com.viaversion.viaversion.exception.CancelDecoderException
import com.viaversion.viaversion.util.PipelineUtil
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import java.lang.reflect.InvocationTargetException

class KryptonDecodeHandler(
    private val info: UserConnection,
    private val minecraftDecoder: ByteToMessageDecoder
) : ByteToMessageDecoder() {

    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        if (!info.checkServerboundPacket()) {
            msg.clear() // Don't accumulate
            throw CancelDecoderException.generate(null)
        }

        var transformedBuf: ByteBuf? = null
        try {
            if (info.shouldTransformPacket()) {
                transformedBuf = ctx.alloc().buffer().writeBytes(msg)
                info.transformServerbound(transformedBuf, CancelDecoderException::generate)
            }

            try {
                out.addAll(PipelineUtil.callDecode(minecraftDecoder, ctx, transformedBuf ?: msg))
            } catch (exception: InvocationTargetException) {
                if (exception.cause is Exception) {
                    throw exception.cause as Exception
                } else if (exception.cause is Error) {
                    throw exception.cause as Error
                }
            }
        } finally {
            transformedBuf?.release()
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        if (cause is CancelCodecException) return
        super.exceptionCaught(ctx, cause)
    }
}
