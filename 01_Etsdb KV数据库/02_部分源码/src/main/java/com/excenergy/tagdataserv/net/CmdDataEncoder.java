package com.excenergy.tagdataserv.net;

import com.excenergy.protocol.CmdDataMapper;
import com.excenergy.protocol.CommandData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-09-13
 */
public class CmdDataEncoder extends MessageToByteEncoder<CommandData> {
    private static final Logger logger = LoggerFactory.getLogger(CmdDataEncoder.class);
    private static long netWriteBytes; // 网络写入量，用来算网络吞吐量

    public CmdDataEncoder() {
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, CommandData msg, ByteBuf out) throws Exception {
        if (msg == null) {
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Encode sn:%d, command:%d", msg.getSn(), msg.getCmdId()));
        }

        byte[] cmd = CmdDataMapper.encodeCmd(msg);
        byte[] pkg = CmdDataMapper.encodePkg(cmd);
        ByteBuf encoded = Unpooled.wrappedBuffer(pkg);
        try {
            out.writeBytes(encoded);
            CmdDataEncoder.addNetWriteBytes(pkg.length);
        } finally {
            encoded.release();
        }
    }

    public static synchronized void addNetWriteBytes(int size) {
        netWriteBytes += size;
    }

    public static synchronized long getNetWriteBytes() {
        return netWriteBytes;
    }
}
