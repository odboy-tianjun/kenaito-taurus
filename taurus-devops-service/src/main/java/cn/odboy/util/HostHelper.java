package cn.odboy.util;

public class HostHelper {
    private static final String DOMAIN_PATTERN = "^([a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}$";
    public static boolean isDomain(String domain){
        if (domain == null || domain.isEmpty()) {
            return false;
        }
        return domain.matches(DOMAIN_PATTERN);
    }
}
