package com.excenergy.tagdataserv.net;

import com.excenergy.protocol.*;
import com.excenergy.tagdataserv.TagDataException;
import com.excenergy.tagdataserv.TagFactory;
import com.excenergy.tagdataserv.TagStore;
import com.excenergy.tagdataserv.TagStorePool;
import com.excenergy.tagmeta.TSource;
import com.excenergy.tagmeta.Tag;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-09-16
 */
public class WriteTagHandler extends SimpleChannelInboundHandler<CommandData> {
    private static final Logger logger = LoggerFactory.getLogger(WriteTagHandler.class);
    private TagFactory tagFactory;
    private TagStorePool tagStorePool;
    private Vector<AgentLoginHandler.Session> agentSessions;

    public WriteTagHandler(TagFactory tagFactory, TagStorePool tagStorePool, Vector<AgentLoginHandler.Session> agentSessions) {
        this.tagFactory = tagFactory;
        this.tagStorePool = tagStorePool;
        this.agentSessions = agentSessions;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CommandData msg) throws Exception {
        if (msg.getCmdId() == CommandData.WRITE_TAG) {
            try {
                WriteTagMapper.WriteTagPara para = WriteTagMapper.decodeRequest(msg.getData());
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Write tag para:%s", para));
                }
                List<Integer> handles = para.getHandles();
                List<TagValue> tagValues = para.getTagValues();

                List<Integer> result = new ArrayList<>(handles.size());
                for (int i = 0; i < handles.size(); i++) {
                    try {
                        // 1. 找到channel，根据sourceIP
                        // 2. 封装发送的包，定义协议
                        // 3. 发送，用新格式
                        // 4. 写入实时库
                        TagValue tagValue = tagValues.get(i);
                        try {
                            Tag tag = tagFactory.get(handles.get(i));
                            TSource source = tagFactory.getSource(tag.getSourceId());
                            String ip = source.getIp();
                            Channel channel = getContext(ip, source.getProtocol());
                            if (channel != null) {
                                KernelWriteTagMapper.KernelWriteTagPara para1 = new KernelWriteTagMapper.KernelWriteTagPara(handles.get(i), tagValue);
                                byte[] bytes = KernelWriteTagMapper.encodeRequest(para1);

                                CommandData commandData = new CommandData(CommandData.WRITE_TAG, CommandData.VERSION_1, bytes);
                                channel.writeAndFlush(commandData);
                            } else {
                                tagValue.setQuality((short) 162);
                            }
                        } catch (RuntimeException e) {
                            tagValue.setQuality((short) 162);
                        }

                        TagStore tagStore = tagStorePool.getTagStore(handles.get(i));
                        tagStore.writeVal(tagValue);

                        result.add(i, WriteTagMapper.SUCCESS);
                    } catch (TagDataException e) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Something unexpected occurred.", e);
                        }
                        if ("E-000002".equals(e.getCode())) {
                            result.add(i, WriteTagMapper.TAG_NOT_EXIST);
                        } else {
                            result.add(i, WriteTagMapper.UNKNOWN_EXCEPTION);
                        }
                    } catch (RuntimeException e) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Something unexpected occurred.", e);
                        }
                        result.add(i, WriteTagMapper.UNKNOWN_EXCEPTION);
                    }
                }

                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Write tag result:%s", result));
                }
                msg.setData(WriteValMapper.encodeResponse(result));

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

    private Channel getContext(String ip, byte protocol) {
        for (AgentLoginHandler.Session s : agentSessions) {
            if (s.getProtocol() == protocol) {
                SocketAddress socketAddress = s.getChannel().remoteAddress();
                String address = socketAddress.toString();

                if (address.contains(ip)) {
                    return s.getChannel();
                }
            }
        }
        return null;
    }
}
