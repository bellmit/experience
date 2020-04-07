package com.excenergy.tagdataserv;

import com.excenergy.tagmeta.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-08-20
 */
public class TagFactory {
    private static final Logger logger = LoggerFactory.getLogger(TagFactory.class);
    private static TagFactory ourInstance = new TagFactory();

    private Vector<TagChangeListener> tagChangeListeners = new Vector<>();
    private boolean isReady = false;
    private ReadWriteLock lock = new ReentrantReadWriteLock(false);
    private TagMeta tagMeta;
    private final Map<Integer, TSource> sourceMap = new HashMap<>();
    private final Map<Integer, Device> deviceMap = new HashMap<>();
    private final Map<String, TClient> clientMap = new HashMap<>();
    private final Set<String> clientIpSet = new HashSet<>();
    private final Set<String> sourceIpSet = new HashSet<>();
    private final ArrayList<Tag> tagList = new ArrayList<>();
    private final Map<String, Tag> tagMap = new HashMap<>();

    public static TagFactory getInstance() {
        return ourInstance;
    }

    private TagFactory() {
    }

    public void checkHandle(int handle) {
        lock.readLock().lock();
        try {
            if (handle >= tagList.size() || handle < 0 || tagList.get(handle) == null) {
                String code = "E-000002";
                String msg = String.format(TagDataException.getMsg(code), handle);
                if (logger.isErrorEnabled()) {
                    logger.error(msg);
                }
                throw new TagDataException(code, msg);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 根据handle得到Tag，如果没有，抛异常
     *
     * @param handle 位号的ID
     * @return 存在返回Tag，如果不存在，或者范围不对，都抛运行时异常
     */
    public Tag get(int handle) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("get tag handle:%d", handle));
        }
        checkHandle(handle);
        lock.readLock().lock();
        try {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("get tag result:%s", tagList.get(handle)));
            }
            return tagList.get(handle);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 根据handle得到Tag，如果没有，抛异常
     *
     * @param name 位号的name
     * @return 存在并且是公开的，返回Tag，否则返回null
     */
    public Tag get(String name) {
        return get(name, true);
    }

