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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;

import org.apache.commons.io.IOUtils;
import org.jclouds.aws.s3.S3Client;
import org.jclouds.aws.s3.blobstore.S3BlobStoreContextBuilder;
import org.jclouds.aws.s3.blobstore.S3BlobStorePropertiesBuilder;
import org.jclouds.aws.s3.reference.S3Constants;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.gae.config.GaeHttpCommandExecutorServiceModule;
import org.jclouds.samples.googleappengine.GetAllBucketsController;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

/**
 * Setup Logging and create Injector for use in testing S3.
 * 
 * @author Adrian Cole
 */
public class GuiceServletConfig extends GuiceServletContextListener {
   @Inject
   BlobStoreContext<S3Client> context;
   String accessKeyId;
   String secretAccessKey;

   @Override
   public void contextInitialized(ServletContextEvent servletContextEvent) {
      Properties props = loadJCloudsProperties(servletContextEvent);
      this.accessKeyId = props.getProperty(S3Constants.PROPERTY_AWS_ACCESSKEYID);
      this.secretAccessKey = props.getProperty(S3Constants.PROPERTY_AWS_SECRETACCESSKEY);
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
         IOUtils.closeQuietly(input);
      }
      return props;
   }

   @Override
   protected Injector getInjector() {
      return new S3BlobStoreContextBuilder(new S3BlobStorePropertiesBuilder(accessKeyId,
               secretAccessKey).build()).withModules(new GaeHttpCommandExecutorServiceModule(),
               new ServletModule() {
                  @Override
                  protected void configureServlets() {
                     serve("*.s3").with(GetAllBucketsController.class);
                     requestInjection(this);
                  }
               }).buildInjector();
   }

   @Override
   public void contextDestroyed(ServletContextEvent servletContextEvent) {
      context.close();
      super.contextDestroyed(servletContextEvent);
   }
}