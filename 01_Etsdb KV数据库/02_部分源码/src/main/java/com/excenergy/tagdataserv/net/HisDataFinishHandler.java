package com.excenergy.tagdataserv.net;

import com.excenergy.protocol.CommandData;
import com.excenergy.protocol.HisDataReportMapper;
import com.excenergy.tagdataserv.Application;
import com.excenergy.tagdataserv.TagDataException;
import com.excenergy.tagdataserv.TagFactory;
import com.excenergy.tagmeta.Device;
import com.excenergy.tagmeta.TSource;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Vector;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-12-31
 */
public class HisDataFinishHandler extends SimpleChannelInboundHandler<CommandData> {
    private static final Logger logger = LoggerFactory.getLogger(WriteTagHandler.class);
    private final TagFactory tagFactory;
    private final Vector<AgentLoginHandler.Session> agentSessions;
    private Application app;

    public HisDataFinishHandler(Application app) {
        this.app = app;
        tagFactory = app.getTagFactory();
        agentSessions = app.getAgentSessions();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CommandData msg) throws Exception {
        if (msg.getCmdId() == CommandData.HIS_DATA_REPORT) {
            try {
                HisDataReportMapper.ReportPara para = HisDataReportMapper.decodeRequest(msg.getData());
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("His Data report para:%s", para));
                }

                String deviceName = para.getDeviceName();

                // 1. find source
                String sourceIp = null;
                AgentLoginHandler.Session session = new AgentLoginHandler.Session(ctx.channel());
                for (AgentLoginHandler.Session agentSession : agentSessions) {
                    if (session.equals(agentSession)) {
                        sourceIp = agentSession.getSourceIp();
                    }
                }

                if (sourceIp == null) {
                    if (logger.isErrorEnabled()) {
                        logger.error(String.format("Can't find the sourceIp of ctx:%s", ctx));
                    }
                }

                TSource source = tagFactory.getSource(sourceIp);
                if (source == null) {
                    String code = "E-000014";
                    String m = String.format(TagDataException.getMsg(code), sourceIp);
                    if (logger.isErrorEnabled()) {
                        logger.error(m);
                    }
                    throw new TagDataException(code, m);
                }

                List<Device> deviceList = source.getDeviceList();

                // 2. find device
                Integer deviceId = null;
                for (Device device : deviceList) {
                    if (deviceName.equalsIgnoreCase(device.getDesc())) {
                        deviceId = device.getId();
                        break;
                    }
                }

                int result = HisDataReportMapper.SUCCESS;
                if (deviceId == null) {
                    result = HisDataReportMapper.FAIL;
                    if (logger.isErrorEnabled()) {
                        logger.error(String.format("Can't find the device of name:%s", deviceName));
                    }
                } else {
                    para.setDeviceName(String.valueOf(deviceId));
                    Channel tagCalculate = app.getTagCalculate();
                    if (tagCalculate != null) {
                        byte[] data = HisDataReportMapper.encodeRequest(para);
                        tagCalculate.writeAndFlush(new CommandData(CommandData.HIS_DATA_REPORT, CommandData.VERSION_1, data));
                    }
                }

                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("His Data report result:%s", result));
                }
                msg.setData(HisDataReportMapper.encodeResponse(result));

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
