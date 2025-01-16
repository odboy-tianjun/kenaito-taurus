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
package cn.odboy.model.response;

import com.alibaba.fastjson.annotation.JSONField;
import io.kubernetes.client.openapi.models.V1NamespaceSpec;
import io.kubernetes.client.openapi.models.V1NamespaceStatus;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PodCondition;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * K8s 资源响应信息
 *
 * @author odboy
 * @date 2024-10-01
 */
public class K8sResource {
    @Data
    public static class ActionExceptionBody {
        private String kind;
        private String apiVersion;
        private String status;
        private String message;
        private String reason;
        private Details details;
        private int code;

        @Data
        public static class Details {
            private String name;
            private String kind;
        }
    }

    @Data
    public static class Namespace {
        private V1NamespaceSpec spec;
        private String kind;
        private V1ObjectMeta metadata;
        private V1NamespaceStatus status;
    }

    @Data
    public static class Pod {
        private String name;
        private String ip;
        @JSONField(format = "yyyy-MM-dd HH:mm:ss")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date createTime;
        @JSONField(format = "yyyy-MM-dd HH:mm:ss")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date deleteTime;
        @JSONField(format = "yyyy-MM-dd HH:mm:ss")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date startTime;
        private Map<String, String> labels;
        private String namespace;
        private String resourceVersion;
        private String restartPolicy;
        private Integer restartCount;
        private String image;
        private String status;
        private String qosClass;
        /**
         * pod 流转状态
         */
        private List<V1PodCondition> conditions;
    }
}
