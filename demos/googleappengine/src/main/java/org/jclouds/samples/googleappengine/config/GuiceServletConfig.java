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

import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_PORT_OPEN;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_SCRIPT_COMPLETE;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.gae.config.AsyncGoogleAppEngineConfigurationModule;
import org.jclouds.samples.googleappengine.GetAllStatusController;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

/**
 * Setup Logging and create {@link Injector} for use in testing Amazon EC2 and S3.
 * 
 * @author Adrian Cole
 */
public class GuiceServletConfig extends GuiceServletContextListener {

   private Iterable<BlobStoreContext> blobsStoreContexts;
   private Iterable<ComputeServiceContext> computeServiceContexts;

   @Override
   public void contextInitialized(ServletContextEvent servletContextEvent) {
      Properties overrides = loadJCloudsProperties(servletContextEvent);
      overrides.setProperty(TIMEOUT_NODE_TERMINATED, "25000");
      overrides.setProperty(TIMEOUT_NODE_RUNNING, "25000");
      overrides.setProperty(TIMEOUT_SCRIPT_COMPLETE, "25000");
      overrides.setProperty(TIMEOUT_PORT_OPEN, "25000");

      // note that this module hooks into the async urlfetchservice
      ImmutableSet<Module> modules = ImmutableSet.<Module> of(new AsyncGoogleAppEngineConfigurationModule());

      blobsStoreContexts = ImmutableSet.of(
            ContextBuilder.newBuilder("hpcloud-objectstorage")
                          .modules(modules)
                          .overrides(overrides)
                          .buildView(BlobStoreContext.class));
      computeServiceContexts = ImmutableSet.of(
            ContextBuilder.newBuilder("hpcloud-compute")
                          .modules(modules)
                          .overrides(overrides)
                          .buildView(ComputeServiceContext.class));

      super.contextInitialized(servletContextEvent);
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
            bind(new TypeLiteral<Iterable<BlobStoreContext>>() {
            }).toInstance(GuiceServletConfig.this.blobsStoreContexts);
            bind(new TypeLiteral<Iterable<ComputeServiceContext>>() {
            }).toInstance(GuiceServletConfig.this.computeServiceContexts);
            serve("*.check").with(GetAllStatusController.class);
            requestInjection(this);
         }
      }

      );
   }

   @Override
   public void contextDestroyed(ServletContextEvent servletContextEvent) {
      for (BlobStoreContext context : blobsStoreContexts) {
         context.close();
      }
      for (ComputeServiceContext context : computeServiceContexts) {
         context.close();
      }
      super.contextDestroyed(servletContextEvent);
   }
}