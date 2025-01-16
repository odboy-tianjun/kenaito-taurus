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
package cn.odboy.context;

import cn.odboy.infra.exception.BadRequestException;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * dingtalk 客户端认证
 *
 * @author odboy
 * @date 2025-01-13
 */
@Slf4j
public class MinioClientAdmin {
    @Autowired
    private MinioProperties minioProperties;

    public MinioClient auth() throws Exception {
        try {
            MinioClient client = MinioClient.builder()
                    .endpoint(minioProperties.getEndpoint())
                    .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                    .build();
            log.info("构建Minio客户端成功");
            return client;
        } catch (Exception e) {
            log.error("构建Minio客户端失败", e);
            throw new BadRequestException("构建Minio客户端失败");
        }
    }
}
