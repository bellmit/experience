package com.excenergy.tagdataserv.telnet;

import com.excenergy.protocol.TagValue;
import com.excenergy.tagdataserv.TagStorePool;
import com.excenergy.telnetutil.TelnetClient;
import com.excenergy.telnetutil.TelnetCommand;
import com.excenergy.telnetutil.TelnetServer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-12-16
 */
public class TagStoreCommand {
    public static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
    private TagStorePool tagStorePool;
    private DateTimeFormatter formatter = DateTimeFormat.forPattern(PATTERN);

    public TagStoreCommand(TagStorePool tagStorePool) {
        this.tagStorePool = tagStorePool;
    }

    @TelnetCommand(command = "readvalue", usage = "readvalue -tag <handle>", help = "Show the real value of handle.")
    public void readReal(TelnetClient cli, TelnetServer srv, String t, final Integer handle) {
        TagValue tagValue = tagStorePool.getTagStore(handle).readReal();
        try {
            BufferedWriter out = cli.getOut();
            out.write(String.format("%s\t%s\t%s", "Time", "Value", "Quality"));
            out.write(TelnetServer.NEXT_LINE);
            if (tagValue != null) {
                out.write(String.format("%s\t%s\t%d", tagValue.getTime().toString(formatter), tagValue.getVal(), tagValue.getQuality()));
            }
            out.flush();
        } catch (IOException e) {
            srv.log(Level.SEVERE, "Failure sending command history to client");
        }
    }

    @TelnetCommand(command = "readhis", usage = "readhis -tag <handle> -h <hours>",
            help = "Read latest hours values of handle.")
    public void readHis(TelnetClient cli, TelnetServer srv, final String t, final Integer handle, final String h, final Integer hours) {
        DateTime e = DateTime.now();
        DateTime b = e.minusHours(hours);
        readHis(cli, srv, handle, b, e);
    }

    @TelnetCommand(command = "readhis2", usage = "readhis2 -tag <handle> -b <startTime> -e <endTime>",
            help = "Read the value of handle from startTime to endTime. Time format:\"" + PATTERN + "\"")
    public void readHis(TelnetClient cli, TelnetServer srv, String t, Integer handle, String _b, String startTime, String _e, String endTime) {
        DateTime s = DateTime.parse(startTime, formatter);
        DateTime e = DateTime.parse(endTime, formatter);
        if (s.compareTo(e) > 0) {
            readHis(cli, srv, handle, e, s);
        } else {
            readHis(cli, srv, handle, s, e);
        }
    }

    private void readHis(TelnetClient cli, TelnetServer srv, Integer handle, DateTime s, DateTime e) {
        long start = s.getMillis();
        long end = e.getMillis();
        List<TagValue> tagValueList = tagStorePool.getTagStore(handle).readHis(start, end);
        try {
            BufferedWriter out = cli.getOut();
            out.write(String.format("%s\t%s\t%s", "Time", "Value", "Quality"));
            out.write(TelnetServer.NEXT_LINE);
            if (tagValueList != null) {
                for (TagValue tagValue : tagValueList) {
                    out.write(String.format("%s\t%s\t%d", tagValue.getTime().toString(formatter), tagValue.getVal(), tagValue.getQuality()));
                    out.write(TelnetServer.NEXT_LINE);
                }
            }
            out.flush();
        } catch (IOException ex) {
            srv.log(Level.SEVERE, "Failure sending command history to client");
        }
    }

    @TelnetCommand(command = "sample", usage = "sample <handle> <time>",
            help = "Read the value of handle at time. Time format:yyyy-MM-dd_HH:mm:ss.")
    public void sample(TelnetClient cli, TelnetServer srv, final Integer handle, final String time) {
        long t = DateTime.parse(time, formatter).getMillis();
        List<TagValue> tagValueList = tagStorePool.getTagStore(handle).readHis(t, t, 1);
        try {
            BufferedWriter out = cli.getOut();
            if (tagValueList != null) {
                for (TagValue tagValue : tagValueList) {
                    out.write(String.valueOf(tagValue));
                    out.write(TelnetServer.NEXT_LINE);
                }
            }
            out.flush();
        } catch (IOException e) {
            srv.log(Level.SEVERE, "Failure sending command history to client");
        }
    }

    @TelnetCommand(command = "sample2", usage = "sample2 <handle> <startTime> <endTime> <interval>",
            help = "Read the value of handle at time. Time format:yyyy-MM-dd_HH:mm:ss.")
    public void sample2(TelnetClient cli, TelnetServer srv, final Integer handle, String startTime, String endTime, Integer interval) {
        DateTime s = DateTime.parse(startTime, formatter);
        DateTime end = DateTime.parse(endTime, formatter);
        List<TagValue> tagValueList = tagStorePool.getTagStore(handle).readHis(s.getMillis(), end.getMillis(), interval);
        try {
            BufferedWriter out = cli.getOut();
            if (tagValueList != null) {
                for (TagValue tagValue : tagValueList) {
                    out.write(String.valueOf(tagValue));
                    out.write(TelnetServer.NEXT_LINE);
                }
            }
            out.flush();
        } catch (IOException e) {
            srv.log(Level.SEVERE, "Failure sending command history to client");
        }
    }

    @TelnetCommand(command = "clear", admin = true, usage = "clear <handle>",
            help = "Clean all TagValue of the handle.")
    public void clear(TelnetClient cli, TelnetServer srv, final Integer handle) {
        boolean success = tagStorePool.getTagStore(handle).clear(handle);
        try {
            BufferedWriter out = cli.getOut();
            out.write(success ? "Success" : "Fail");
            out.write(TelnetServer.NEXT_LINE);
            out.flush();
        } catch (IOException e) {
            srv.log(Level.SEVERE, "Failure sending command history to client");
        }
    }
}
