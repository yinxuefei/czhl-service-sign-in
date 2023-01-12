
package com.gdczhl.saas.config;

import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.*;

/**
 * @author jkguo
 */
@Configuration
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfiguration {

    @Value("${swagger.application.name}")
    private String applicationName;
    @Value("${swagger.application.name}")
    private String applicationVersion;
    @Value("${swagger.application.description}")
    private String applicationDescription;

    /**
     * 外部平台接口
     *
     * @return
     */
    @Bean(value = "externalApi")
    public Docket externalApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("external")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gdczhl.saas.controller.external"))
                .paths(PathSelectors.ant("/**/external/**"))
                .build()
                // 支持的通讯协议集合
                .protocols(newHashSet("https", "http"))

                // 授权信息设置，必要的header token等认证信息
                .securitySchemes(securitySchemes())

                // 授权信息全局应用
                .securityContexts(securityContexts())
                ;
    }

    /**
     * 小程序接口
     *
     * @return
     */
    @Bean(value = "weappApi")
    public Docket weappApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("weapp")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gdczhl.saas.controller.weapp"))
                .paths(PathSelectors.ant("/**/weapp/**"))
                .build()
                // 授权信息设置，必要的header token等认证信息
                .securitySchemes(securitySchemes())
                // 授权信息全局应用
                .securityContexts(securityContexts())
                // 支持的通讯协议集合
                .protocols(newHashSet("https", "http"))
                ;
    }

    /**
     * feign接口
     *
     * @return
     */
    @Bean(value = "feignApi")
    public Docket feignApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("feign")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gdczhl.saas.controller.feign"))
                .paths(PathSelectors.ant("/**/feign/**"))
                .build()
                // 授权信息设置，必要的header token等认证信息
                .securitySchemes(securitySchemes())
                // 授权信息全局应用
                .securityContexts(securityContexts())
                // 支持的通讯协议集合
                .protocols(newHashSet("https", "http"))
                ;
    }

    /**
     * 第三方平台接口
     *
     * @return
     */
    @Bean(value = "thirdApi")
    public Docket thirdApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("third")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gdczhl.saas.controller.third"))
                .paths(PathSelectors.ant("/**/third/**"))
                .build()
                // 支持的通讯协议集合
                .protocols(newHashSet("https", "http"))
                ;
    }

    /**
     * API 页面上半部分展示信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(applicationName + "接口文档")
                .description(String.format("<div style='font-size:14px;color:red;'>%s</div>", applicationDescription))
                .contact(contact())
                .version(applicationVersion)
                .build();
    }

    /**
     * 设置公司信息
     *
     * @return
     */
    private Contact contact() {
        return new Contact("czhl", "http://www.gdczhl.com/", "hy88830398@189.cn");
    }


    /**
     * 设置授权信息
     */
    private List<SecurityScheme> securitySchemes() {
        ApiKey apiKey = new ApiKey("ACCESS_TOKEN", "ACCESS_TOKEN", In.HEADER.toValue());
        return Collections.singletonList(apiKey);
    }

    /**
     * 授权信息全局应用
     */
    private List<SecurityContext> securityContexts() {
        return Collections.singletonList(
                SecurityContext.builder()
                        .securityReferences(Collections.singletonList(new SecurityReference("ACCESS_TOKEN", new AuthorizationScope[]{new AuthorizationScope("global", "")})))
                        .build()
        );
    }

    @SafeVarargs
    private final <T> Set<T> newHashSet(T... ts) {
        if (ts.length > 0) {
            return new LinkedHashSet<>(Arrays.asList(ts));
        }
        return null;
    }
}
