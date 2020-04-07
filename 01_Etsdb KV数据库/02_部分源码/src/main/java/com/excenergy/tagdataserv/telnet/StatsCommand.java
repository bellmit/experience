package com.excenergy.tagdataserv.telnet;

import com.excenergy.protocol.TagValue;
import com.excenergy.tagdataserv.Application;
import com.excenergy.tagdataserv.TagFactory;
import com.excenergy.tagdataserv.TagStorePool;
import com.excenergy.tagdataserv.stats.StatsItems;
import com.excenergy.telnetutil.TelnetClient;
import com.excenergy.telnetutil.TelnetCommand;
import com.excenergy.telnetutil.TelnetServer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

import static com.excenergy.telnetutil.TelnetServer.NEXT_LINE;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-12-20
 */
public class StatsCommand {
    private final StatsItems statsItems;
    private final TagStorePool tagStorePool;
    private final TagFactory tagFactory;

    public StatsCommand(Application app) {
        statsItems = app.getStatsItems();
        tagStorePool = app.getTagStorePool();
        tagFactory = app.getTagFactory();
    }

    @TelnetCommand(command = "stat", usage = "stat", help = "Show the stats message.")
    public void stat(TelnetClient cli, TelnetServer srv) {
        try {
            BufferedWriter out = cli.getOut();
            Map<Integer, TagValue> stats = statsItems.getStats();
            for (Map.Entry<Integer, TagValue> entry : stats.entrySet()) {
                Integer handle = entry.getKey();
                TagValue value = entry.getValue();

                out.write(String.format("%s=%s", tagFactory.get(handle).getName().substring(2), value.getVal()));
                out.write(NEXT_LINE);
            }
            out.flush();
        } catch (IOException e) {
            srv.log(Level.SEVERE, "Failure sending command history to client");
        }
    }
}
