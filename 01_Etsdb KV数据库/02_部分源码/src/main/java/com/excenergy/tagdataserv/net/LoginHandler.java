package com.excenergy.tagdataserv.net;

import com.excenergy.protocol.CommandData;
import com.excenergy.protocol.LoginMapper;
import com.excenergy.tagdataserv.Application;
import com.excenergy.tagdataserv.TagDataException;
import com.excenergy.tagdataserv.TagFactory;
import com.excenergy.tagmeta.TClient;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Set;
import java.util.Vector;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-09-13
 */
public class LoginHandler extends SimpleChannelInboundHandler<CommandData> {
    private static final Logger logger = LoggerFactory.getLogger(LoginHandler.class);
    private final TagFactory tagFactory;
    private final Vector<Session> sessions;

    public LoginHandler(Application app) {
        tagFactory = app.getTagFactory();
        sessions = app.getClientSessions();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CommandData msg) throws Exception {
        if (hasLogin(ctx)) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("The channelContext has login."));
            }
            ctx.fireChannelRead(msg);
        } else if (isLoginCmd(msg)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Login command.");
            }
            LoginMapper.LoginPara para = LoginMapper.decodeRequest(msg.getData());
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Login para:%s", para));
            }
            TClient client = tagFactory.getClient(para.getName());
            int result;
            if (client == null) { // 用户名不存在
                result = LoginMapper.NOT_EXIST;
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("The user name:%s not exist.", para.getName()));
                }
            } else if (para.getPassword().equals(client.getPassword())) { // 成功
                SocketAddress socketAddress = ctx.channel().remoteAddress();
                if (socketAddress instanceof InetSocketAddress) {
                    InetSocketAddress remoteAddress = (InetSocketAddress) socketAddress;
                    Set<String> clientIpSet = tagFactory.getClientIpSet();
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Client IP set:%s", clientIpSet));
                    }
                    String ip = remoteAddress.getAddress().getHostAddress();
                    if (clientIpSet == null || !clientIpSet.contains(ip)) {
                        result = LoginMapper.UN_CONFIG_IP;
//                        String code = "DATA-000004";
//                        String ms = String.format(TagDataException.getMsg(code), ip);
//                        msgQueue.append(new WarnMsg(code, "TagDataServer.net.LoginHandler", ms, new Date()));
                        if (logger.isWarnEnabled()) {
                            logger.warn(String.format("The client IP:%s has not config, now reject register.", ip));
                        }
                    } else if (!ip.equalsIgnoreCase(client.getIp())) {
                        result = LoginMapper.NOT_MATCH;
                        String code = "DATA-000006";
                        String m = String.format(TagDataException.getMsg(code), para.getName(), remoteAddress.getHostName());
//                        msgQueue.append(new WarnMsg(code, "TagDataServer.net.LoginHandler", m, new Date()));
                        if (logger.isWarnEnabled()) {
                            logger.warn(m);
                        }
                    } else {
                        result = LoginMapper.SUCCESS;
                        sessions.add(new Session(ctx.channel(), System.currentTimeMillis()));
                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format("The client login success:%s", para.getName()));
                        }
                    }
                } else {
                    result = LoginMapper.SUCCESS;
                    sessions.add(new Session(ctx.channel(), System.currentTimeMillis()));
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("The client login success:%s", para.getName()));
                    }
                }
            } else { // 密码错误
                result = LoginMapper.PASSWORD_ERROR;
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Login fail, password error. client name:%s, password:%s", para.getName(), para.getPassword()));
                }
            }

            msg.setData(LoginMapper.encodeResponse(result));
            ctx.writeAndFlush(msg);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Close channel:%s", ctx.channel()));
            }
            ctx.close();
        }
    }

    private boolean isLoginCmd(CommandData msg) {
        return msg.getCmdId() == CommandData.LOGIN;
    }

    private boolean hasLogin(ChannelHandlerContext ctx) {
        return sessions.contains(new Session(ctx.channel()));
    }

    public static class Session {
        private Channel channel;
        private long createTime;

        public Session(Channel channel) {
            this.channel = channel;
        }

        public Session(Channel channel, long createTime) {
            this.channel = channel;
            this.createTime = createTime;
        }

        public Channel getChannel() {
            return channel;
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

            return channel.equals(session.channel);
        }

        @Override
        public int hashCode() {
            return channel.hashCode();
        }

        @Override
        public String toString() {
            return "Session{" +
                    "channel=" + channel +
                    ", createTime=" + createTime +
                    '}';
        }
    }
}
