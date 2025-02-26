package cn.odboy.util;

import cn.hutool.core.util.ReUtil;
import cn.hutool.extra.pinyin.PinyinUtil;

import java.util.List;

public class IterationHelper {
    public static String getPinyin(String text) {
        // 正则表达式匹配中英文字符
        String regex = "[\\u4e00-\\u9fa5a-zA-Z]";
        // 使用ReUtil.findAll方法提取所有匹配的字符
        List<String> chars = ReUtil.findAll(regex, text, 0);
        String join = String.join("", chars);
        return PinyinUtil.getPinyin(join, "");
    }

    public static void main(String[] args) {
        String text = "Hello, 你好！This is a test. 测试内容你好！This is a tes你好！This is a tes你好！This is a tes你好！This is a tes你好！This is a tes你好！This is a tes。";
        System.err.println(getPinyin(text));
    }
}
