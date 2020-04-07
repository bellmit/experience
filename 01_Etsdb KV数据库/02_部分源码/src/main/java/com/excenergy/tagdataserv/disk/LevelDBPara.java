package com.excenergy.tagdataserv.disk;

import org.iq80.leveldb.CompressionType;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-10-17
 */
public class LevelDBPara {
    private int blockRestartInterval = 16;
    private CompressionType compressionType = CompressionType.SNAPPY;
    private int writeBufferSize = 4194304;
    private int blockSize = 4096;
    private int cacheSize = 0;
    private boolean createIfMissing = true;
    private boolean errorIfExists = false;
    private int maxOpenFiles = 1000;
    private boolean paranoidChecks = false;
    private boolean verifyChecksums = true;

    public LevelDBPara() {
    }

    public int getBlockRestartInterval() {
        return blockRestartInterval;
    }

    public void setBlockRestartInterval(int blockRestartInterval) {
        this.blockRestartInterval = blockRestartInterval;
    }

    public CompressionType getCompressionType() {
        return compressionType;
    }

    public void setCompressionType(CompressionType compressionType) {
        this.compressionType = compressionType;
    }

    public int getWriteBufferSize() {
        return writeBufferSize;
    }

    public void setWriteBufferSize(int writeBufferSize) {
        this.writeBufferSize = writeBufferSize;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    public boolean isCreateIfMissing() {
        return createIfMissing;
    }

    public void setCreateIfMissing(boolean createIfMissing) {
        this.createIfMissing = createIfMissing;
    }

    public boolean isErrorIfExists() {
        return errorIfExists;
    }

    public void setErrorIfExists(boolean errorIfExists) {
        this.errorIfExists = errorIfExists;
    }

    public int getMaxOpenFiles() {
        return maxOpenFiles;
    }

    public void setMaxOpenFiles(int maxOpenFiles) {
        this.maxOpenFiles = maxOpenFiles;
    }

    public boolean isParanoidChecks() {
        return paranoidChecks;
    }

    public void setParanoidChecks(boolean paranoidChecks) {
        this.paranoidChecks = paranoidChecks;
    }

    public boolean isVerifyChecksums() {
        return verifyChecksums;
    }

    public void setVerifyChecksums(boolean verifyChecksums) {
        this.verifyChecksums = verifyChecksums;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LevelDBPara that = (LevelDBPara) o;

        if (blockRestartInterval != that.blockRestartInterval) {
            return false;
        }
        if (blockSize != that.blockSize) {
            return false;
        }
        if (cacheSize != that.cacheSize) {
            return false;
        }
        if (createIfMissing != that.createIfMissing) {
            return false;
        }
        if (errorIfExists != that.errorIfExists) {
            return false;
        }
        if (maxOpenFiles != that.maxOpenFiles) {
            return false;
        }
        if (paranoidChecks != that.paranoidChecks) {
            return false;
        }
        if (verifyChecksums != that.verifyChecksums) {
            return false;
        }
        if (writeBufferSize != that.writeBufferSize) {
            return false;
        }
        if (compressionType != that.compressionType) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = blockRestartInterval;
        result = 31 * result + (compressionType != null ? compressionType.hashCode() : 0);
        result = 31 * result + writeBufferSize;
        result = 31 * result + blockSize;
        result = 31 * result + cacheSize;
        result = 31 * result + (createIfMissing ? 1 : 0);
        result = 31 * result + (errorIfExists ? 1 : 0);
        result = 31 * result + maxOpenFiles;
        result = 31 * result + (paranoidChecks ? 1 : 0);
        result = 31 * result + (verifyChecksums ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LevelDBPara{" +
                "blockRestartInterval=" + blockRestartInterval +
                ", compressionType=" + compressionType +
                ", writeBufferSize=" + writeBufferSize +
                ", blockSize=" + blockSize +
                ", cacheSize=" + cacheSize +
                ", createIfMissing=" + createIfMissing +
                ", errorIfExists=" + errorIfExists +
                ", maxOpenFiles=" + maxOpenFiles +
                ", paranoidChecks=" + paranoidChecks +
                ", verifyChecksums=" + verifyChecksums +
                '}';
    }
}
