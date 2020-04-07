package com.excenergy.tagdataserv.net;

import com.excenergy.protocol.AgentLoginMapper;
import com.excenergy.protocol.CommandData;
import com.excenergy.tagdataserv.Application;
import com.excenergy.tagdataserv.TagFactory;
import com.typesafe.config.Config;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.Vector;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-09-13
 */
public class AgentLoginHandler extends SimpleChannelInboundHandler<CommandData> {
    private static final Logger logger = LoggerFactory.getLogger(AgentLoginHandler.class);
    private final TagFactory tagFactory;
    private final Vector<Session> sessions;
    private final String password;
    private final String name;

    public AgentLoginHandler(Application app) {
        tagFactory = app.getTagFactory();
        sessions = app.getAgentSessions();
        Config config = app.getConfig();
        name = config.getString("net.agent.name");
        password = config.getString("net.agent.password");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        sessions.remove(new Session(ctx.channel()));
        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CommandData msg) throws Exception {
        if (hasLogin(ctx)) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("The channelContext has login."));
            }
            ctx.fireChannelRead(msg);
        } else if (isAgentLoginCmd(msg)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Login command.");
            }
            AgentLoginMapper.AgentLoginPara para = AgentLoginMapper.decodeRequest(msg.getData());

            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Login para:%s", para));
            }

            int result;
            String sourceIp = para.getSourceIp();
            if (sourceIp == null || sourceIp.trim().isEmpty()) {
                sourceIp = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
            }

            Set<String> sourceIpSet = tagFactory.getSourceIpSet();
            if (sourceIpSet == null || !sourceIpSet.contains(sourceIp)) {
                result = AgentLoginMapper.NOT_EXIST;
                if (logger.isWarnEnabled()) {
                    logger.warn(String.format("The Source IP:%s has not config, now reject login.", sourceIp));
                }
//                String code = "DATA-000005";
//                String m = String.format(TagDataException.getMsg(code), sourceIp);
//                msgQueue.append(new WarnMsg(code, "TagDataServer.net.LoginHandler", m, new Date()));
                ctx.close();
            } else if (!name.equals(para.getName()) || !password.equals(para.getPassword())) {
                result = AgentLoginMapper.PASSWORD_ERROR;
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Login fail, password error. client name:%s, password:%s", para.getName(), para.getPassword()));
                }
            } else {
                result = AgentLoginMapper.SUCCESS;
                sessions.add(new Session(ctx.channel(), para.getProtocol(), System.currentTimeMillis(), sourceIp));
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("The client login success:%s", para.getName()));
                }
            }
            msg.setData(AgentLoginMapper.encodeResponse(result));
            ctx.writeAndFlush(msg);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Close channel:%s", ctx.channel()));
            }
            sessions.remove(new Session(ctx.channel()));
            ctx.close();
        }
    }

    private boolean isAgentLoginCmd(CommandData msg) {
        return msg.getCmdId() == CommandData.AGENT_LOGIN;
    }

    private boolean hasLogin(ChannelHandlerContext ctx) {
        return sessions.contains(new Session(ctx.channel()));
    }

    public static class Session {
        private Channel channel;
        private byte protocol;
        private String sourceIp;
        private long createTime;

        public Session(Channel channel) {
            this.channel = channel;
        }

        public Session(Channel channel, byte protocol, long createTime, String sourceIp) {
            this.channel = channel;
            this.protocol = protocol;
            this.createTime = createTime;
            this.sourceIp = sourceIp;
        }

        public Channel getChannel() {
            return channel;
        }

        public byte getProtocol() {
            return protocol;
        }

        public String getSourceIp() {
            return sourceIp;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Session session = (Session) o;

            if (!channel.equals(session.channel)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            return channel.hashCode();
        }

        @Override
        public String toString() {
            return "Session{" +
                    "channel=" + channel +
                    ", protocol=" + protocol +
                    ", sourceIp='" + sourceIp + '\'' +
                    ", createTime=" + createTime +
                    '}';
        }
    }
}
