package org.jclouds.demo.tweetstore.config;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Inject;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.jclouds.demo.tweetstore.controller.AddTweetsController;
import org.jclouds.demo.tweetstore.controller.StoreTweetsController;
import org.jclouds.demo.tweetstore.functions.ServiceToStoredTweetStatuses;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.SimpleServletHandlerAdapter;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import com.google.common.collect.Maps;

/**
 * Creates servlets (using resources from the {@link SpringAppConfig}) and mappings.
 * 
 * @author Andrew Phillips
 * @see SpringAppConfig
 */
@Configuration
public class SpringServletConfig implements ServletConfigAware {
    private ServletConfig servletConfig;
    
    @Inject
    private SpringAppConfig appConfig;
    
    @Bean
    public StoreTweetsController storeTweetsController() {
        StoreTweetsController controller = new StoreTweetsController(appConfig.providerTypeToBlobStoreMap, 
                appConfig.container, appConfig.twitterClient);
        injectServletConfig(controller);        
        return controller;
    }

    @Bean
    public AddTweetsController addTweetsController() {
        AddTweetsController controller = new AddTweetsController(appConfig.providerTypeToBlobStoreMap,
                serviceToStoredTweetStatuses());
        injectServletConfig(controller);
        return controller;
    }
    
    private void injectServletConfig(Servlet servlet) {
        checkNotNull(servletConfig);
        try {
            servlet.init(servletConfig);
        } catch (ServletException exception) {
            throw new BeanCreationException("Unable to instantiate " + servlet, exception);
        }
    }

    @Bean
    ServiceToStoredTweetStatuses serviceToStoredTweetStatuses() {
        return new ServiceToStoredTweetStatuses(appConfig.providerTypeToBlobStoreMap,
                appConfig.container);
    }
    
    @Bean
    public HandlerMapping handlerMapping() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        Map<String, Object> urlMap = Maps.newHashMapWithExpectedSize(2);
        urlMap.put("/store/*", storeTweetsController());
        urlMap.put("/tweets/*", addTweetsController());
        mapping.setUrlMap(urlMap);
        /*
         * "/store" and "/tweets" are part of the servlet mapping and thus stripped
         * by the mapping if using default settings.
         */
        mapping.setAlwaysUseFullPath(true);
        return mapping;
    }
    
    @Bean
    public HandlerAdapter servletHandlerAdapter() {
        return new SimpleServletHandlerAdapter();
    }

    /* (non-Javadoc)
     * @see org.springframework.web.context.ServletConfigAware#setServletConfig(javax.servlet.ServletConfig)
     */
    @Override
    public void setServletConfig(ServletConfig servletConfig) {
        this.servletConfig = servletConfig;
    }
}
