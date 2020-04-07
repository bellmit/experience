package com.excenergy.tagdataserv.net;

import com.excenergy.protocol.CommandData;
import com.excenergy.protocol.ReadHisIntervalMapper;
import com.excenergy.protocol.TagValue;
import com.excenergy.tagdataserv.TagDataException;
import com.excenergy.tagdataserv.TagStore;
import com.excenergy.tagdataserv.TagStorePool;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.excenergy.protocol.ReadHisIntervalMapper.decodeRequest;
import static com.excenergy.protocol.ReadHisIntervalMapper.encodeResponse;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-09-16
 */
public class ReadHisIntervalHandler extends SimpleChannelInboundHandler<CommandData> {
    private static final Logger logger = LoggerFactory.getLogger(ReadHisIntervalHandler.class);
    private final TagStorePool tagStorePool;

    public ReadHisIntervalHandler(TagStorePool tagStorePool) {
        this.tagStorePool = tagStorePool;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CommandData msg) throws Exception {
        if (msg.getCmdId() == CommandData.READ_HIS_INTERVAL) {
            try {
                ReadHisIntervalMapper.ReadHisIntervalPara para = decodeRequest(msg.getData());
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Read his interval para:%s", para));
                }
                List<Integer> handles = para.getHandles();
                DateTime startTime = para.getStartTime();
                DateTime endTime = para.getEndTime();
                int interval = para.getInterval();
                List<List<TagValue>> result = new ArrayList<>();
                for (Integer handle : handles) {
                    TagStore tagStore = tagStorePool.getTagStore(handle);
                    List<TagValue> tvs = tagStore.readHis(startTime.getMillis(), endTime.getMillis(), interval);
                    result.add(tvs);
                }
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Read his interval result:%s", result));
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
