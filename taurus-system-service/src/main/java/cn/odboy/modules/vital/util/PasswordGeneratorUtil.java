/*
 *  Copyright 2021-2025 Tian Jun
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
package cn.odboy.modules.vital.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.odboy.infra.exception.BadRequestException;
import cn.odboy.modules.vital.domain.PwdInfo;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 密码生成器
 *
 * @author odboy
 * @date 2022-11-10
 */
@Component
public class PasswordGeneratorUtil {
    /**
     * 小写字母
     */
    private static final List<String> LOWER_LETTERS = new ArrayList<>();
    /**
     * 大写字母
     */
    private static final List<String> UPPER_LETTERS = new ArrayList<>();
    /**
     * 数字
     */
    private static final List<String> NUMBERS = new ArrayList<>();
    /**
     * 符号
     */
    private static final List<String> SYMBOLS = new ArrayList<>();

    @PostConstruct
    private void initData() {
        for (char i = 'a'; i <= 'z'; i++) {
            LOWER_LETTERS.add(i + "");
        }
        for (char i = 'A'; i <= 'Z'; i++) {
            UPPER_LETTERS.add(i + "");
        }
        for (char i = '0'; i <= '9'; i++) {
            NUMBERS.add(i + "");
        }
        String originSymbolStr = "~!@#$%^&*()-=_+[],.?;:";
        SYMBOLS.addAll(List.of(originSymbolStr.split("")));
    }

