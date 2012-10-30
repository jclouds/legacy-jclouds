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
package org.jclouds.samples.googleappengine.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.io.Closeables.closeQuietly;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_PORT_OPEN;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_SCRIPT_COMPLETE;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.inject.Singleton;
import javax.servlet.ServletContextEvent;

import org.jclouds.ContextBuilder;
import org.jclouds.View;
import org.jclouds.gae.config.AsyncGoogleAppEngineConfigurationModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.Providers;
import org.jclouds.samples.googleappengine.GetAllResourcesController;

import com.google.appengine.api.ThreadManager;
import com.google.apphosting.api.ApiProxy;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

/**
 * Setup Logging and create {@link Injector} for use in testing Views
 * 
 * @author Adrian Cole
 */
public class GuiceServletConfig extends GuiceServletContextListener {

   private Iterable<View> views;

   @Override
   public void contextInitialized(ServletContextEvent servletContextEvent) {
      final Properties overrides = loadJCloudsProperties(servletContextEvent);
      // until there's a global skip image parse option
      overrides.setProperty("jclouds.ec2.ami-query", "");
      overrides.setProperty("jclouds.ec2.cc-ami-query", "");

      // ensure requests don't take longer than GAE timeout
      overrides.setProperty(TIMEOUT_NODE_TERMINATED, "25000");
      overrides.setProperty(TIMEOUT_NODE_RUNNING, "25000");
      overrides.setProperty(TIMEOUT_SCRIPT_COMPLETE, "25000");
      overrides.setProperty(TIMEOUT_PORT_OPEN, "25000");

      // correct the classloader so that extensions can be found
      Thread.currentThread().setContextClassLoader(Providers.class.getClassLoader());

      Iterable<ProviderMetadata> identityInProperties = providersWeHaveIdentitiesFor(overrides);

      final ImmutableSet<Module> modules = ImmutableSet.<Module> of(new AsyncGoogleAppEngineConfigurationModule());
      views = transform(identityInProperties, new Function<ProviderMetadata, View>() {

         @Override
         public View apply(ProviderMetadata input) {
            TypeToken<? extends View> defaultView = get(input.getApiMetadata().getViews(), 0);
            return ContextBuilder.newBuilder(input).modules(modules).overrides(overrides).buildView(defaultView);
         }

      });

      super.contextInitialized(servletContextEvent);
   }

   protected Iterable<ProviderMetadata> providersWeHaveIdentitiesFor(final Properties overrides) {
      // there's a chance serviceloader is being lazy, and we don't want
      // ConcurrentModificationException, so copy into a set.
      return ImmutableSet.copyOf(filter(Providers.all(), new Predicate<ProviderMetadata>() {

         @Override
         public boolean apply(ProviderMetadata input) {
            return overrides.containsKey(input.getId() + ".identity");
         }

      }));
   }

   private Properties loadJCloudsProperties(ServletContextEvent servletContextEvent) {
      InputStream input = servletContextEvent.getServletContext().getResourceAsStream("/WEB-INF/jclouds.properties");
      Properties props = new Properties();
      try {
         props.load(input);
      } catch (IOException e) {
         throw new RuntimeException(e);
      } finally {
         closeQuietly(input);
      }
      return props;
   }

   @Override
   protected Injector getInjector() {
      return Guice.createInjector(new JDKLoggingModule(), new ServletModule() {
         @Override
         protected void configureServlets() {
            bind(new TypeLiteral<Iterable<View>>() {
            }).toInstance(GuiceServletConfig.this.views);
            serve("*.check").with(GetAllResourcesController.class);
            requestInjection(this);
         }

         @SuppressWarnings("unused")
         @Provides
         long remainingMillis() {
            // leave 100ms for any post processing
            return ApiProxy.getCurrentEnvironment().getRemainingMillis() - 100;
         }

         @SuppressWarnings("unused")
         @Provides
         @Singleton
         ListeningExecutorService currentRequestExecutorService() {
            ThreadFactory factory = checkNotNull(ThreadManager.currentRequestThreadFactory(),
                  "ThreadManager.currentRequestThreadFactory()");
            return MoreExecutors.listeningDecorator(Executors.newCachedThreadPool(factory));
         }
      });
   }

   @Override
   public void contextDestroyed(ServletContextEvent servletContextEvent) {
      for (View view : views) {
         view.unwrap().close();
      }
      super.contextDestroyed(servletContextEvent);
   }
}
