package com.excenergy.tagdataserv.net;

import com.excenergy.protocol.CommandData;
import com.excenergy.tagdataserv.TagDataException;
import com.excenergy.tagdataserv.TagFactory;
import com.excenergy.tagmeta.TSource;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.excenergy.protocol.EnumAllMapper.encodeResponse;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-09-16
 */
public class EnumAllHandler extends SimpleChannelInboundHandler<CommandData> {
    private static final Logger logger = LoggerFactory.getLogger(EnumAllHandler.class);
    private TagFactory tagFactory;

    public EnumAllHandler(TagFactory tagFactory) {
        this.tagFactory = tagFactory;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CommandData msg) throws Exception {
        if (msg.getCmdId() == CommandData.ENUM_ALL) {
            try {
                List<TSource> result = tagFactory.enumAll();

                msg.setData(encodeResponse(result));

                ctx.writeAndFlush(msg);
            } catch (TagDataException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("Something unexpected occurred.", e);
                }
                msg.setErrorCode(CommandData.PROTOCOL_ERROR);
                msg.setData(e.toString().getBytes(StandardCharsets.UTF_8));
                ctx.writeAndFlush(msg);
                throw e;
            } catch (Throwable e) {
                if (logger.isErrorEnabled()) {
                    logger.error("Something unexpected occurred.", e);
                }
                msg.setErrorCode(CommandData.UNKNOWN_ERROR);
                msg.setData(e.toString().getBytes(StandardCharsets.UTF_8));
                ctx.writeAndFlush(msg);
                throw e;
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
