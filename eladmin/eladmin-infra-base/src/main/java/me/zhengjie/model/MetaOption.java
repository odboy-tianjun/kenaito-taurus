package me.zhengjie.model;

import lombok.*;

import java.util.Map;

/**
 * 元数据选项
 *
 * @author odboy
 * @date 2025-01-13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MetaOption extends MyObject {
    private String label;
    private String value;
    private Map<String, Object> ext;
}
