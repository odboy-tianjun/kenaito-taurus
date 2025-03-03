package cn.odboy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.odboy.constant.K8sEnvEnum;
import cn.odboy.constant.NetworkTypeEnum;
import cn.odboy.constant.NetworkTypeSuffixEnum;
import cn.odboy.domain.NetworkIngress;
import cn.odboy.domain.NetworkService;
import cn.odboy.mapper.NetworkIngressMapper;
import cn.odboy.mybatisplus.model.PageArgs;
import cn.odboy.model.request.K8sIngress;
import cn.odboy.model.request.K8sNamespace;
import cn.odboy.repository.K8sIngressRepository;
import cn.odboy.repository.K8sNamespaceRepository;
import cn.odboy.service.NetworkIngressService;
import cn.odboy.service.NetworkServiceService;
import cn.odboy.util.HostHelper;
import cn.odboy.util.K8sNameHelper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.BadRequestException;

/**
 * <p>
 * devops网络ingress-nginx 服务实现类
 * </p>
 *
 * @author odboy
 * @since 2024-11-21
 */
@Service
@RequiredArgsConstructor
public class NetworkIngressServiceImpl extends ServiceImpl<NetworkIngressMapper, NetworkIngress> implements NetworkIngressService {
    private final K8sNamespaceRepository k8sNamespaceRepository;
    private final K8sIngressRepository k8sIngressRepository;
    private final NetworkServiceService networkServiceService;

    @Override
    public IPage<NetworkIngress.QueryPage> queryPage(PageArgs<NetworkIngress> args) {
        NetworkIngress body = args.getBody();
        if (body == null) {
            throw new BadRequestException("参数必填");
        }
        if (body.getAppName() == null) {
            throw new BadRequestException("appName必填");
        }
        LambdaQueryWrapper<NetworkIngress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NetworkIngress::getAppName, body.getAppName());
        wrapper.like(StrUtil.isNotBlank(body.getIngressName()), NetworkIngress::getIngressName, body.getIngressName());
        wrapper.orderByDesc(NetworkIngress::getId);
        Page<NetworkIngress> page = page(new Page<>(args.getPage(), args.getPageSize()), wrapper);
        return page.convert(m -> {
            NetworkIngress.QueryPage currItem = BeanUtil.copyProperties(m, NetworkIngress.QueryPage.class);
            currItem.setServiceInfo(networkServiceService.getById(currItem.getServiceId()));
            return currItem;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(NetworkIngress.CreateArgs args) {
        Long serviceId = args.getServiceId();
        NetworkService localNetworkService = networkServiceService.getById(serviceId);
        if (localNetworkService == null) {
            throw new BadRequestException("无效的Service，请刷新后再试");
        }
        String envCode = args.getEnvCode().trim();
        String appName = args.getAppName().trim();
        String hostname = args.getHostname();
        String networkType = args.getNetworkType().trim();
        String path = args.getPath().trim();
        String ingressName = K8sNameHelper.getIngressName(envCode, appName, networkType);
        // 验证有效性
        K8sEnvEnum envEnum = K8sEnvEnum.getByCode(envCode);
        if (envEnum == null) {
            throw new BadRequestException("不支持的环境编码 " + envCode);
        }
        // 没有后缀加后缀
        if (NetworkTypeEnum.INNER.getCode().equals(networkType)) {
            if (!hostname.endsWith(NetworkTypeSuffixEnum.INNER.getCode())) {
                hostname = hostname + NetworkTypeSuffixEnum.INNER.getCode();
            }
        } else if (NetworkTypeEnum.OUTER.getCode().equals(networkType)) {
            if (!hostname.endsWith(NetworkTypeSuffixEnum.OUTER.getCode())) {
                hostname = hostname + NetworkTypeSuffixEnum.OUTER.getCode();
            }
        } else {
            throw new BadRequestException("不支持的网络类型 " + networkType);
        }
        if (!HostHelper.isDomain(hostname)) {
            throw new BadRequestException("无效的域名");
        }
        if (existByHostname(hostname)) {
            throw new BadRequestException("域名 " + hostname + " 已存在");
        }
        if (existByIngressName(ingressName)) {
            throw new BadRequestException("Ingress=" + ingressName + " 已存在，请确认后再试");
        }
        // 存库
        NetworkIngress networkIngress = new NetworkIngress();
        networkIngress.setEnvCode(envCode);
        networkIngress.setAppName(appName);
        networkIngress.setIngressName(ingressName);
        networkIngress.setHostname(hostname);
        networkIngress.setNetworkType(networkType);
        networkIngress.setServiceId(serviceId);
        networkIngress.setPath(path);
        save(networkIngress);
        // 创建k8s namespace
        k8sNamespaceRepository.createNamespace(K8sNamespace.CreateArgs.builder()
                .clusterCode(envCode)
                .appName(appName)
                .build());
        // 创建k8s Ingress
        k8sIngressRepository.createIngress(K8sIngress.CreateArgs.builder()
                .clusterCode(envCode)
                .appName(appName)
                .serviceName(localNetworkService.getServiceName())
                .servicePort(localNetworkService.getServicePort())
                .hostname(hostname)
                .build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(NetworkIngress.RemoveArgs args) {
        NetworkIngress networkIngress = getById(args.getId());
        if (networkIngress == null) {
            throw new BadRequestException("未找到当前记录，删除失败");
        }
        removeById(networkIngress.getId());
        k8sIngressRepository.deleteIngress(K8sIngress.DeleteArgs.builder()
                .clusterCode(networkIngress.getEnvCode())
                .appName(networkIngress.getAppName())
                .build());
    }

    private boolean existByHostname(String hostname) {
        return exists(new LambdaQueryWrapper<NetworkIngress>()
                .eq(NetworkIngress::getHostname, hostname)
        );
    }

    private boolean existByIngressName(String ingressName) {
        return exists(new LambdaQueryWrapper<NetworkIngress>()
                .eq(NetworkIngress::getIngressName, ingressName)
        );
    }
}
