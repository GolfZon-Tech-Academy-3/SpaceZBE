package com.golfzon.lastspacezbe.config;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableSwagger2 // Swagger2 사용하겠다.
@SuppressWarnings("unchecked")
public class SwaggerConfig extends WebMvcConfigurationSupport {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    // swagger 설정.
    public Docket getDocket(String groupName, Predicate<String> predicate) {
        return new Docket(DocumentationType.SWAGGER_2).groupName(groupName)
                .securityContexts(Arrays.asList(securityContext())) //security, jwt
                .securitySchemes(Arrays.asList(apiKey())) //security, jwt
                .apiInfo(apiInfo()).select().apis(RequestHandlerSelectors.basePackage("com.golfzon.lastspacezbe"))
                .paths(predicate).apis(RequestHandlerSelectors.any()).build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("SPACEZ").description("3조/사무공간 렌탈서비스 최종프로젝트 API 문서입니다.").version("0.0.1").build();
    }

    //security,jwt swagger 2.9.2 version 부터 authorize 버튼 생성됨.
    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build();
    }
    //security,jwt
    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("Authorization", authorizationScopes));
    }
    //security,jwt
    private ApiKey apiKey() {
        return new ApiKey("Authorization", "Authorization", "header");
    }

    @Bean
    public UiConfiguration uiConfiguration() {
        return UiConfigurationBuilder.builder().displayRequestDuration(true).validatorUrl("").build();
    }

    // API마다 구분짓기 위한 설정.
    @Bean
    public Docket productApi() {
        return getDocket("회원", Predicates.or(PathSelectors.regex("/member.*")));
    }

    @Bean
    public Docket employeeApi() {
        return getDocket("예약", Predicates.or(PathSelectors.regex("/reservation.*")));
    }

    @Bean
    public Docket searchApi() {
        return getDocket("주문", Predicates.or(PathSelectors.regex("/order.*")));
    }

    @Bean
    public Docket commonApi() {
        return getDocket("공통", Predicates.or(PathSelectors.regex("/test.*")));

    }

    @Bean
    public Docket allApi() {
        return getDocket("전체", Predicates.or(PathSelectors.regex("/*.*")));
    }
}
