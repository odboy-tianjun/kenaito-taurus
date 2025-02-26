package cn.odboy.util;

/**
 * k8s资源命名助手
 *
 * @author odboy
 * @date 2024-11-20
 */
public class K8sNameHelper {
    public static String getServiceName(String envCode, String appName, String serviceType, Integer servicePort) {
        return String.format("%s-%s-%s-svc-%s", appName, envCode, serviceType, servicePort);
    }

    public static String getIngressName(String envCode, String appName, String networkType) {
        return String.format("%s-%s-%s-ing", appName, envCode, networkType);
    }
}
