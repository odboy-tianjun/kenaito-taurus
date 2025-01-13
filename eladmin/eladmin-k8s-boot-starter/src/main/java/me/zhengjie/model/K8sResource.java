package me.zhengjie.model;

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
