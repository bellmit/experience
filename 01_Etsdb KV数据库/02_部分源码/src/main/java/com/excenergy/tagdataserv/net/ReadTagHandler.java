package com.excenergy.tagdataserv.net;

import com.excenergy.protocol.CommandData;
import com.excenergy.tagdataserv.TagDataException;
import com.excenergy.tagdataserv.TagFactory;
import com.excenergy.tagmeta.Tag;
import com.excenergy.tagmeta.TagMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.excenergy.protocol.ReadTagMapper.decodeRequest;
import static com.excenergy.protocol.ReadTagMapper.encodeResponse;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-09-16
 */
public class ReadTagHandler extends SimpleChannelInboundHandler<CommandData> {
    private static final Logger logger = LoggerFactory.getLogger(ReadTagHandler.class);
    private TagFactory tagFactory;

    public ReadTagHandler(TagFactory tagFactory) {
        this.tagFactory = tagFactory;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CommandData msg) throws Exception {
        if (msg.getCmdId() == CommandData.READ_TAG) {
            try {
                List<Integer> para = decodeRequest(msg.getData());
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Enum tag para:%s", para));
                }

                List<Tag> result = new ArrayList<>(para.size());
                for (Integer handle : para) {
                    result.add(tagFactory.get(handle));
                }

                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Enum tag result:%s", TagMapper.toJson(result)));
                }

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
