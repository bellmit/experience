package com.excenergy.tagdataserv;

import java.util.ArrayList;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-10-17
 */
public class TagStorePool {
    private Application app;
    private TagFactory tagFactory;
    private final ArrayList<TagStore> tagStores = new ArrayList<>();

    public TagStorePool(Application app) {
        this.app = app;
        this.tagFactory = app.getTagFactory();
    }

    public TagStore getTagStore(Integer handle) {
        tagFactory.checkHandle(handle);
        synchronized (tagStores) {
            while (handle >= tagStores.size()) {
                tagStores.add(null);
            }

            TagStore tagStore = tagStores.get(handle);
            if (tagStore == null) {
                tagStore = new TagStore(handle, app);
                tagStores.set(handle, tagStore);
            }
            return tagStore;
        }
    }
}
