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
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

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
                .apiInfo(apiInfo()).select().apis(RequestHandlerSelectors.basePackage("com.golfzon.lastspacezbe"))
                .paths(predicate).apis(RequestHandlerSelectors.any()).build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("SPACEZ").description("3조/사무공간 렌탈서비스 최종프로젝트 API 문서입니다.").version("0.0.1").build();
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
        return getDocket("사원", Predicates.or(PathSelectors.regex("/emp.*")));
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
