package me.zhengjie.constant;

/**
 * 分支命名规则
 *
 * @author odboy
 * @date 2024-11-15
 */
public interface BranchNameConst {
    /**
     * release_{迭代名称拼音}_{版本号，格式: yyyyMMddHHmmss}
     */
    String RELEASE = "release_%s_%s";

    /**
     * release_{迭代名称拼音}_{版本号，格式: yyyyMMddHHmmss}
     */
    String FEATURE = "feature_%s_%s";
}
