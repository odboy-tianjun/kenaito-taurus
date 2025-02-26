package cn.odboy.service;

import cn.odboy.domain.ProductLine;
import cn.odboy.model.MetaOption;
import cn.odboy.model.PageArgs;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 产品线 服务类
 * </p>
 *
 * @author odboy
 * @since 2024-09-13
 */
public interface ProductLineService extends IService<ProductLine> {
    /**
     * 创建产品线
     *
     * @param args /
     */
    void create(ProductLine.CreateArgs args);

    /**
     * 修改产品线
     *
     * @param args /
     */
    void modify(ProductLine.ModifyArgs args);

    /**
     * 删除产品线
     *
     * @param args /
     */
    void remove(ProductLine.RemoveByIdsArgs args);

    /**
     * 获取产品树
     *
     * @return /
     */
    List<ProductLine.TreeNode> getTree();

    /**
     * 分页查询产品线
     *
     * @param args /
     * @return /
     */
    IPage<ProductLine.QueryPage> queryPage(PageArgs<ProductLine> args);

    /**
     * 获取产品线列表
     *
     * @return /
     */
    List<MetaOption> queryList();

    /**
     * 获取产品线路径列表
     *
     * @return /
     */
    List<MetaOption> queryPathList();
}
