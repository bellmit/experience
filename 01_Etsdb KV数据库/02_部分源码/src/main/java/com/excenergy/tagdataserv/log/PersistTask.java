package com.excenergy.tagdataserv.log;

import com.excenergy.tagdataserv.TV;

import java.util.Map;
import java.util.Set;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-09-06
 */
public class PersistTask {
    private final String fileName;
    private final int version;
    private Map<Integer, Set<TV>> tvMap;

    public PersistTask(String fileName, int version, Map<Integer, Set<TV>> tvMap) {
        this.fileName = fileName;
        this.version = version;
        this.tvMap = tvMap;
    }

    public int getVersion() {
        return version;
    }

    public String getFileName() {
        return fileName;
    }

    public Map<Integer, Set<TV>> getTvMap() {
        return tvMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PersistTask that = (PersistTask) o;

        if (version != that.version) {
            return false;
        }
        if (fileName != null ? !fileName.equals(that.fileName) : that.fileName != null) {
            return false;
        }
        if (tvMap != null ? !tvMap.equals(that.tvMap) : that.tvMap != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = fileName != null ? fileName.hashCode() : 0;
        result = 31 * result + version;
        result = 31 * result + (tvMap != null ? tvMap.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PersistTask{" +
                "fileName='" + fileName + '\'' +
                ", version=" + version +
                ", tvMap=" + tvMap +
                '}';
    }
}
