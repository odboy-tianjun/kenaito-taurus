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

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.odboy.constant.MinioConst;
import cn.odboy.context.MinioClientAdmin;
import cn.odboy.context.MinioProperties;
import cn.odboy.infra.exception.BadRequestException;
import cn.odboy.model.MinioStorage;
import cn.odboy.util.RedisUtil;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Minio Bucket's Object
 *
 * @author odboy
 * @date 2025-01-16
 */
@Slf4j
@Component
public class MinioObjectRepository {
    private static final String CACHE_KEY = "minio:file:previewUrl:";
    @Autowired
    private MinioClientAdmin minioClientAdmin;
    @Autowired
    private MinioProperties minioProperties;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取region下的bucketName中的对象
     *
     * @param region     地域编码
     * @param bucketName bucket名称
     * @return /
     */
    public List<MinioStorage.Object> listObjects(String region, String bucketName) {
        List<MinioStorage.Object> result = new ArrayList<>();
        try (MinioClient client = minioClientAdmin.auth()) {
            Iterable<Result<Item>> iterables = client.listObjects(ListObjectsArgs.builder()
                    .region(region)
                    .bucket(bucketName)
                    .build());
            for (Result<Item> iterable : iterables) {
                Item item = iterable.get();
                MinioStorage.Object record = new MinioStorage.Object();
                record.setFileName(item.objectName());
                String suffix = FileUtil.getSuffix(item.objectName());
                record.setSuffix(suffix);
                record.setFileType(cn.odboy.util.FileUtil.getFileType(suffix));
                record.setFileSize(item.size());
                record.setObjectName(item.objectName());
                record.setETag(item.etag());
                record.setVersionId(item.versionId());
                record.setBucketName(bucketName);
                record.setRegion(region);
                result.add(record);
            }
            return result;
        } catch (Exception e) {
            log.error("获取所有对象失败");
            return new ArrayList<>();
        }
    }

    /**
     * 获取默认region下的bucketName中的对象
     *
     * @param bucketName bucket名称
     * @return /
     */
    public List<MinioStorage.Object> listObjects(String bucketName) {
        return listObjects(MinioConst.DEFAULT_REGION, bucketName);
    }

    /**
     * 删除region下的bucketName中的对象
     *
     * @param region     地域编码
     * @param bucketName bucket名称
     * @param object     对象相对路径，例如: d20250116/test.txt
     */
    public void deleteObject(String region, String bucketName, String object) {
        try (MinioClient client = minioClientAdmin.auth()) {
            client.removeObject(RemoveObjectArgs.builder()
                    .region(region)
                    .bucket(bucketName)
                    .object(object)
                    .build());
        } catch (Exception e) {
            log.error("删除对象失败", e);
            throw new BadRequestException("删除对象失败");
        }
    }

    /**
     * 删除默认region下的bucketName中的对象
     *
     * @param bucketName bucket名称
     * @param object     对象相对路径，例如: d20250116/test.txt
     */
    public void deleteObject(String bucketName, String object) {
        try (MinioClient client = minioClientAdmin.auth()) {
            client.removeObject(RemoveObjectArgs.builder()
                    .region(MinioConst.DEFAULT_REGION)
                    .bucket(bucketName)
                    .object(object)
                    .build());
        } catch (Exception e) {
            log.error("删除对象失败", e);
            throw new BadRequestException("删除对象失败");
        }
    }

    /**
     * 上传对象到region中bucketName内
     *
     * @param region     地域编码
     * @param bucketName bucket名称
     * @param object     对象相对路径，例如: d20250116/test.txt
     * @param file       上传的文件
     */
    public ObjectWriteResponse uploadObject(String region, String bucketName, String object, MultipartFile file) {
        try (MinioClient client = minioClientAdmin.auth()) {
            PutObjectArgs uploadObjectArgs = PutObjectArgs.builder()
                    .region(region)
                    .bucket(bucketName)
                    .object(object)
                    .stream(file.getInputStream(), file.getInputStream().available(), -1)
                    .contentType(FileTypeUtil.getType(file.getInputStream()))
                    .build();
            ObjectWriteResponse response = client.putObject(uploadObjectArgs);
            log.info("上传对象到region={}，bucket={}成功", region, bucketName);
            return response;
        } catch (Exception e) {
            String message = "上传对象到region={}，bucket={}失败";
            log.error(message, region, bucketName, e);
            try {
                deleteObject(region, bucketName, object);
            } catch (Exception e2) {
                log.error(e2.getMessage(), e2);
            }
            throw new BadRequestException(message);
        }
    }

    /**
     * 上传对象到默认region中bucketName内
     *
     * @param bucketName bucket名称
     * @param object     对象相对路径，例如: d20250116/test.txt
     * @param file       上传的文件
     */
    public ObjectWriteResponse uploadObject(String bucketName, String object, MultipartFile file) {
        return uploadObject(MinioConst.DEFAULT_REGION, bucketName, object, file);
    }

