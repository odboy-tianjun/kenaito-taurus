package cn.odboy.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Minio 存储
 *
 * @author odboy
 * @date 2025-01-16
 */
public class MinioStorage {
    /**
     * 对象信息
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Object extends MyObject {
        private String fileName;
        private String suffix;
        private String fileType;
        private long fileSize;
        private String objectName;
        private String eTag;
        private String versionId;
        private String bucketName;
        private String region;
    }
}
