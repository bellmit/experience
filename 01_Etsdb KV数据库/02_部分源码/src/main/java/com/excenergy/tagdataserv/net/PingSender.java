package com.excenergy.tagdataserv.net;

import com.excenergy.protocol.CommandData;
import com.excenergy.tagdataserv.Application;
import io.netty.channel.Channel;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2014-01-03
 */
public class PingSender implements Runnable {
    private final Application app;
    private int interval;

    public PingSender(Application app) {
        this.app = app;
        interval = app.getConfig().getInt("net.ping_interval");
    }

    public void start() {
        app.getScheduledPool().scheduleAtFixedRate(this, 1, interval, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        try {
            Vector<AgentLoginHandler.Session> sessions = app.getAgentSessions();
            if (sessions.isEmpty()) {
                return;
            }
            CommandData cmdData = new CommandData(CommandData.PING, CommandData.VERSION_1, new byte[]{});
            for (AgentLoginHandler.Session session : sessions) {
                if (session != null) {
                    Channel channel = session.getChannel();
                    if (channel != null && channel.isActive()) {
                        channel.writeAndFlush(cmdData);
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
