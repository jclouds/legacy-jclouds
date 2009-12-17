/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.samples.googleappengine.config;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContextEvent;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextBuilder;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.gae.config.GaeHttpCommandExecutorServiceModule;
import org.jclouds.samples.googleappengine.GetAllContainersController;

import com.google.appengine.repackaged.com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.io.Closeables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

/**
 * Setup Logging and create Injector for use in testing S3.
 * 
 * @author Adrian Cole
 */
public class GuiceServletConfig extends GuiceServletContextListener {

   private Map<String, BlobStoreContext<?, ?>> contexts;

   @SuppressWarnings("unchecked")
   @Override
   public void contextInitialized(ServletContextEvent servletContextEvent) {
      Properties props = loadJCloudsProperties(servletContextEvent);
      ImmutableList<String> list = ImmutableList.<String> of(checkNotNull(
               props.getProperty(BlobStoreConstants.PROPERTY_BLOBSTORE_CONTEXTBUILDERS),
               BlobStoreConstants.PROPERTY_BLOBSTORE_CONTEXTBUILDERS).split(","));
      contexts = Maps.newHashMap();
      for (String className : list) {
         try {
            Class<BlobStoreContextBuilder<?, ?>> builderClass;
            builderClass = (Class<BlobStoreContextBuilder<?, ?>>) Class.forName(className);
            String name = builderClass.getSimpleName().replaceAll("BlobStoreContextBuilder", "");
            Constructor<BlobStoreContextBuilder<?, ?>> constructor = builderClass
                     .getConstructor(Properties.class);
            contexts.put(name, constructor.newInstance(props).withModules(
                     new GaeHttpCommandExecutorServiceModule()).buildContext());
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
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
            bind(new TypeLiteral<Map<String, BlobStoreContext<?, ?>>>() {
            }).toInstance(GuiceServletConfig.this.contexts);
            serve("*.blobstore").with(GetAllContainersController.class);
            requestInjection(this);
         }
      }

      );
   }

   @Override
   public void contextDestroyed(ServletContextEvent servletContextEvent) {
      for (BlobStoreContext<?, ?> context : contexts.values()) {
         context.close();
      }
      super.contextDestroyed(servletContextEvent);
   }
}