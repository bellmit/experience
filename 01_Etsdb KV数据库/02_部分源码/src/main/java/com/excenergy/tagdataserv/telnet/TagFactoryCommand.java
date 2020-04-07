package com.excenergy.tagdataserv.telnet;

import com.excenergy.tagdataserv.TagFactory;
import com.excenergy.tagmeta.Tag;
import com.excenergy.tagmeta.TagAttribute;
import com.excenergy.tagmeta.VirtualTag;
import com.excenergy.telnetutil.TelnetClient;
import com.excenergy.telnetutil.TelnetCommand;
import com.excenergy.telnetutil.TelnetServer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import static com.excenergy.telnetutil.TelnetServer.NEXT_LINE;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-12-13
 */
public class TagFactoryCommand {
    private TagFactory tagFactory;

    public TagFactoryCommand(TagFactory tagFactory) {
        this.tagFactory = tagFactory;
    }

    @TelnetCommand(command = "handle", usage = "handle <handle>", help = "Show the tag message of handle.")
    public void getTagByHandle(TelnetClient cli, TelnetServer srv, final Integer handle) {
        Tag tag = tagFactory.get(handle);
        writeTag(cli, srv, tag);
    }

    @TelnetCommand(command = "pr", usage = "pr <handle>", help = "Show the PR tag of R or reverse.")
    public void pr(TelnetClient cli, TelnetServer srv, final Integer handle) {
        Tag tag = tagFactory.get(handle);
        Tag pr = null;
        if (tag.isReal()) {
            pr = tagFactory.get(String.format("p%s", tag.getName()), false);
        } else if (tag.isPreReal()) {
            pr = tagFactory.get(tag.getName().substring(1), false);
        }

        if (pr != null) {
            writeTag(cli, srv, pr);
        } else {
            try {
                cli.getOut().write("Can't find pr of r or r of pr.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeTag(TelnetClient cli, TelnetServer srv, Tag tag) {
        try {
            BufferedWriter out = cli.getOut();
            out.write(String.format("%s=%s", "handle", tag.getHandle()));
            out.write(NEXT_LINE);
            out.write(String.format("%s=%s", "name", tag.getName()));
            out.write(NEXT_LINE);
            Map<String, TagAttribute> attributeList = tag.getAttributeList();
            for (TagAttribute att : attributeList.values()) {
                out.write(String.format("%s=%s", att.getDisplayName(), att.getValue()));
                out.write(NEXT_LINE);
            }
            if (tag.isVirtual()) {
                VirtualTag vTag = (VirtualTag) tag;
                List<Integer> dependTagIdList = vTag.getDependTagIdList();
                out.write(String.format("%s=%s", "dependTagIdList", dependTagIdList));
                out.write(NEXT_LINE);

                List<VirtualTag> children = vTag.getChildren();
                List<Integer> childHandleList = new ArrayList<>(children.size());
                for (VirtualTag child : children) {
                    childHandleList.add(child.getHandle());
                }
                out.write(String.format("%s=%s", "children", childHandleList));
                out.write(NEXT_LINE);
            } else if (tag.isReal()) {
                Tag pr = tagFactory.get(String.format("p%s", tag.getName()), false);
                out.write(String.format("%s=%s", "PR Handle", pr.getHandle()));
                out.write(NEXT_LINE);
            } else if (tag.isPreReal()) {
                Tag pr = tagFactory.get(tag.getName().substring(1), false);
                out.write(String.format("%s=%s", "R handle", pr.getHandle()));
                out.write(NEXT_LINE);
            }
            out.flush();
        } catch (IOException e) {
            srv.log(Level.SEVERE, "Failure sending command history to client");
        }
    }

    @TelnetCommand(command = "name", usage = "name <tagName>", help = "Show the tag message of name.")
    public void getTagByName(TelnetClient cli, TelnetServer srv, final String name) {
        Tag tag = tagFactory.get(name, false);
        writeTag(cli, srv, tag);
    }

    @TelnetCommand(command = "enum", usage = "enum <regex>", help = "Enum names match regex.")
    public void enumTag(TelnetClient cli, TelnetServer srv, final String regex) {
        List<Tag> tagList = tagFactory.enumTag(regex, false);
        try {
            BufferedWriter out = cli.getOut();
            out.write("Name\tHandle");
            out.write(NEXT_LINE);
            for (Tag tag : tagList) {
                out.write(String.format("%s\t%d", tag.getName(), tag.getHandle()));
                out.write(NEXT_LINE);
            }
            out.flush();
        } catch (IOException e) {
            srv.log(Level.SEVERE, "Failure sending command history to client");
        }
    }

    @TelnetCommand(command = "clientIpSet", usage = "clientIpSet", help = "Show all supported Client IP.")
    public void clientIpSet(TelnetClient cli, TelnetServer srv) {
        Set<String> clientIpSet = tagFactory.getClientIpSet();
        try {
            BufferedWriter out = cli.getOut();
            for (String ip : clientIpSet) {
                out.write(ip);
                out.write(NEXT_LINE);
            }
            out.flush();
        } catch (IOException e) {
            srv.log(Level.SEVERE, "Failure sending command history to client");
        }
    }
}
