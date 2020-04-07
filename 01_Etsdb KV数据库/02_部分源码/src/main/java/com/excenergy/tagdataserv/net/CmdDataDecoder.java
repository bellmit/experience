package com.excenergy.tagdataserv.net;

import com.excenergy.protocol.CmdDataMapper;
import com.excenergy.protocol.CommandData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-09-13
 */
public class CmdDataDecoder extends ByteToMessageDecoder {
    private static final Logger logger = LoggerFactory.getLogger(CmdDataDecoder.class);
    private static long netReadBytes; // 网络写出量，用来算网络吞吐量

    public CmdDataDecoder() {
        super.setSingleDecode(true);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 14) {
            return;
        }

        byte[] pkg = new byte[in.readableBytes()];
        in.getBytes(0, pkg);
        in.skipBytes(in.readableBytes());

        CmdDataDecoder.addNetReadBytes(pkg.length);
        byte[] cmdBytes = CmdDataMapper.decodePkg(pkg);
        CommandData cmd = CmdDataMapper.decodeCmd(cmdBytes);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Decode sn:%d, command:%d", cmd.getSn(), cmd.getCmdId()));
        }
        out.add(cmd);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (logger.isWarnEnabled()) {
            logger.warn(String.format("Exception caught of the channel:%s", ctx.channel()), cause);
        }
    }

    public static synchronized void addNetReadBytes(int size) {
        netReadBytes += size;
    }

    public static synchronized long getNetReadBytes() {
        return netReadBytes;
    }
}
