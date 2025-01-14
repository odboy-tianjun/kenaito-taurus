package cn.odboy.util;

/**
 * dry-run状态转换
 *
 * @author odboy
 * @date 2025-01-13
 */
public class K8sDryRunUtil {
    public static String transferState(Boolean isDryRun) {
        if (isDryRun == null) {
            return null;
        }
        return isDryRun ? "All" : null;
    }
}
