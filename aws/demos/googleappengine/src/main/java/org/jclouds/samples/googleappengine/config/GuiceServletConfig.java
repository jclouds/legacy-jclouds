/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static org.jclouds.compute.reference.ComputeServiceConstants.PROPERTY_TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.reference.ComputeServiceConstants.PROPERTY_TIMEOUT_NODE_TERMINATED;
import static org.jclouds.compute.reference.ComputeServiceConstants.PROPERTY_TIMEOUT_PORT_OPEN;
import static org.jclouds.compute.reference.ComputeServiceConstants.PROPERTY_TIMEOUT_SCRIPT_COMPLETE;

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
      props.setProperty(PROPERTY_TIMEOUT_NODE_TERMINATED, "25000");
      props.setProperty(PROPERTY_TIMEOUT_NODE_RUNNING, "25000");
      props.setProperty(PROPERTY_TIMEOUT_SCRIPT_COMPLETE, "25000");
      props.setProperty(PROPERTY_TIMEOUT_PORT_OPEN, "25000");

      ImmutableSet<Module> modules = ImmutableSet
               .<Module> of(new GoogleAppEngineConfigurationModule());

      blobsStoreContexts = ImmutableMap.<String, BlobStoreContext> of("s3",
               new BlobStoreContextFactory().createContext("s3", modules, props));
      computeServiceContexts = ImmutableMap.<String, ComputeServiceContext> of("ec2",
               new ComputeServiceContextFactory().createContext("ec2", modules, props));

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