    /**
     * 获取对象预览地址
     *
     * @param region     地域编码
     * @param bucketName bucket名称
     * @param object     对象相对路径，例如: d20250116/test.txt
     */
    public String getFilePreviewUrl(String region, String bucketName, String object) {
        try (MinioClient client = minioClientAdmin.auth()) {
            String redisKey = CACHE_KEY + Base64Encoder.encode(object, StandardCharsets.UTF_8);
            String filePreviewUrl = redisUtil.get(redisKey, String.class);
            if (filePreviewUrl != null) {
                return filePreviewUrl;
            }
            GetPresignedObjectUrlArgs getPresignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .expiry(minioProperties.getShareExpireTime(), TimeUnit.DAYS)
                    .region(region)
                    .bucket(bucketName)
                    .object(object)
                    .build();
            String presignedObjectUrl = client.getPresignedObjectUrl(getPresignedObjectUrlArgs);
            redisUtil.set(redisKey, presignedObjectUrl, Math.round(minioProperties.getShareExpireTime() * 24 * 0.8), TimeUnit.HOURS);
            return presignedObjectUrl;
        } catch (Exception e) {
            log.error("获取对象分享链接失败", e);
            throw new BadRequestException("获取对象分享链接失败");
        }
    }

    /**
     * 获取默认region中对象预览地址
     *
     * @param bucketName bucket名称
     * @param object     对象相对路径，例如: d20250116/test.txt
     */
    public String getFilePreviewUrl(String bucketName, String object) {
        return getFilePreviewUrl(MinioConst.DEFAULT_REGION, bucketName, object);
    }

    /**
     * 获取region中bucketName内的对象数据流分片
     *
     * @param region     地域编码
     * @param bucketName bucket名称
     * @param object     对象相对路径，例如: d20250116/test.txt
     */
    public InputStream getObjectOffsetInputStream(String region, String bucketName, String object, long offset, long length) {
        try (MinioClient client = minioClientAdmin.auth()) {
            return client.getObject(
                    GetObjectArgs.builder()
                            .region(region)
                            .bucket(bucketName)
                            .object(object)
                            .offset(offset)
                            .length(length)
                            .build());
        } catch (Exception e) {
            log.error("获取对象InputStream失败", e);
            throw new BadRequestException("获取对象InputStream失败");
        }
    }

    /**
     * 获取默认region中bucketName内的对象数据流分片
     *
     * @param bucketName bucket名称
     * @param object     对象相对路径，例如: d20250116/test.txt
     */
    public InputStream getObjectOffsetInputStream(String bucketName, String object, long offset, long length) {
        return getObjectOffsetInputStream(MinioConst.DEFAULT_REGION, bucketName, offset, length);
    }

    /**
     * 获取region中bucketName内的对象数据流
     *
     * @param region     地域编码
     * @param bucketName bucket名称
     * @param object     对象相对路径，例如: d20250116/test.txt
     */
    public InputStream getObjectInputStream(String region, String bucketName, String object) {
        try (MinioClient client = minioClientAdmin.auth()) {
            return client.getObject(GetObjectArgs.builder()
                    .region(region)
                    .bucket(bucketName)
                    .object(object)
                    .build());
        } catch (Exception e) {
            log.error("获取对象InputStream失败", e);
            throw new BadRequestException("获取对象InputStream失败");
        }
    }

    /**
     * 获取默认region中bucketName内的对象数据流
     *
     * @param bucketName bucket名称
     * @param object     对象相对路径，例如: d20250116/test.txt
     */
    public InputStream getObjectInputStream(String bucketName, String object) {
        return getObjectInputStream(MinioConst.DEFAULT_REGION, bucketName);
    }

    /**
     * 获取region中bucketName内的对象
     *
     * @param region     地域编码
     * @param bucketName bucket名称
     * @param object     对象相对路径，例如: d20250116/test.txt
     * @return /
     */
    public GetObjectResponse getByObjectName(String region, String bucketName, String object) {
        try (MinioClient client = minioClientAdmin.auth()) {
            return client.getObject(GetObjectArgs.builder()
                    .region(region)
                    .bucket(bucketName)
                    .object(object)
                    .build());
        } catch (Exception e) {
            log.error("获取对象失败", e);
            throw new BadRequestException("获取对象失败");
        }
    }

    /**
     * 获取默认region中bucketName内的对象
     *
     * @param bucketName bucket名称
     * @param object     对象相对路径，例如: d20250116/test.txt
     * @return /
     */
    public GetObjectResponse getByObjectName(String bucketName, String object) {
        try (MinioClient client = minioClientAdmin.auth()) {
            return client.getObject(GetObjectArgs.builder()
                    .region(MinioConst.DEFAULT_REGION)
                    .bucket(bucketName)
                    .object(object)
                    .build());
        } catch (Exception e) {
            log.error("获取对象失败", e);
            throw new BadRequestException("获取对象失败");
        }
    }
}
