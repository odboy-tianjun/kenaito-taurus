package cn.odboy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.odboy.constant.ContainerdClusterConfigEnvStatusEnum;
import cn.odboy.constant.GitlabBizConst;
import cn.odboy.context.K8sConfigHelper;
import cn.odboy.context.K8sHealthChecker;
import cn.odboy.domain.ContainerdClusterConfig;
import cn.odboy.infra.exception.BadRequestException;
import cn.odboy.constant.K8sEnvEnum;
import cn.odboy.model.MetaOption;
import cn.odboy.model.PageArgs;
import cn.odboy.mapper.ContainerdClusterConfigMapper;
import cn.odboy.service.ContainerdClusterConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.kubernetes.client.openapi.ApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * <p>
 * k8s容器集群配置 服务实现类
 * </p>
 *
 * @author odboy
 * @since 2024-11-15
 */
@Service
@RequiredArgsConstructor
public class ContainerdClusterConfigServiceImpl extends ServiceImpl<ContainerdClusterConfigMapper, ContainerdClusterConfig> implements ContainerdClusterConfigService {
    private final K8sHealthChecker k8sHealthChecker;
    private final K8sConfigHelper k8sConfigHelper;

    @Override
    public IPage<ContainerdClusterConfig.QueryPage> queryPage(PageArgs<ContainerdClusterConfig> args) {
        LambdaQueryWrapper<ContainerdClusterConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ContainerdClusterConfig::getId);
        Page<ContainerdClusterConfig> page = page(new Page<>(1, 100), wrapper);
        return page.convert(m -> BeanUtil.copyProperties(m, ContainerdClusterConfig.QueryPage.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(ContainerdClusterConfig.CreateArgs args) {
        String configContent = args.getConfigContent();
        try {
            //        字符串必须以小写字母开头。
//        字符串中间可以包含小写字母和数字。
//        - 符号可以出现多次，并且 - 的左右两边都必须是小写字母或数字
            if (!Pattern.compile(GitlabBizConst.REGEX_APP_NAME).matcher(args.getClusterCode()).matches()) {
                throw new BadRequestException("集群编码格式不正确, 只能由小写字母、数字与符号-组成");
            }
            ApiClient apiClient = k8sConfigHelper.loadFormContent(configContent);
            k8sHealthChecker.checkConfigContent(apiClient);
            K8sEnvEnum k8sEnvEnum = K8sEnvEnum.getByCode(args.getEnvCode());
            if (k8sEnvEnum == null) {
                throw new BadRequestException("不支持的环境参数 " + args.getEnvCode());
            }
            if (existByCode(args.getEnvCode(), args.getClusterCode())) {
                throw new BadRequestException("配置已存在，请确认后再试");
            }
            ContainerdClusterConfig newConfig = new ContainerdClusterConfig();
            newConfig.setEnvCode(args.getEnvCode());
            newConfig.setClusterCode(args.getClusterCode());
            newConfig.setClusterName(args.getClusterName());
            newConfig.setConfigContent(args.getConfigContent());
            newConfig.setEnvStatus(ContainerdClusterConfigEnvStatusEnum.HEALTH.getCode());
            save(newConfig);
        } catch (Exception e) {
            throw new BadRequestException("健康检查失败，无效配置文件内容，请确认后再试");
        }
    }

    private boolean existByCode(String envCode, String clusterCode) {
        return exists(new LambdaQueryWrapper<ContainerdClusterConfig>()
                .eq(ContainerdClusterConfig::getEnvCode, envCode)
                .eq(ContainerdClusterConfig::getClusterCode, clusterCode)
        );
    }

    @Override
    public List<MetaOption> queryEnvList() {
        List<MetaOption> result = new ArrayList<>();
        for (K8sEnvEnum level : K8sEnvEnum.values()) {
            MetaOption option = new MetaOption();
            option.setLabel(level.getDesc());
            option.setValue(level.getCode());
            result.add(option);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modify(ContainerdClusterConfig.ModifyArgs args) {
        ContainerdClusterConfig localClusterConfig = getById(args.getId());
        if (localClusterConfig == null) {
            throw new BadRequestException("操作失败，请刷新后再试");
        }
        if (localClusterConfig.getConfigContent().equals(args.getConfigContent())) {
            throw new BadRequestException("操作失败，文件内容未变更");
        }
        String configContent = args.getConfigContent();
        try {
            ApiClient apiClient = k8sConfigHelper.loadFormContent(configContent);
            k8sHealthChecker.checkConfigContent(apiClient);
            K8sEnvEnum k8sEnvEnum = K8sEnvEnum.getByCode(localClusterConfig.getEnvCode());
            if (k8sEnvEnum == null) {
                throw new BadRequestException("不支持的环境参数 " + localClusterConfig.getEnvCode());
            }
            ContainerdClusterConfig newConfig = new ContainerdClusterConfig();
            newConfig.setId(localClusterConfig.getId());
            newConfig.setConfigContent(args.getConfigContent());
            newConfig.setEnvStatus(ContainerdClusterConfigEnvStatusEnum.HEALTH.getCode());
            updateById(newConfig);
        } catch (Exception e) {
            throw new BadRequestException("无效配置文件内容，健康检查失败，请确认后再试");
        }
    }

    @Override
    public ContainerdClusterConfig getByEnvCode(String envCode) {
        return selectByEnvCode(envCode);
    }

    @Override
    public void modifyStatus(Long id, ContainerdClusterConfigEnvStatusEnum containerdClusterConfigEnvStatusEnum) {
        ContainerdClusterConfig updRecord = new ContainerdClusterConfig();
        updRecord.setId(id);
        updRecord.setEnvStatus(containerdClusterConfigEnvStatusEnum.getCode());
        updateById(updRecord);
    }

    @Override
    public List<ContainerdClusterConfig> queryHealthConfigList() {
        return list(new LambdaQueryWrapper<ContainerdClusterConfig>()
                .eq(ContainerdClusterConfig::getEnvStatus, ContainerdClusterConfigEnvStatusEnum.HEALTH.getCode())
        );
    }

    @Override
    public void remove(ContainerdClusterConfig.RemoveArgs args) {
        removeById(args.getId());
    }

    private ContainerdClusterConfig selectByEnvCode(String envCode) {
        return getOne(new LambdaQueryWrapper<ContainerdClusterConfig>()
                .eq(ContainerdClusterConfig::getEnvCode, envCode)
        );
    }
}
