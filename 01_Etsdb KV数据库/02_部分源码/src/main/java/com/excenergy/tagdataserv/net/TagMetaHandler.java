package com.excenergy.tagdataserv.net;

import com.excenergy.protocol.CmdDataMapper;
import com.excenergy.protocol.CommandData;
import com.excenergy.tagdataserv.TagFactory;
import com.excenergy.tagmeta.TagMeta;
import com.excenergy.tagmeta.TagMetaMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-09-22
 */
public class TagMetaHandler extends SimpleChannelInboundHandler<CommandData> {
    private static final Logger logger = LoggerFactory.getLogger(TagMetaHandler.class);
    private TagFactory tagFactory;

    public TagMetaHandler(TagFactory tagFactory) {
        this.tagFactory = tagFactory;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CommandData msg) throws Exception {
        String result = "FAIL";
        try {
            switch (msg.getCmdId()) {
                case CommandData.DOWNLOAD_TAG_META:
                default:
                    ByteBuffer bf = ByteBuffer.wrap(msg.getData());
                    bf.order(ByteOrder.LITTLE_ENDIAN);
                    String json = CmdDataMapper.decodeStr(bf);
                    TagMeta tagMeta = TagMetaMapper.fromJson(json);
                    if (tagMeta != null) {
                        tagFactory.put(tagMeta);
                        result = "SUCCESS";
                    }
            }
        } catch (Throwable e) {
            logger.error("Something unexpected occur.", e);
        } finally {
            ByteBuffer bf = ByteBuffer.allocate(4 + result.getBytes(StandardCharsets.UTF_8).length);
            bf.order(ByteOrder.LITTLE_ENDIAN);
            CmdDataMapper.encodeStr(result, bf);
            msg.setData(bf.array());
            ctx.writeAndFlush(msg);
        }
    }
}
