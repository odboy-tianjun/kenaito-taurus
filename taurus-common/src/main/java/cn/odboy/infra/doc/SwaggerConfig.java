package cn.odboy.infra.doc;/*
 *  Copyright 2019-2020 Zheng Jie
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

import cn.odboy.util.AnonTagUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * api页面 /doc.html
 *
 * @author Zheng Jie
 * @date 2018-11-23
 */
@Configuration
@EnableSwagger2
@RequiredArgsConstructor
public class SwaggerConfig {
    @Value("${server.servlet.context-path:}")
    private String apiPath;
    @Value("${jwt.header}")
    private String tokenHeader;
    @Value("${swagger.enabled}")
    private Boolean enabled;
    private final ApplicationContext applicationContext;

    @Bean
    @SuppressWarnings("all")
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(enabled)
                .pathMapping("/")
                .apiInfo(apiInfo())
                .select()
                .paths(PathSelectors.regex("^(?!/error).*"))
                .paths(PathSelectors.any())
                .build()
                //添加登陆认证
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .description("一个简单且易上手的 Spring boot 后台管理框架")
                .title("ELADMIN 接口文档")
                .version("1.1")
                .build();
    }

    private List<SecurityScheme> securitySchemes() {
        //设置请求头信息
        List<SecurityScheme> securitySchemes = new ArrayList<>();
        ApiKey apiKey = new ApiKey(tokenHeader, tokenHeader, "header");
        securitySchemes.add(apiKey);
        return securitySchemes;
    }

    private List<SecurityContext> securityContexts() {
        //设置需要登录认证的路径
        List<SecurityContext> securityContexts = new ArrayList<>();
        securityContexts.add(getContextByPath());
        return securityContexts;
    }

    private SecurityContext getContextByPath() {
        Set<String> urls = AnonTagUtil.getAllAnonymousUrl(applicationContext);
        urls = urls.stream().filter(url -> !"/".equals(url)).collect(Collectors.toSet());
        String regExp = "^(?!" + apiPath + String.join("|" + apiPath, urls) + ").*$";
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .operationSelector(o -> o.requestMappingPattern()
                        // 排除不需要认证的接口
                        .matches(regExp))
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        List<SecurityReference> securityReferences = new ArrayList<>();
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        securityReferences.add(new SecurityReference(tokenHeader, authorizationScopes));
        return securityReferences;
    }
}