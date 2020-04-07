package com.excenergy.tagdataserv;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-09-09
 */
public class Bootstrap {
    public static void main(String[] args) {
        TagDataServer server = TagDataServer.getInstance();
        server.start();
    }
}
