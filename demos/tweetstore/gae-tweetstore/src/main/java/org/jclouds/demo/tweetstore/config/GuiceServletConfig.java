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

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;
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

import javax.servlet.ServletContextEvent;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextFactory;
import org.jclouds.demo.tweetstore.config.util.CredentialsCollector;
import org.jclouds.demo.tweetstore.controller.AddTweetsController;
import org.jclouds.demo.tweetstore.controller.StoreTweetsController;
import org.jclouds.gae.config.GoogleAppEngineConfigurationModule;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.repackaged.com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.io.Closeables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

/**
 * Setup Logging and create Injector for use in testing S3.
 * 
 * @author Adrian Cole
 */
public class GuiceServletConfig extends GuiceServletContextListener {
   public static final String PROPERTY_BLOBSTORE_CONTEXTS = "blobstore.contexts";

   private Map<String, BlobStoreContext> providerTypeToBlobStoreMap;
   private Twitter twitterClient;
   private String container;
   private Queue queue;

   @Override
   public void contextInitialized(ServletContextEvent servletContextEvent) {

      BlobStoreContextFactory blobStoreContextFactory = new BlobStoreContextFactory();

      Properties props = loadJCloudsProperties(servletContextEvent);

      Module googleModule = new GoogleAppEngineConfigurationModule();
      Set<Module> modules = ImmutableSet.<Module> of(googleModule);
      // shared across all blobstores and used to retrieve tweets
      try {
          Configuration twitterConf = new ConfigurationBuilder()
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
      // instantiate and store references to all blobstores by provider name
      providerTypeToBlobStoreMap = Maps.newHashMap();
      for (String hint : getBlobstoreContexts(props)) {
          providerTypeToBlobStoreMap.put(hint, blobStoreContextFactory.createContext(hint, modules, props));
      }

      // get a queue for submitting store tweet requests
      queue = QueueFactory.getQueue("twitter");
      // submit a job to store tweets for each configured blobstore
      for (String name : providerTypeToBlobStoreMap.keySet()) {
          queue.add(withUrl("/store/do").header("context", name).method(Method.GET));
      }

      super.contextInitialized(servletContextEvent);
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

   private Properties loadJCloudsProperties(ServletContextEvent servletContextEvent) {
      InputStream input = servletContextEvent.getServletContext().getResourceAsStream("/WEB-INF/jclouds.properties");
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
            bind(Twitter.class).toInstance(twitterClient);
            bindConstant().annotatedWith(Names.named(PROPERTY_TWEETSTORE_CONTAINER)).to(container);
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
      queue.purge();
      super.contextDestroyed(servletContextEvent);
   }
}