package cn.odboy.constant;

/**
 * 常用正则表达式
 *
 * @author odboy
 * @date 2024-09-13
 */
public interface RegexConst {
    // 正则表达式，用于匹配中国大陆手机号码
    String PHONE_NUMBER = "^1[3-9]\\d{9}$";
    // 小写字母和下划线
    String LOW_CHAR_LINE = "^[a-z_]+$";
}
