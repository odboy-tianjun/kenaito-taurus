package cn.odboy.infra.monitor;

import cn.odboy.infra.security.annotation.AnonymousGetMapping;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/health")
@Api(tags = "系统：健康检查接口")
public class HealthCheckController {
    @AnonymousGetMapping(value = "/check")
    public ResponseEntity<?> doCheck() {
        return ResponseEntity.ok().build();
    }
}
