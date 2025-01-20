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
package cn.odboy.util;

/**
 * Dingtalk内容字体颜色
 *
 * @author odboy
 * @date 2025-02-10
 */
public class DingTalkTextColorUtil {
    /**
     * 获取Error样式内容
     *
     * @param text 文字内容
     */
    public static String getColorErrorText(String text) {
        return "<font color=\"#ED4014\"> " + text + " </font>";
    }

    /**
     * 获取Danger样式内容
     *
     * @param text 文字内容
     */
    public static String getColorDangerText(String text) {
        return "<font color=\"#F56C6C\"> " + text + " </font>";
    }

    /**
     * 获取Success样式内容
     *
     * @param text 文字内容
     */
    public static String getColorSuccessText(String text) {
        return "<font color=\"#67C23A\"> " + text + " </font>";
    }

    /**
     * 获取Blue样式内容
     *
     * @param text 文字内容
     */
    public static String getBlueColorText(String text) {
        return "<font color=\"#409EFF\"> " + text + " </font>";
    }

    /**
     * 获取Warning样式内容
     *
     * @param text 文字内容
     */
    public static String getColorWarningText(String text) {
        return "<font color=\"#E6A23C\"> " + text + " </font>";
    }

    /**
     * 获取Info样式内容
     *
     * @param text 文字内容
     */
    public static String getColorInfoText(String text) {
        return "<font color=\"#909399\"> " + text + " </font>";
    }

    /**
     * 获取主要文字样式内容
     *
     * @param text 文字内容
     */
    public static String getColorMainFontText(String text) {
        return "<font color=\"#303133\"> " + text + " </font>";
    }

    /**
     * 获取常规文字样式内容
     *
     * @param text 文字内容
     */
    public static String getColorConventionFontText(String text) {
        return "<font color=\"#303133\"> " + text + " </font>";
    }

    /**
     * 获取次要文字样式内容
     *
     * @param text 文字内容
     */
    public static String getColorSecondaryFontText(String text) {
        return "<font color=\"#303133\"> " + text + " </font>";
    }

    /**
     * 获取占位文字样式内容
     *
     * @param text 文字内容
     */
    public static String getColorSeizeSeatFontText(String text) {
        return "<font color=\"#303133\"> " + text + " </font>";
    }

    /**
     * 获取基础黑色文字样式内容
     *
     * @param text 文字内容
     */
    public static String getColorBaseBlackFontText(String text) {
        return "<font color=\"#000000\"> " + text + " </font>";
    }

    /**
     * 获取换行符
     */
    public static String getBreLine() {
        return "  /n  ";
    }
}
