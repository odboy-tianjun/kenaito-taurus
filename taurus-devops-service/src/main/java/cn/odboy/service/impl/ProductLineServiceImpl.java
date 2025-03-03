package cn.odboy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.odboy.constant.ProductLineUserRoleEnum;
import cn.odboy.domain.ProductLine;
import cn.odboy.domain.ProductLineUser;
import cn.odboy.mapper.ProductLineMapper;
import cn.odboy.common.model.MetaOptionModel;
import cn.odboy.mybatisplus.model.PageArgs;
import cn.odboy.service.ProductLineAppService;
import cn.odboy.service.ProductLineService;
import cn.odboy.service.ProductLineUserService;
import cn.odboy.common.util.CollUtil;
import cn.odboy.util.ProductLineHelper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 产品线 服务实现类
 * </p>
 *
 * @author odboy
 * @since 2024-09-13
 */
@Service
@AllArgsConstructor
public class ProductLineServiceImpl extends ServiceImpl<ProductLineMapper, ProductLine> implements ProductLineService {
    private ProductLineUserService productLineUserService;
    private ProductLineAppService productLineAppService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(ProductLine.CreateArgs args) {
        Long pid = args.getPid() == null ? 0L : args.getPid();
        productLineAppService.isBindApp(pid);

        ProductLine record = new ProductLine();
        record.setName(args.getName());
        record.setDescription(args.getDescription());
        record.setPid(pid);
        save(record);

        Long newProductLineId = record.getId();

        List<ProductLineUser> addList = new ArrayList<>();
        List<Long> adminList = args.getAdminList();
        List<Long> peList = args.getPeList();
        if (CollUtil.isNotEmpty(adminList)) {
            for (Long userId : adminList) {
                ProductLineUser base = new ProductLineUser();
                base.setProductLineId(newProductLineId);
                base.setUserId(userId);
                base.setRoleCode(ProductLineUserRoleEnum.ADMIN.getCode());
                addList.add(base);
            }
        }
        if (CollUtil.isNotEmpty(peList)) {
            for (Long userId : peList) {
                ProductLineUser base = new ProductLineUser();
                base.setProductLineId(newProductLineId);
                base.setUserId(userId);
                base.setRoleCode(ProductLineUserRoleEnum.PE.getCode());
                addList.add(base);
            }
        }
        if (CollUtil.isNotEmpty(addList)) {
            productLineUserService.saveBatch(addList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modify(ProductLine.ModifyArgs args) {
        ProductLine record = new ProductLine();
        record.setId(args.getId());
        record.setName(args.getName());
        record.setDescription(args.getDescription());
        updateById(record);
        productLineUserService.removeByProductLineId(record.getId());
        List<ProductLineUser> addList = new ArrayList<>();
        List<Long> adminList = args.getAdminList();
        List<Long> peList = args.getPeList();
        if (CollUtil.isNotEmpty(adminList)) {
            for (Long userId : adminList) {
                ProductLineUser base = new ProductLineUser();
                base.setProductLineId(record.getId());
                base.setUserId(userId);
                base.setRoleCode(ProductLineUserRoleEnum.ADMIN.getCode());
                addList.add(base);
            }
        }
        if (CollUtil.isNotEmpty(peList)) {
            for (Long userId : peList) {
                ProductLineUser base = new ProductLineUser();
                base.setProductLineId(record.getId());
                base.setUserId(userId);
                base.setRoleCode(ProductLineUserRoleEnum.PE.getCode());
                addList.add(base);
            }
        }
        if (CollUtil.isNotEmpty(addList)) {
            productLineUserService.saveBatch(addList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(ProductLine.RemoveByIdsArgs args) {
        removeBatchByIds(args.getIds());
        productLineUserService.removeBatchByProductLineIds(args.getIds());
    }

    @Override
    public List<ProductLine.TreeNode> getTree() {
        return ProductLineHelper.convertToTree(list(new LambdaQueryWrapper<ProductLine>().orderByDesc(ProductLine::getId)));
    }

    @Override
    public IPage<ProductLine.QueryPage> queryPage(PageArgs<ProductLine> args) {
        ProductLine body = args.getBody();
        LambdaQueryWrapper<ProductLine> wrapper = new LambdaQueryWrapper<>();
        if (body != null) {
            wrapper.like(StrUtil.isNotBlank(body.getName()), ProductLine::getName, body.getName());
            wrapper.eq(body.getPid() != null, ProductLine::getPid, body.getPid());
            wrapper.orderByDesc(ProductLine::getId);
        }
        Page<ProductLine> page = page(new Page<>(args.getPage(), args.getPageSize()), wrapper);
        return page.convert(m -> {
            ProductLine.QueryPage queryPage = BeanUtil.copyProperties(m, ProductLine.QueryPage.class);
            queryPage.setAdminList(productLineUserService.queryProductLineUserList(m.getId(), ProductLineUserRoleEnum.ADMIN.getCode()));
            queryPage.setPeList(productLineUserService.queryProductLineUserList(m.getId(), ProductLineUserRoleEnum.PE.getCode()));
            return queryPage;
        });
    }

    @Override
    public List<MetaOptionModel> queryList() {
        return list().stream().map(m -> {
            MetaOptionModel selectOption = new MetaOptionModel();
            selectOption.setLabel(String.valueOf(m.getId()));
            selectOption.setValue(m.getName());
            return selectOption;
        }).collect(Collectors.toList());
    }

    @Override
    public List<MetaOptionModel> queryPathList() {
        List<MetaOptionModel> result = new ArrayList<>(10);
        List<ProductLine> productLines = list(new LambdaQueryWrapper<ProductLine>()
                .orderByDesc(ProductLine::getId)
        );

        Map<Long, ProductLine> menuMap = new HashMap<>(5);
        for (ProductLine productLine : productLines) {
            menuMap.put(productLine.getId(), productLine);
        }

        for (ProductLine productLine : productLines) {
            List<String> paths = ProductLineHelper.generatePath(productLine, menuMap);
            MetaOptionModel option = new MetaOptionModel();
            option.setLabel(String.join("/", paths));
            option.setValue(String.valueOf(productLine.getId()));
            result.add(option);
        }

        result.sort(Comparator.comparing(MetaOptionModel::getLabel));
        return result;
    }
}
