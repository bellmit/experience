package com.excenergy.tagdataserv;

import com.excenergy.emq.MsgQueueService;
import com.excenergy.tagdataserv.disk.TVDB;
import com.excenergy.tagdataserv.log.PersistThread;
import com.excenergy.tagdataserv.log.TVLog;
import com.excenergy.tagdataserv.log.WriteLogThread;
import com.excenergy.tagdataserv.mem.TVMemPool;
import com.excenergy.tagdataserv.net.AgentLoginHandler;
import com.excenergy.tagdataserv.net.LoginHandler;
import com.excenergy.tagdataserv.net.PingSender;
import com.excenergy.tagdataserv.stats.StatsItems;
import com.typesafe.config.Config;
import io.netty.channel.Channel;

import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-12-18
 */
public class Application {
    public enum AppStatus {
        Closed, Init, WaitingTagMeta, Working
    }

    private Long onlineTime;
    private AppStatus status;
    private Config config;
    private TagFactory tagFactory;
    private TagStorePool tagStorePool;
    private TVLog tvLog;
    private TVMemPool memPool;
    private TVDB tvdb;
    private StatsItems statsItems;
    private PersistThread persistThread;
    private WriteLogThread writeLogThread;
    private Vector<LoginHandler.Session> clientSessions;
    private Vector<AgentLoginHandler.Session> agentSessions;
    private ExecutorService threadPool;
    private ScheduledExecutorService scheduledPool;
    private Channel tagCalculate;
    private Channel tagMeta;
    private PingSender pingSender;
    private MsgQueueService msgQueueService;

    public Long getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(Long onlineTime) {
        this.onlineTime = onlineTime;
    }

    public AppStatus getStatus() {
        return status;
    }

    public void setStatus(AppStatus status) {
        this.status = status;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public TagFactory getTagFactory() {
        return tagFactory;
    }

    public void setTagFactory(TagFactory tagFactory) {
        this.tagFactory = tagFactory;
    }

    public TagStorePool getTagStorePool() {
        return tagStorePool;
    }

    public void setTagStorePool(TagStorePool tagStorePool) {
        this.tagStorePool = tagStorePool;
    }

    public TVLog getTvLog() {
        return tvLog;
    }

    public void setTvLog(TVLog tvLog) {
        this.tvLog = tvLog;
    }

    public TVMemPool getMemPool() {
        return memPool;
    }

    public void setMemPool(TVMemPool memPool) {
        this.memPool = memPool;
    }

    public TVDB getTvdb() {
        return tvdb;
    }

    public void setTvdb(TVDB tvdb) {
        this.tvdb = tvdb;
    }

    public StatsItems getStatsItems() {
        return statsItems;
    }

    public void setStatsItems(StatsItems statsItems) {
        this.statsItems = statsItems;
    }

    public PersistThread getPersistThread() {
        return persistThread;
    }

    public void setPersistThread(PersistThread persistThread) {
        this.persistThread = persistThread;
    }

    public WriteLogThread getWriteLogThread() {
        return writeLogThread;
    }

    public void setWriteLogThread(WriteLogThread writeLogThread) {
        this.writeLogThread = writeLogThread;
    }

    public Vector<LoginHandler.Session> getClientSessions() {
        return clientSessions;
    }

    public void setClientSessions(Vector<LoginHandler.Session> clientSessions) {
        this.clientSessions = clientSessions;
    }

    public Vector<AgentLoginHandler.Session> getAgentSessions() {
        return agentSessions;
    }

    public void setAgentSessions(Vector<AgentLoginHandler.Session> agentSessions) {
        this.agentSessions = agentSessions;
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }

    public ScheduledExecutorService getScheduledPool() {
        return scheduledPool;
    }

    public void setScheduledPool(ScheduledExecutorService scheduledPool) {
        this.scheduledPool = scheduledPool;
    }

    public Channel getTagCalculate() {
        return tagCalculate;
    }

    public void setTagCalculate(Channel tagCalculate) {
        this.tagCalculate = tagCalculate;
    }

    public Channel getTagMeta() {
        return tagMeta;
    }

    public void setTagMeta(Channel tagMeta) {
        this.tagMeta = tagMeta;
    }

    public PingSender getPingSender() {
        return pingSender;
    }

    public void setPingSender(PingSender pingSender) {
        this.pingSender = pingSender;
    }

    public MsgQueueService getMsgQueueService() {
        return msgQueueService;
    }

    public void setMsgQueueService(MsgQueueService msgQueueService) {
        this.msgQueueService = msgQueueService;
    }
}
