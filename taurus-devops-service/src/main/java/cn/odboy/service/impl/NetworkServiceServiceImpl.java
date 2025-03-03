package cn.odboy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.odboy.constant.K8sEnvEnum;
import cn.odboy.domain.NetworkService;
import cn.odboy.exception.BadRequestException;
import cn.odboy.mapper.NetworkServiceMapper;
import cn.odboy.mybatisplus.model.PageArgs;
import cn.odboy.model.request.K8sNamespace;
import cn.odboy.model.request.K8sService;
import cn.odboy.repository.K8sNamespaceRepository;
import cn.odboy.repository.K8sServiceRepository;
import cn.odboy.service.NetworkServiceService;
import cn.odboy.util.K8sNameHelper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * k8s网络service 服务实现类
 * </p>
 *
 * @author odboy
 * @since 2024-11-20
 */
@Service
@RequiredArgsConstructor
public class NetworkServiceServiceImpl extends ServiceImpl<NetworkServiceMapper, NetworkService> implements NetworkServiceService {
    private final K8sServiceRepository k8sServiceRepository;
    private final K8sNamespaceRepository k8sNamespaceRepository;

    @Override
    public IPage<NetworkService.QueryPage> queryPage(PageArgs<NetworkService> args) {
        NetworkService body = args.getBody();
        if (body == null) {
            throw new BadRequestException("参数必填");
        }
        if (body.getAppName() == null) {
            throw new BadRequestException("appName必填");
        }
        LambdaQueryWrapper<NetworkService> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NetworkService::getAppName, body.getAppName());
        wrapper.like(StrUtil.isNotBlank(body.getServiceName()), NetworkService::getServiceName, body.getServiceName());
        wrapper.orderByDesc(NetworkService::getId);
        Page<NetworkService> page = page(new Page<>(args.getPage(), args.getPageSize()), wrapper);
        return page.convert(m -> BeanUtil.copyProperties(m, NetworkService.QueryPage.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(NetworkService.CreateArgs args) {
        String appName = args.getAppName().trim();
        String serviceType = args.getServiceType().trim();
        String envCode = args.getEnvCode().trim();
        Integer serviceTargetPort = args.getServiceTargetPort();
        // 服务端口默认80
        Integer servicePort = 80;
        String serviceName = K8sNameHelper.getServiceName(envCode, appName, serviceType, servicePort);
        // 验证有效性
        K8sEnvEnum envEnum = K8sEnvEnum.getByCode(envCode);
        if (envEnum == null) {
            throw new BadRequestException("不支持的环境编码 " + envCode);
        }
        if (existByServiceName(serviceName)) {
            throw new BadRequestException("Service=" + serviceName + " 已存在，请确认后再试");
        }
        // 存库
        NetworkService newService = new NetworkService();
        newService.setEnvCode(envCode);
        newService.setAppName(appName);
        newService.setServiceType(serviceType);
        newService.setServiceName(serviceName);
        newService.setServicePort(servicePort);
        newService.setServiceTargetPort(serviceTargetPort);
        save(newService);
        // 创建k8s namespace
        k8sNamespaceRepository.createNamespace(K8sNamespace.CreateArgs.builder()
                .clusterCode(envEnum.getCode())
                .appName(appName)
                .build());
        // 创建k8s service
        Map<String, String> labelSelector = new HashMap<>(2);
        labelSelector.put("env", envCode);
        labelSelector.put("appName", appName);
        k8sServiceRepository.createService(K8sService.CreateArgs.builder()
                        .clusterCode(envEnum.getCode())
                        .appName(appName)
                        .labelSelector(labelSelector)
                        .port(servicePort)
                        .targetPort(serviceTargetPort)
                .build());
    }

    private boolean existByServiceName(String serviceName) {
        return exists(new LambdaQueryWrapper<NetworkService>()
                .eq(NetworkService::getServiceName, serviceName)
        );
    }
}