    /**
     * 查找最长的连续串
     */
    public String getContinuityStr(String str, char startChar, char endChar) {
        int max = 0, count = 0, end = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) >= startChar && str.charAt(i) <= endChar) {
                count++;
                if (max < count) {
                    max = count;
                    end = i;
                }
            } else {
                count = 0;
            }
        }
        return str.substring(end - max + 1, end + 1);
    }

    /**
     * 找连续最长的数字串
     *
     * @param str 待验证字符串
     * @return /
     */
    public String getContinuityNumberStr(String str) {
        return getContinuityStr(str, '0', '9');
    }

    /**
     * 找连续最长的字母串
     *
     * @param str 待验证字符串
     * @return /
     */
    public String getContinuityLetterStr(String str) {
        return getContinuityStr(str, 'A', 'z');
    }

    public PwdInfo.PasswordContent generator(PwdInfo.GeneratorRuleArgs args) {
        // 1、验证规则
        checkAndThrowError(args);
        // 2、解析规则
        int minLength = args.getLengthRange().get(0);
        int maxLength = args.getLengthRange().get(1);
        if (minLength > maxLength) {
            int tempLength = minLength;
            minLength = maxLength;
            maxLength = tempLength;
        }
        Integer defineLength = args.getDefineLength();
        if (defineLength < minLength || defineLength > maxLength) {
            throw new BadRequestException("参数defineLength(生成的密码长度)配置异常");
        }
        // 不包含（非必须）
        boolean notIncludeStatus = false;
        List<String> notIncludeSplitList = new ArrayList<>();
        String notIncludeSymbols = args.getNotIncludeSymbols();
        String[] notIncludeSplitStr = notIncludeSymbols.split("");
        if (notIncludeSplitStr.length > 0) {
            notIncludeSplitList = Arrays.stream(notIncludeSplitStr).filter(Objects::nonNull).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(notIncludeSplitList)) {
                notIncludeStatus = true;
            }
        }
        // 拼接需要的字符
        List<String> finalList = new ArrayList<>();
        List<String> includeTypeList = args.getIncludeType();
        for (String includeName : includeTypeList) {
            if ("symbols".equals(includeName)) {
                finalList.addAll(SYMBOLS);
                continue;
            }
            if ("numbers".equals(includeName)) {
                finalList.addAll(NUMBERS);
                continue;
            }
            if ("lower_letters".equals(includeName)) {
                finalList.addAll(LOWER_LETTERS);
                continue;
            }
            if ("upper_letters".equals(includeName)) {
                finalList.addAll(UPPER_LETTERS);
            }
        }
        // 生成有效的密码
        boolean isBuild = true;
        int randomSize = finalList.size() - 1;
        String finalPassword;
        do {
            List<String> tempPassword = new ArrayList<>(defineLength);
            while (tempPassword.size() < defineLength) {
                String tempChar = finalList.get(RandomUtil.randomInt(0, randomSize));
                if (notIncludeStatus) {
                    if (!notIncludeSplitList.contains(tempChar)) {
                        tempPassword.add(tempChar);
                    }
                } else {
                    tempPassword.add(tempChar);
                }
            }
            finalPassword = String.join("", tempPassword);
            if (getContinuityLetterStr(finalPassword).length() >= 6 || getContinuityNumberStr(finalPassword).length() >= 6) {
                continue;
            }
            isBuild = false;
        } while (isBuild);
        PwdInfo.PasswordContent passwordContent = new PwdInfo.PasswordContent();
        passwordContent.setContent(finalPassword);
        passwordContent.setCrackDuration(getCrackDuration(finalPassword));
        return passwordContent;
    }

    private String getCrackDuration(String finalPassword) {
        BigDecimal duration = BigDecimal.ONE;
        String[] charArray = finalPassword.split("");
        for (String c : charArray) {
            if (SYMBOLS.contains(c)) {
                duration = duration.multiply(BigDecimal.valueOf(SYMBOLS.size()));
                continue;
            }
            if (NUMBERS.contains(c)) {
                duration = duration.multiply(BigDecimal.valueOf(NUMBERS.size()));
                continue;
            }
            if (LOWER_LETTERS.contains(c)) {
                duration = duration.multiply(BigDecimal.valueOf(LOWER_LETTERS.size()));
                continue;
            }
            if (UPPER_LETTERS.contains(c)) {
                duration = duration.multiply(BigDecimal.valueOf(UPPER_LETTERS.size()));
            }
        }
        // 每个重复一次的概率
        duration = duration.divide(BigDecimal.valueOf(finalPassword.length()), 6, RoundingMode.FLOOR);
        duration = duration.divide(BigDecimal.valueOf(1000), 6, RoundingMode.FLOOR).divide(BigDecimal.valueOf(60), 0, RoundingMode.FLOOR);
        BigDecimal durationTenBillionYear = BigDecimal.valueOf(60L * 24 * 365).multiply(BigDecimal.valueOf(10000000000000L));
        if (duration.compareTo(durationTenBillionYear) >= 0) {
            duration = duration.divide(durationTenBillionYear, 0, RoundingMode.FLOOR);
            return duration.doubleValue() + " 兆年";
        }
        BigDecimal durationBillionYear = BigDecimal.valueOf(60L * 24 * 365).multiply(BigDecimal.valueOf(100000000));
        if (duration.compareTo(durationBillionYear) >= 0) {
            duration = duration.divide(durationBillionYear, 0, RoundingMode.FLOOR);
            return duration.doubleValue() + " 亿年";
        }
        BigDecimal durationMillionYear = BigDecimal.valueOf(60L * 24 * 365).multiply(BigDecimal.valueOf(1000000));
        if (duration.compareTo(durationMillionYear) >= 0) {
            duration = duration.divide(durationMillionYear, 0, RoundingMode.FLOOR);
            return duration.doubleValue() + " 百万年";
        }
        BigDecimal durationYear = BigDecimal.valueOf(60 * 24 * 365);
        if (duration.compareTo(durationYear) >= 0) {
            duration = duration.divide(durationYear, 0, RoundingMode.FLOOR);
            return duration.doubleValue() + " 年";
        }
        BigDecimal durationDay = BigDecimal.valueOf(60 * 24);
        if (duration.compareTo(durationDay) >= 0) {
            duration = duration.divide(durationDay, 0, RoundingMode.FLOOR);
            return duration.doubleValue() + " 天";
        }
        BigDecimal durationHour = BigDecimal.valueOf(60);
        if (duration.compareTo(durationHour) >= 0) {
            duration = duration.divide(durationHour, 0, RoundingMode.FLOOR);
            return duration.doubleValue() + " 小时";
        }
        return duration.doubleValue() + " 分钟";
    }

    private void checkAndThrowError(PwdInfo.GeneratorRuleArgs args) {
        List<Integer> lengthRange = args.getLengthRange();
        if (lengthRange == null) {
            throw new BadRequestException("参数lengthRange(密码长度范围)为空");
        }
        int rangeSize = 2;
        if (lengthRange.size() != rangeSize) {
            throw new BadRequestException(String.format("lengthRange(密码长度范围)的数组大小应为2, 当前大小: %s", lengthRange.size()));
        }
        List<String> includeType = args.getIncludeType();
        if (includeType == null) {
            throw new BadRequestException("参数includeType(包含字符)为空");
        }
        if (includeType.isEmpty() || includeType.size() > 4) {
            throw new BadRequestException(String.format("参数includeType(密码长度范围)的数组大小应为1~4, 当前大小: %s", includeType.size()));
        }
        Integer defineLength = args.getDefineLength();
        if (defineLength == null) {
            throw new BadRequestException("参数defineLength(生成密码长度)为空");
        }
        if (defineLength < lengthRange.get(0)) {
            throw new BadRequestException(String.format("参数defineLength(生成密码长度)最小值应为%s, 当前值: %s", lengthRange.get(0), defineLength));
        }
        if (defineLength > lengthRange.get(1)) {
            throw new BadRequestException(String.format("参数defineLength(生成密码长度)最大值应为%s, 当前值: %s", lengthRange.get(1), defineLength));
        }
    }
