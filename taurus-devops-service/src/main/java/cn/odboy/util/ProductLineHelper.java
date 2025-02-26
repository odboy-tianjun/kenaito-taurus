package cn.odboy.util;

import cn.odboy.domain.ProductLine;

import java.util.*;
import java.util.stream.Collectors;

public class ProductLineHelper {
    public static List<String> generatePath(ProductLine productLine, Map<Long, ProductLine> productLineMap) {
        List<String> path = new ArrayList<>();
        buildPath(productLine, productLineMap, path);
        return path;
    }

    private static void buildPath(ProductLine currentLine, Map<Long, ProductLine> productLineMap, List<String> path) {
        if (currentLine != null) {
            // 将当前菜单名称添加到路径列表的开头
            path.add(0, currentLine.getName());
            ProductLine parentLine = productLineMap.get(currentLine.getPid());
            // 递归处理父菜单
            buildPath(parentLine, productLineMap, path);
        }
    }

    public static List<ProductLine.TreeNode> convertToTree(List<ProductLine> list) {
        List<ProductLine.TreeNode> nodes = list.stream().map(item -> {
            ProductLine.TreeNode treeNode = new ProductLine.TreeNode();
            treeNode.setId(item.getId());
            treeNode.setPid(item.getPid());
            treeNode.setName(item.getName());
            treeNode.setChildren(new ArrayList<>());
            return treeNode;
        }).collect(Collectors.toList());

        Map<Long, ProductLine.TreeNode> idMap = new HashMap<>(10);
        Set<Long> ids = new HashSet<>();

        for (ProductLine.TreeNode item : nodes) {
            idMap.put(item.getId(), item);
        }

        for (ProductLine.TreeNode item : nodes) {
            Long parentId = item.getPid();
            if (parentId == 0L) {
                // 根节点
                continue;
            }
            ProductLine.TreeNode parent = idMap.get(parentId);
            if (parent != null) {
                parent.getChildren().add(item);
                ids.add(item.getId());
            }
        }

        // 找到根节点
        List<ProductLine.TreeNode> rootNodes = new ArrayList<>();
        for (ProductLine.TreeNode item : nodes) {
            if (!ids.contains(item.getId())) {
                rootNodes.add(item);
            }
        }
        return rootNodes;
    }
}
