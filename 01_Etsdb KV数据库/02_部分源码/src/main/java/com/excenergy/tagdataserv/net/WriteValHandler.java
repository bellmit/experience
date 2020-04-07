package com.excenergy.tagdataserv.net;

import com.excenergy.protocol.CommandData;
import com.excenergy.protocol.TagValue;
import com.excenergy.protocol.WriteValMapper;
import com.excenergy.tagdataserv.TagDataException;
import com.excenergy.tagdataserv.TagStore;
import com.excenergy.tagdataserv.TagStorePool;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.excenergy.protocol.WriteValMapper.decodeRequest;
import static com.excenergy.protocol.WriteValMapper.encodeResponse;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-09-13
 */
public class WriteValHandler extends SimpleChannelInboundHandler<CommandData> {
    private static final Logger logger = LoggerFactory.getLogger(WriteValHandler.class);
    private TagStorePool tagStorePool;

    public WriteValHandler(TagStorePool tagStorePool) {
        this.tagStorePool = tagStorePool;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CommandData msg) throws Exception {
        if (msg.getCmdId() == CommandData.WRITE_VAL) {
            try {
                WriteValMapper.WriteValPara para = decodeRequest(msg.getData());
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Write val para:%s", para));
                }
                List<Integer> handles = para.getHandles();
                List<TagValue> tagValues = para.getTagValues();

                List<Integer> result = new ArrayList<>(handles.size());
                for (int i = 0; i < handles.size(); i++) {
                    try {
                        TagStore tagStore = tagStorePool.getTagStore(handles.get(i));
                        tagStore.writeVal(tagValues.get(i));

                        result.add(i, WriteValMapper.SUCCESS);
                    } catch (TagDataException e) {
                        if (logger.isErrorEnabled()) {
                            logger.error(String.format("Something unexpected occurred. Handle:%d, tagValue:%s", handles.get(i), tagValues.get(i)), e);
                        }
                        if ("E-000002".equals(e.getCode())) {
                            result.add(i, WriteValMapper.TAG_NOT_EXIST);
                        } else {
                            result.add(i, WriteValMapper.UNKNOWN_EXCEPTION);
                        }
                    } catch (RuntimeException e) {
                        if (logger.isErrorEnabled()) {
                            logger.error(String.format("Something unexpected occurred. Handle:%d, tagValue:%s", handles.get(i), tagValues.get(i)), e);
                        }
                        result.add(i, WriteValMapper.UNKNOWN_EXCEPTION);
                    }
                }

                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Write val result:%s", result));
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
