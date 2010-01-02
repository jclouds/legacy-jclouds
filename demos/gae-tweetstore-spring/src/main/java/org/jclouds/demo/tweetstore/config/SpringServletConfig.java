package org.jclouds.demo.tweetstore.config;

import java.util.Map;

import javax.inject.Inject;

import org.jclouds.demo.tweetstore.controller.AddTweetsController;
import org.jclouds.demo.tweetstore.controller.StoreTweetsController;
import org.jclouds.demo.tweetstore.functions.ServiceToStoredTweetStatuses;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.SimpleServletHandlerAdapter;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import com.google.common.collect.Maps;

/**
 * Creates servlets,(using resources from the {@link SpringAppConfig}) and mappings.
 * 
 * @author Andrew Phillips
 */
@Configuration
public class SpringServletConfig {
    @Inject
    private SpringAppConfig appConfig;
    
    @Bean
    public StoreTweetsController storeTweetsController() {
        return new StoreTweetsController(appConfig.providerTypeToBlobStoreMap, 
                appConfig.container, appConfig.twitterClient);
    }

    @Bean
    public AddTweetsController addTweetsController() {
        return new AddTweetsController(appConfig.providerTypeToBlobStoreMap,
                serviceToStoredTweetStatuses());
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
}
