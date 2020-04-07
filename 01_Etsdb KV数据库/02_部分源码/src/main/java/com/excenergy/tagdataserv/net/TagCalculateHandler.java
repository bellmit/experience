package com.excenergy.tagdataserv.net;

import com.excenergy.protocol.CommandData;
import com.excenergy.protocol.HisDataReportMapper;
import com.excenergy.tagdataserv.Application;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-09-22
 */
public class TagCalculateHandler extends SimpleChannelInboundHandler<CommandData> {
    private static final Logger logger = LoggerFactory.getLogger(TagCalculateHandler.class);
    private Application app;

    public TagCalculateHandler(Application app) {
        this.app = app;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        app.setTagCalculate(ctx.channel());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        app.setTagCalculate(null);
        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CommandData msg) throws Exception {
        switch (msg.getCmdId()) {
            case CommandData.HIS_DATA_REPORT:
                HisDataReportMapper.decodeResponse(msg.getData());
                if (logger.isInfoEnabled()) {
                    logger.info("Tag Calculate Server has received HisDataReport message.");
                }
                break;
            case CommandData.TAG_META_CHANGED:
                if (logger.isInfoEnabled()) {
                    logger.info("Tag Calculate Server has received TagMeta changed message.");
                }
                break;
            default:
                ctx.fireChannelRead(msg);
        }
    }
}
