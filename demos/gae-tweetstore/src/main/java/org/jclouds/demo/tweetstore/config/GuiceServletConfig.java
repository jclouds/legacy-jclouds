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
import static org.jclouds.demo.tweetstore.reference.TweetStoreConstants.PROPERTY_TWEETSTORE_CONTAINER;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContextEvent;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextFactory;
import org.jclouds.demo.tweetstore.controller.AddTweetsController;
import org.jclouds.demo.tweetstore.controller.StoreTweetsController;
import org.jclouds.gae.config.GoogleAppEngineConfigurationModule;
import org.jclouds.twitter.TwitterClient;
import org.jclouds.twitter.TwitterContextFactory;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;
import com.google.appengine.repackaged.com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.io.Closeables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.google.inject.util.Jsr330;

/**
 * Setup Logging and create Injector for use in testing S3.
 * 
 * @author Adrian Cole
 */
public class GuiceServletConfig extends GuiceServletContextListener {
   public static final String PROPERTY_BLOBSTORE_CONTEXTS = "blobstore.contexts";

   private Map<String, BlobStoreContext> providerTypeToBlobStoreMap;
   private TwitterClient twitterClient;
   private String container;

   @Override
   public void contextInitialized(ServletContextEvent servletContextEvent) {

      BlobStoreContextFactory blobStoreContextFactory = null;
      try {
         blobStoreContextFactory = new BlobStoreContextFactory();
      } catch (IOException e) {
         Throwables.propagate(e);
      }

      Properties props = loadJCloudsProperties(servletContextEvent);

      Module googleModule = new GoogleAppEngineConfigurationModule();
      Set<Module> modules = ImmutableSet.<Module> of(googleModule);
      // shared across all blobstores and used to retrieve tweets
      twitterClient = TwitterContextFactory.createContext(props, googleModule).getApi();

      // common namespace for storing tweets
      container = checkNotNull(props.getProperty(PROPERTY_TWEETSTORE_CONTAINER),
               PROPERTY_TWEETSTORE_CONTAINER);

      // instantiate and store references to all blobstores by provider name
      providerTypeToBlobStoreMap = Maps.newHashMap();
      for (String hint : Splitter.on(',').split(
               checkNotNull(props.getProperty(PROPERTY_BLOBSTORE_CONTEXTS),
                        PROPERTY_BLOBSTORE_CONTEXTS))) {
         providerTypeToBlobStoreMap.put(hint, blobStoreContextFactory.createContext(hint, modules,
                  props));
      }

      // get a queue for submitting store tweet requests
      Queue queue = QueueFactory.getQueue("twitter");
      // submit a job to store tweets for each configured blobstore
      for (String name : providerTypeToBlobStoreMap.keySet()) {
         queue.add(url("/store/do").header("context", name).method(Method.GET));
      }

      super.contextInitialized(servletContextEvent);
   }

   private Properties loadJCloudsProperties(ServletContextEvent servletContextEvent) {
      InputStream input = servletContextEvent.getServletContext().getResourceAsStream(
               "/WEB-INF/jclouds.properties");
      Properties props = new Properties();
      try {
         props.load(input);
      } catch (IOException e) {
         throw new RuntimeException(e);
      } finally {
         Closeables.closeQuietly(input);
      }
      return props;
   }

   @Override
   protected Injector getInjector() {
      return Guice.createInjector(new ServletModule() {
         @Override
         protected void configureServlets() {
            bind(new TypeLiteral<Map<String, BlobStoreContext>>() {
            }).toInstance(providerTypeToBlobStoreMap);
            bind(TwitterClient.class).toInstance(twitterClient);
            bindConstant().annotatedWith(Jsr330.named(PROPERTY_TWEETSTORE_CONTAINER)).to(container);
            serve("/store/*").with(StoreTweetsController.class);
            serve("/tweets/*").with(AddTweetsController.class);
         }
      });
   }

   @Override
   public void contextDestroyed(ServletContextEvent servletContextEvent) {
      for (BlobStoreContext context : providerTypeToBlobStoreMap.values()) {
         context.close();
      }
      super.contextDestroyed(servletContextEvent);
   }
}