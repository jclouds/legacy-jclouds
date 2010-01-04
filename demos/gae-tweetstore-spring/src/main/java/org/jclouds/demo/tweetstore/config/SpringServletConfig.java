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

import org.jclouds.demo.tweetstore.controller.AddTweetsController;
import org.jclouds.demo.tweetstore.controller.StoreTweetsController;
import org.jclouds.demo.tweetstore.functions.ServiceToStoredTweetStatuses;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.WebApplicationContext;
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

   @Bean
   public StoreTweetsController storeTweetsController(SpringAppConfig appConfig) {
      return new StoreTweetsController(checkNotNull(appConfig.providerTypeToBlobStoreMap,
               "contexts"), checkNotNull(appConfig.container, "container"), checkNotNull(
               appConfig.twitterClient, "twitterClient"));
   }

   @Bean
   public AddTweetsController addTweetsController(SpringAppConfig appConfig) {
      return new AddTweetsController(
               checkNotNull(appConfig.providerTypeToBlobStoreMap, "contexts"),
               serviceToStoredTweetStatuses(appConfig));
   }

   @Bean
   ServiceToStoredTweetStatuses serviceToStoredTweetStatuses(SpringAppConfig appConfig) {
      return new ServiceToStoredTweetStatuses(checkNotNull(appConfig.providerTypeToBlobStoreMap,
               "contexts"), checkNotNull(appConfig.container, "container"));
   }

   @Bean
   public HandlerMapping handlerMapping(AddTweetsController add, StoreTweetsController store,
            WebApplicationContext context) {
      SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
      mapping.setServletContext(context.getServletContext());
      Map<String, Object> urlMap = Maps.newHashMapWithExpectedSize(2);
      urlMap.put("/store/*", checkNotNull(store, "store"));
      add.setServletContext(checkNotNull(context.getServletContext(), "servletContext"));
      urlMap.put("/tweets/*", checkNotNull(add, "add"));
      mapping.setUrlMap(urlMap);
      /*
       * "/store" and "/tweets" are part of the servlet mapping and thus stripped by the mapping if
       * using default settings.
       */
      mapping.setAlwaysUseFullPath(true);
      return mapping;
   }

   @Bean
   public HandlerAdapter servletHandlerAdapter() {
      return new SimpleServletHandlerAdapter();
   }
}

