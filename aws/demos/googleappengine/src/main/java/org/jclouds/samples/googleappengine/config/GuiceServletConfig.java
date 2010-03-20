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
package org.jclouds.samples.googleappengine.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContextEvent;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextFactory;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.gae.config.GoogleAppEngineConfigurationModule;
import org.jclouds.samples.googleappengine.GetAllStatusController;

import com.google.appengine.repackaged.com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

/**
 * Setup Logging and create Injector for use in testing S3.
 * 
 * @author Adrian Cole
 */
public class GuiceServletConfig extends GuiceServletContextListener {

   private Map<String, BlobStoreContext> blobsStoreContexts;
   private Map<String, ComputeServiceContext> computeServiceContexts;

   @Override
   public void contextInitialized(ServletContextEvent servletContextEvent) {
      Properties props = loadJCloudsProperties(servletContextEvent);
      ImmutableSet<Module> modules = ImmutableSet
               .<Module> of(new GoogleAppEngineConfigurationModule());
      try {
         blobsStoreContexts = ImmutableMap.<String, BlobStoreContext> of("s3",
                  new BlobStoreContextFactory().createContext("s3", modules, props));
         computeServiceContexts = ImmutableMap.<String, ComputeServiceContext> of("ec2",
                  new ComputeServiceContextFactory().createContext("ec2", modules, props));
      } catch (IOException e) {
         Throwables.propagate(e);
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
            }).toInstance(GuiceServletConfig.this.blobsStoreContexts);
            bind(new TypeLiteral<Map<String, ComputeServiceContext>>() {
            }).toInstance(GuiceServletConfig.this.computeServiceContexts);
            serve("*.check").with(GetAllStatusController.class);
            requestInjection(this);
         }
      }

      );
   }

   @Override
   public void contextDestroyed(ServletContextEvent servletContextEvent) {
      for (BlobStoreContext context : blobsStoreContexts.values()) {
         context.close();
      }
      for (ComputeServiceContext context : computeServiceContexts.values()) {
         context.close();
      }
      super.contextDestroyed(servletContextEvent);
   }
}