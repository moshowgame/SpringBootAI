package com.softdev.system.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/statics/**").addResourceLocations("classpath:/statics/");
    }
//    @Bean
//    public FilterRegistrationBean xssFilterRegistration() {
//        FilterRegistrationBean registration = new FilterRegistrationBean();
//        registration.setDispatcherTypes(DispatcherType.REQUEST);
//        registration.setFilter(new XssFilter());
//        registration.addUrlPatterns("/*");
//        registration.setName("xssFilter");
//        registration.setOrder(Integer.MAX_VALUE);
//        return registration;
//    }

    // @Override
    // public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    //     converters.clear();
    //     //FastJsonHttpMessageConverter
    //     FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();

    //     List<MediaType> fastMediaTypes = new ArrayList<>();
    //     fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
    //     fastConverter.setSupportedMediaTypes(fastMediaTypes);

    //     FastJsonConfig fastJsonConfig = new FastJsonConfig();
    //     fastJsonConfig.setCharset(StandardCharsets.UTF_8);
    //     fastConverter.setFastJsonConfig(fastJsonConfig);

    //     //StringHttpMessageConverter
    //     StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
    //     stringConverter.setDefaultCharset(StandardCharsets.UTF_8);
    //     stringConverter.setSupportedMediaTypes(fastMediaTypes);
    //     converters.add(stringConverter);
    //     converters.add(fastConverter);
    // }
    /**
     * FASTJSON2升级 by https://zhengkai.blog.csdn.net/
     * https://blog.csdn.net/moshowgame/article/details/138013669
     */
//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
//        //自定义配置...
//        FastJsonConfig config = new FastJsonConfig();
//        config.setDateFormat("yyyy-MM-dd HH:mm:ss");
//        config.setReaderFeatures(JSONReader.Feature.FieldBased, JSONReader.Feature.SupportArrayToBean);
//        config.setWriterFeatures(JSONWriter.Feature.WriteMapNullValue, JSONWriter.Feature.PrettyFormat);
//        converter.setFastJsonConfig(config);
//        converter.setDefaultCharset(StandardCharsets.UTF_8);
//        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
//        converters.add(0, converter);
//    }

}
