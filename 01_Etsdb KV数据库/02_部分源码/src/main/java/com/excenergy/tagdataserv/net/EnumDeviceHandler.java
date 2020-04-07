package com.excenergy.tagdataserv.net;

import com.excenergy.protocol.CommandData;
import com.excenergy.tagdataserv.TagDataException;
import com.excenergy.tagdataserv.TagFactory;
import com.excenergy.tagmeta.Device;
import com.excenergy.tagmeta.DeviceMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

import static com.excenergy.protocol.EnumDeviceMapper.decodeRequest;
import static com.excenergy.protocol.EnumDeviceMapper.encodeResponse;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-09-16
 */
public class EnumDeviceHandler extends SimpleChannelInboundHandler<CommandData> {
    private static final Logger logger = LoggerFactory.getLogger(EnumDeviceHandler.class);
    private TagFactory tagFactory;

    public EnumDeviceHandler(TagFactory tagFactory) {
        this.tagFactory = tagFactory;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CommandData msg) throws Exception {
        if (msg.getCmdId() == CommandData.ENUM_DEVICE) {
            try {
                Device result;
                String idOrName = decodeRequest(msg.getData());
                if (idOrName.matches("^\\d+$")) { // is integer
                    result = tagFactory.getDevice(Integer.valueOf(idOrName));
                } else {
                    result = tagFactory.getDevice(idOrName);
                }

                if (result == null) {
                    throw new TagDataException("enumdevice", "can't find the device by " + idOrName);
                }
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Enum device:%s", DeviceMapper.toJson(result)));
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
