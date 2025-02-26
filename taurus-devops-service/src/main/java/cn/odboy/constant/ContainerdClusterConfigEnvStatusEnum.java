package cn.odboy.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 容器环境配置健康状态
 *
 * @author odboy
 * @date 2024-11-16
 */
@Getter
@AllArgsConstructor
public enum ContainerdClusterConfigEnvStatusEnum {
    HEALTH(1, "健康"),
    UN_HEALTH(2, "不健康");

    private final Integer code;
    private final String desc;

    public static ContainerdClusterConfigEnvStatusEnum getByCode(String code) {
        for (ContainerdClusterConfigEnvStatusEnum item : ContainerdClusterConfigEnvStatusEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }

    public static String getDescByCode(String code) {
        for (ContainerdClusterConfigEnvStatusEnum item : ContainerdClusterConfigEnvStatusEnum.values()) {
            if (item.code.equals(code)) {
                return item.getDesc();
            }
        }
        return "";
    }
}
