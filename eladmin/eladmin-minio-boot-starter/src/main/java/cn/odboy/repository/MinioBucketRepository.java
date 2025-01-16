/*
 *  Copyright 2022-2025 Tian Jun
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package cn.odboy.repository;

import cn.odboy.constant.MinioConst;
import cn.odboy.context.MinioClientAdmin;
import cn.odboy.infra.exception.BadRequestException;
import cn.odboy.infra.exception.util.MessageFormatterUtil;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.messages.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Minio Bucket
 *
 * @author odboy
 * @date 2025-01-16
 */
@Slf4j
@Component
public class MinioBucketRepository {
    @Autowired
    private MinioClientAdmin minioClientAdmin;

    /**
     * 创建Bucket
     *
     * @param region     地域编码
     * @param bucketName bucket名称
     */
    public void createBucket(String region, String bucketName) {
        if (exitsBucket(region, bucketName)) {
            log.info("region={}，bucket={}已存在，不会执行任何操作", region, bucketName);
            return;
        }
        try (MinioClient client = minioClientAdmin.auth()) {
            client.makeBucket(MakeBucketArgs.builder()
                    .region(region)
                    .bucket(bucketName)
                    .objectLock(false)
                    .build());
        } catch (Exception e) {
            String message = "创建region={}，bucket={}失败";
            log.error(message, region, bucketName, e);
            throw new BadRequestException(MessageFormatterUtil.format(message, region, bucketName));
        }
    }

    /**
     * 判断Bucket是否存在
     *
     * @param region     地域编码
     * @param bucketName bucket名称
     */
    public boolean exitsBucket(String region, String bucketName) {
        try (MinioClient client = minioClientAdmin.auth()) {
            return client.bucketExists(BucketExistsArgs.builder()
                    .region(region)
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            log.error("查询bucket是否存在失败", e);
            return false;
        }
    }

    /**
     * 判断default地域中是否存在
     *
     * @param bucketName bucket名称
     */
    public boolean exitsBucket(String bucketName) {
        return exitsBucket(MinioConst.DEFAULT_REGION, bucketName);
    }

    /**
     * 获取所有的bucket
     *
     * @return /
     */
    public List<Bucket> listBuckets() {
        try (MinioClient client = minioClientAdmin.auth()) {
            return client.listBuckets();
        } catch (Exception e) {
            log.error("获取所有的bucket失败", e);
            return new ArrayList<>();
        }
    }
}