    public Tag get(String name, boolean onlyPublic) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("TagFactory.get: get tag name:%s", name));
        }
        lock.readLock().lock();
        try {
            Tag tag = tagMap.get(name);
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("TagFactory.get: get tag result:%s", tag));
            }

            if (onlyPublic && tag != null && !tag.isPublic()) {
                tag = null;
            }
            return tag;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void put(TagMeta tagMeta) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Put TagMeta:%s", tagMeta));
        }
        if (tagMeta == null) {
            String code = "E-000008";
            String msg = String.format(TagDataException.getMsg(code), "tagMeta", "TagFactory.put");
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new TagDataException(code, msg);
        }
        lock.writeLock().lock();
        try {
            this.tagMeta = tagMeta;

            List<TSource> sourceList = tagMeta.getSourceList();
            sourceMap.clear();
            sourceIpSet.clear();
            deviceMap.clear();
            //                        tagList.clear();
            tagMap.clear();
            for (TSource source : sourceList) {
                sourceMap.put(source.getId(), source);
                sourceIpSet.add(source.getIp());
                if (source.getDeviceList() == null) {
                    continue;
                }
                for (Device device : source.getDeviceList()) {
                    deviceMap.put(device.getId(), device);
                    List<RealTag> rTagList = device.getRealTagList();
                    if (rTagList != null && rTagList.size() > 0) {
                        for (Iterator<RealTag> it = rTagList.iterator(); it.hasNext(); ) {
                            RealTag realTag = it.next();
                            if (realTag == null) {
                                continue;
                            }
                            put(realTag);
                            if (!realTag.isPublic() || !realTag.isValid()) {
                                it.remove();
                            }
                        }
                    }

                    putVTags(device.getVirtualTagList());
                }
            }

            List<TClient> clientList = tagMeta.getClientList();
            clientMap.clear();
            clientIpSet.clear();
            for (TClient c : clientList) {
                clientMap.put(c.getName(), c);
                clientIpSet.add(c.getIp());
            }
        } finally {
            lock.writeLock().unlock();
        }

        for (TagChangeListener listener : tagChangeListeners) {
            listener.tagChanged();
            if (!isReady) {
                listener.tagReady();
            }
        }
        isReady = true;
        if (logger.isInfoEnabled()) {
            logger.info("Tag is ready.");
        }
    }

    private void putVTags(List<VirtualTag> vTagList) {
        if (vTagList == null || vTagList.size() == 0) {
            return;
        }
        for (Iterator<VirtualTag> it = vTagList.iterator(); it.hasNext(); ) {
            VirtualTag vTag = it.next();
            if (vTag == null) {
                continue;
            }
            put(vTag);
            putVTags(vTag.getChildren());
            if (!vTag.isPublic() || !vTag.isValid()) {
                it.remove();
            }
        }
    }

    public void put(Tag tag) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("put Tag:%s", tag));
        }
        if (!tag.isValid()) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Tag is invalid, will not add to cache:%s", tag));
            }
            return;
        }
        lock.writeLock().lock();
        try {
            while (tag.getHandle() >= tagList.size()) {
                tagList.add(null);
            }
            tagList.set(tag.getHandle(), tag);
            if (tag.isPublic()) {
                tagMap.put(tag.getName(), tag);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public TSource getSource(int sourceId) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("getSource ID:%d", sourceId));
        }
        lock.readLock().lock();
        try {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("getSource result:%s", sourceMap.get(sourceId)));
            }
            return sourceMap.get(sourceId);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Device getDevice(int deviceId) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("getSource ID:%d", deviceId));
        }
        lock.readLock().lock();
        try {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("getSource result:%s", deviceMap.get(deviceId)));
            }
            return deviceMap.get(deviceId);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Device getDevice(String name) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("getSource name:%s", name));
        }
        lock.readLock().lock();
        try {
            for (Device device : deviceMap.values()) {
                if (name.equalsIgnoreCase(device.getDesc())) {
                    return device;
                }
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public TClient getClient(String name) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("getClient name:%s", name));
        }
        lock.readLock().lock();
        try {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("getClient result:%s", clientMap.get(name)));
            }
            return clientMap.get(name);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Set<String> getClientIpSet() {
        lock.readLock().lock();
        try {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("getClientIpSet result:%s", clientIpSet));
            }
            return clientIpSet;
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Tag> enumTag(String regex) {
        return enumTag(regex, true);
    }

    public List<Tag> enumTag(String regex, boolean onlyPublic) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("enumTag regex:%s", regex));
        }
        regex = ".*".concat(regex).concat(".*");
        List<Tag> result = new ArrayList<>();
        lock.readLock().lock();
        try {
            for (Tag tag : tagList) {
                if (tag == null) {
                    continue;
                }
                if (onlyPublic && !tag.isPublic()) {
                    continue;
                }
                String name = tag.getName();
                if (name == null) {
                    continue;
                }
                if (name.matches(regex)) {
                    result.add(tag);
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("enumTag result:%s", result));
        }
        return result;
    }

    public List<TSource> enumAll() {
        lock.readLock().lock();
        try {
            List<TSource> sourceList = tagMeta.getSourceList();
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("enumAll result:%s", sourceList));
            }
            return sourceList;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void addTagChangeListener(TagChangeListener listener) {
        if (!tagChangeListeners.contains(listener)) {
            tagChangeListeners.add(listener);
        }
    }

    public Set<String> getSourceIpSet() {
        return sourceIpSet;
    }

    public TSource getSource(String sourceIp) {
        if (sourceIp == null) {
            return null;
        }
        for (TSource tSource : sourceMap.values()) {
            if (tSource == null) {
                continue;
            }
            if (sourceIp.equalsIgnoreCase(tSource.getIp())) {
                return tSource;
            }
        }
        return null;
    }
}
