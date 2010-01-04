/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
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
public class SpringServletConfig extends LoggingConfig implements ServletConfigAware {
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
        logger.trace("About to inject servlet config '%s'", servletConfig);
        
        try {
            servlet.init(checkNotNull(servletConfig));
        } catch (ServletException exception) {
            throw new BeanCreationException("Unable to instantiate " + servlet, exception);
        }
        
        logger.trace("Successfully injected servlet config.");
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