//    public static String generator(Map<String, String> rules) {
//        // 解析规则
//        int minLength, maxLength;
//        String lengthRegex = rules.getOrDefault("lengthRange", null);
//        if (StrUtil.isBlank(lengthRegex)) {
//            throw new RuntimeException("规则lengthRange不存在");
//        }
//        String[] lengths = lengthRegex.split("-");
//        if (lengths.length != 2) {
//            throw new RuntimeException("规则lengthRange格式异常");
//        }
//        try {
//            minLength = Integer.parseInt(lengths[0]);
//            maxLength = Integer.parseInt(lengths[1]);
//            if (minLength >= maxLength) {
//                throw new RuntimeException("规则length格式异常");
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("规则length格式异常, " + e.getMessage());
//        }
//        int defineLength;
//        String defineLengthValue = rules.getOrDefault("defineLength", null);
//        if (StrUtil.isBlank(defineLengthValue)) {
//            throw new RuntimeException("规则defineLength不存在");
//        }
//        try {
//            defineLength = Integer.parseInt(defineLengthValue);
//            if (defineLength == 0) {
//                throw new RuntimeException("规则defineLength格式异常");
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("规则defineLength不存在, " + e.getMessage());
//        }
//        String includeRegex = rules.getOrDefault("includeType", null);
//        if (StrUtil.isBlank(lengthRegex)) {
//            throw new RuntimeException("规则includeType不存在");
//        }
//        String[] includes = includeRegex.split(",");
//        if (includes.length == 0) {
//            throw new RuntimeException("规则includeType格式异常");
//        }
//        // 不包含（非必须）
//        boolean notIncludeStatus = false;
//        List<String> notIncludeSplitList = new ArrayList<>();
//        String notIncludeRegex = rules.getOrDefault("notIncludeSymbols", null);
//        if (StrUtil.isNotBlank(notIncludeRegex)) {
//            String[] notIncludeSplitStr = notIncludeRegex.split("");
//            if (notIncludeSplitStr.length > 0) {
//                notIncludeSplitList = Arrays.stream(notIncludeSplitStr).filter(Objects::nonNull).collect(Collectors.toList());
//                if (CollUtil.isNotEmpty(notIncludeSplitList)) {
//                    notIncludeStatus = true;
//                }
//            }
//        }
//        // 拼接需要的字符
//        List<String> finalList = new ArrayList<>();
//        for (String includeName : includes) {
//            if ("symbols".equals(includeName)) {
//                finalList.addAll(SYMBOLS);
//                continue;
//            }
//            if ("numbers".equals(includeName)) {
//                finalList.addAll(NUMBERS);
//                continue;
//            }
//            if ("lower_letters".equals(includeName)) {
//                finalList.addAll(LOWER_LETTERS);
//                continue;
//            }
//            if ("upper_letters".equals(includeName)) {
//                finalList.addAll(UPPER_LETTERS);
//            }
//        }
//        // 生成有效的密码
//        boolean isBuild = true;
//        int randomSize = finalList.size() - 1;
//        String finalPassword;
//        do {
//            List<String> tempPassword = new ArrayList<>(defineLength);
//            while (tempPassword.size() < defineLength) {
//                String tempChar = finalList.get(RandomUtil.randomInt(0, randomSize));
//                if (notIncludeStatus) {
//                    if (!notIncludeSplitList.contains(tempChar)) {
//                        tempPassword.add(tempChar);
//                    }
//                } else {
//                    tempPassword.add(tempChar);
//                }
//            }
//            finalPassword = String.join("", tempPassword);
//            if (getContinuityLetterStr(finalPassword).length() >= 6 || getContinuityNumberStr(finalPassword).length() >= 6) {
//                continue;
//            }
//            isBuild = false;
//        } while (isBuild);
//        return finalPassword;
//    }
    /**
     * QQ的密码规则
     * 不能包括空格（默认不能包含）<br/>
     * 长度为8-16个字符<br/>
     * 必须包含字母、数字、符号中至少两种<br/>
     * 请勿输入连续、重复6位以上字母或数字（默认不能包含）, 如: abcdefg、1111111、0123456
     */
//    public static void main(String[] args) {
//        TimeInterval timer = DateUtil.timer();
//        PasswordGeneratorRuleArgs rules = new PasswordGeneratorRuleArgs();
//        rules.setLengthRange(new ArrayList<>() {{
//            add(8);
//            add(50);
//        }});
//        rules.setIncludeType(new ArrayList<>() {{
//            add("symbols");
//            add("numbers");
//            add("lower_letters");
//            add("upper_letters");
//        }});
//        rules.setDefineLength(50);
//        rules.setNotIncludeSymbols("()~{[}}!,");
//
//        String password = generator(rules);
//        System.err.println("generator耗时: " + timer.interval() + " ms, 密码为: " + password + ", 长度为: " + password.length());
//    }
}
