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

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_BLOBSTORE_CONTEXTBUILDERS;
import static org.jclouds.demo.tweetstore.reference.TweetStoreConstants.PROPERTY_TWEETSTORE_CONTAINER;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextBuilder;
import org.jclouds.demo.tweetstore.controller.AddTweetsController;
import org.jclouds.demo.tweetstore.controller.StoreTweetsController;
import org.jclouds.demo.tweetstore.functions.ServiceToStoredTweetStatuses;
import org.jclouds.gae.config.GoogleAppEngineConfigurationModule;
import org.jclouds.twitter.TwitterClient;
import org.jclouds.twitter.TwitterContextFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.SimpleServletHandlerAdapter;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;
import com.google.appengine.repackaged.com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.io.Closeables;

/**
 * Creates servlets (using resources from the {@link SpringAppConfig}) and mappings.
 * 
 * @author Andrew Phillips
 * @see SpringAppConfig
 */
@Configuration
public class SpringServletConfig extends LoggingConfig implements ServletConfigAware {
    private ServletConfig servletConfig;
    
    private Map<String, BlobStoreContext<?, ?>> providerTypeToBlobStoreMap;
    private TwitterClient twitterClient;
    private String container;

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void initialize() {
        Properties props = loadJCloudsProperties();
        logger.trace("About to initialize members.");
        
        // shared across all blobstores and used to retrieve tweets
        twitterClient = TwitterContextFactory.createContext(props,
                new GoogleAppEngineConfigurationModule()).getApi();

        // common namespace for storing tweets
        container = checkNotNull(props.getProperty(PROPERTY_TWEETSTORE_CONTAINER),
                                 PROPERTY_TWEETSTORE_CONTAINER);
        ImmutableList<String> contextBuilderClassNames = ImmutableList.<String>of(
                checkNotNull(props.getProperty(PROPERTY_BLOBSTORE_CONTEXTBUILDERS),
                             PROPERTY_BLOBSTORE_CONTEXTBUILDERS)
                .split(","));

        // instantiate and store references to all blobstores by provider name
        providerTypeToBlobStoreMap = Maps.newHashMap();
        for (String className : contextBuilderClassNames) {
            Class<BlobStoreContextBuilder<?, ?>> builderClass;
            Constructor<BlobStoreContextBuilder<?, ?>> constructor;
            String name;
            BlobStoreContext<?, ?> context;
            try {
                builderClass = (Class<BlobStoreContextBuilder<?, ?>>) Class.forName(className);
                name = builderClass.getSimpleName().replaceAll("BlobStoreContextBuilder", "");
                constructor = builderClass.getConstructor(Properties.class);
                context = constructor.newInstance(props)
                          .withModules(new GoogleAppEngineConfigurationModule())
                          .buildContext();
            } catch (Exception e) {
                throw new RuntimeException("error instantiating " + className, e);
            }
            providerTypeToBlobStoreMap.put(name, context);
        }

        // get a queue for submitting store tweet requests
        Queue queue = QueueFactory.getQueue("twitter");
        // submit a job to store tweets for each configured blobstore
        for (String name : providerTypeToBlobStoreMap.keySet()) {
            queue.add(url("/store/do").header("context", name).method(Method.GET));
        }
        logger.trace("Members initialized. TwitterClient: '%s', container: '%s', provider types: '%s'",
                twitterClient, container, providerTypeToBlobStoreMap.keySet());      
    }
    
    private Properties loadJCloudsProperties() {
        logger.trace("About to read properties from '%s'", "/WEB-INF/jclouds.properties");
        Properties props = new Properties();
        InputStream input = servletConfig.getServletContext()
                            .getResourceAsStream("/WEB-INF/jclouds.properties");
        try {
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            Closeables.closeQuietly(input);
        }
        logger.trace("Properties successfully read.");
        return props;
    }
    
    @Bean
    public StoreTweetsController storeTweetsController() {
        StoreTweetsController controller = new StoreTweetsController(providerTypeToBlobStoreMap, 
                container, twitterClient);
        injectServletConfig(controller);        
        return controller;
    }

    @Bean
    public AddTweetsController addTweetsController() {
        AddTweetsController controller = new AddTweetsController(providerTypeToBlobStoreMap,
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
        return new ServiceToStoredTweetStatuses(providerTypeToBlobStoreMap, container);
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
    
    @PreDestroy
    public void destroy() throws Exception {
        logger.trace("About to close contexts.");
        for (BlobStoreContext<?, ?> context : providerTypeToBlobStoreMap.values()) {
            context.close();
        }
        logger.trace("Contexts closed.");
    }    

    /* (non-Javadoc)
     * @see org.springframework.web.context.ServletConfigAware#setServletConfig(javax.servlet.ServletConfig)
     */
    @Override
    public void setServletConfig(ServletConfig servletConfig) {
        this.servletConfig = servletConfig;
    }
}
