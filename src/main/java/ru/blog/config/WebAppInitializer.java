package ru.blog.config;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{AppConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{WebConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        String uploadTempDir = System.getProperty("java.io.tmpdir");
        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
                uploadTempDir, 5 * 1024 * 1024, 10 * 1024 * 1024, 0
        );
        registration.setMultipartConfig(multipartConfigElement);
    }
}
