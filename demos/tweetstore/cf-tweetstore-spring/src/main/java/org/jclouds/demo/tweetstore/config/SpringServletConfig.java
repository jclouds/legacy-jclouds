/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.demo.tweetstore.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Predicates.in;
import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Sets.filter;
import static org.jclouds.demo.tweetstore.reference.TweetStoreConstants.PROPERTY_TWEETSTORE_BLOBSTORES;
import static org.jclouds.demo.tweetstore.reference.TweetStoreConstants.PROPERTY_TWEETSTORE_CONTAINER;
import static org.jclouds.demo.tweetstore.reference.TwitterConstants.PROPERTY_TWITTER_ACCESSTOKEN;
import static org.jclouds.demo.tweetstore.reference.TwitterConstants.PROPERTY_TWITTER_ACCESSTOKEN_SECRET;
import static org.jclouds.demo.tweetstore.reference.TwitterConstants.PROPERTY_TWITTER_CONSUMER_KEY;
import static org.jclouds.demo.tweetstore.reference.TwitterConstants.PROPERTY_TWITTER_CONSUMER_SECRET;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextBuilder;
import org.jclouds.demo.paas.PlatformServices;
import org.jclouds.demo.paas.service.taskqueue.TaskQueue;
import org.jclouds.demo.tweetstore.config.util.CredentialsCollector;
import org.jclouds.demo.tweetstore.controller.AddTweetsController;
import org.jclouds.demo.tweetstore.controller.EnqueueStoresController;
import org.jclouds.demo.tweetstore.controller.StoreTweetsController;
import org.jclouds.demo.tweetstore.functions.ServiceToStoredTweetStatuses;
import org.jclouds.logging.Logger;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.SimpleServletHandlerAdapter;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.io.Closeables;
import com.google.inject.Module;

/**
 * Creates servlets (using resources from the {@link SpringAppConfig}) and mappings.
 * 
 * @author Andrew Phillips
 * @see SpringAppConfig
 */
@Configuration
public class SpringServletConfig extends LoggingConfig implements ServletConfigAware {
   public static final String PROPERTY_BLOBSTORE_CONTEXTS = "blobstore.contexts";

   private static final Logger LOGGER = LOGGER_FACTORY.getLogger(SpringServletConfig.class.getName());

   private ServletConfig servletConfig;

   private Map<String, BlobStoreContext<?, ?>> providerTypeToBlobStoreMap;
   private Twitter twitterClient;
   private String container;
   private TaskQueue queue;
   private String baseUrl;

   @PostConstruct
   public void initialize() throws IOException {
      Properties props = loadJCloudsProperties();
      LOGGER.trace("About to initialize members.");

      Set<Module> modules = ImmutableSet.of();
      // shared across all blobstores and used to retrieve tweets
      try {
          twitter4j.conf.Configuration twitterConf = new ConfigurationBuilder()
              .setOAuthConsumerKey(props.getProperty(PROPERTY_TWITTER_CONSUMER_KEY))
              .setOAuthConsumerSecret(props.getProperty(PROPERTY_TWITTER_CONSUMER_SECRET))
              .setOAuthAccessToken(props.getProperty(PROPERTY_TWITTER_ACCESSTOKEN))
              .setOAuthAccessTokenSecret(props.getProperty(PROPERTY_TWITTER_ACCESSTOKEN_SECRET))
              .build();
          twitterClient = new TwitterFactory(twitterConf).getInstance();
      } catch (IllegalArgumentException e) {
          throw new IllegalArgumentException("properties for twitter not configured properly in " + props.toString(), e);
      }
      // common namespace for storing tweets
      container = checkNotNull(props.getProperty(PROPERTY_TWEETSTORE_CONTAINER), PROPERTY_TWEETSTORE_CONTAINER);

      // instantiate and store references to all blobstores by provider name
      providerTypeToBlobStoreMap = Maps.newHashMap();
      for (String hint : getBlobstoreContexts(props)) {
          providerTypeToBlobStoreMap.put(hint, BlobStoreContextBuilder
                  .newBuilder(hint).modules(modules).overrides(props).build());
      }

      // get a queue for submitting store tweet requests and the application's base URL
      PlatformServices platform = PlatformServices.get(servletConfig.getServletContext());
      queue = platform.getTaskQueue("twitter");
      baseUrl = platform.getBaseUrl();

      LOGGER.trace("Members initialized. Twitter: '%s', container: '%s', provider types: '%s'", twitterClient,
            container, providerTypeToBlobStoreMap.keySet());
   }

   private static Iterable<String> getBlobstoreContexts(Properties props) {
       Set<String> contexts = new CredentialsCollector().apply(props).keySet();
       String explicitContexts = props.getProperty(PROPERTY_TWEETSTORE_BLOBSTORES);
       if (explicitContexts != null) {
           contexts = filter(contexts, in(copyOf(Splitter.on(',').split(explicitContexts))));
       }
       checkState(!contexts.isEmpty(), "no credentials available for any requested  context");
       return contexts;
   }
   
   private Properties loadJCloudsProperties() {
      LOGGER.trace("About to read properties from '%s'", "/WEB-INF/jclouds.properties");
      Properties props = new Properties();
      InputStream input = servletConfig.getServletContext().getResourceAsStream("/WEB-INF/jclouds.properties");
      try {
         props.load(input);
      } catch (IOException e) {
         throw new RuntimeException(e);
      } finally {
         Closeables.closeQuietly(input);
      }
      LOGGER.trace("Properties successfully read.");
      return props;
   }

   @Bean
   public StoreTweetsController storeTweetsController() {
      StoreTweetsController controller = new StoreTweetsController(providerTypeToBlobStoreMap, container, twitterClient);
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

   @Bean
   public EnqueueStoresController enqueueStoresController() {
       return new EnqueueStoresController(providerTypeToBlobStoreMap, queue, baseUrl);
   }

   private void injectServletConfig(Servlet servlet) {
      LOGGER.trace("About to inject servlet config '%s'", servletConfig);
      try {
         servlet.init(checkNotNull(servletConfig));
      } catch (ServletException exception) {
         throw new BeanCreationException("Unable to instantiate " + servlet, exception);
      }
      LOGGER.trace("Successfully injected servlet config.");
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
      urlMap.put("/stores/*", enqueueStoresController());
      mapping.setUrlMap(urlMap);
      /*
       * "/store", "/tweets" and "/stores" are part of the servlet mapping and thus 
       * stripped by the mapping if using default settings.
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
      LOGGER.trace("About to close contexts.");
      for (BlobStoreContext<?, ?> context : providerTypeToBlobStoreMap.values()) {
         context.close();
      }
      LOGGER.trace("Contexts closed.");
      LOGGER.trace("About to purge request queue.");
      queue.destroy();
      LOGGER.trace("Request queue purged.");
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.springframework.web.context.ServletConfigAware#setServletConfig(javax.servlet.ServletConfig
    * )
    */
   @Override
   public void setServletConfig(ServletConfig servletConfig) {
      this.servletConfig = servletConfig;
   }
}