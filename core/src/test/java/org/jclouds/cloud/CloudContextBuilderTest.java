/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.cloud;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jclouds.http.RequiresHttp;
import org.jclouds.http.config.ConfiguresHttpCommandExecutorService;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.config.LoggingModule;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Module;

/**
 * Tests behavior of modules configured in CloudContextBuilder
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "s3.CloudContextBuilderTest")
public class CloudContextBuilderTest {

   @ConfiguresHttpCommandExecutorService
   static class HttpModule extends AbstractModule {

      @Override
      protected void configure() {

      }
   }

   class TestCloudContext implements CloudContext<String> {

      public void close() {

      }

      public String getConnection() {
         return "";
      }

   }

   class TestCloudContextBuilder extends CloudContextBuilder<String, TestCloudContext> {

      protected TestCloudContextBuilder(Properties properties) {
         super(properties);
      }

      @Override
      public TestCloudContext buildContext() {
         return new TestCloudContext();
      }

      @Override
      protected void addConnectionModule(List<Module> modules) {
         modules.add(new Module() {
            public void configure(Binder arg0) {
            }
         });
      }

      @Override
      protected void addContextModule(List<Module> modules) {
         modules.add(new Module() {
            public void configure(Binder arg0) {
            }
         });
      }

      @Override
      protected void addParserModule(List<Module> modules) {
         modules.add(new Module() {
            public void configure(Binder arg0) {
            }
         });
      }

      @Override
      public void authenticate(String id, String secret) {
      }

   }

   @Test
   public void testAddHttpModuleIfNotPresent() {
      List<Module> modules = new ArrayList<Module>();
      HttpModule module = new HttpModule();
      modules.add(module);
      new TestCloudContextBuilder(new Properties()).addHttpModuleIfNeededAndNotPresent(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.remove(0), module);
   }

   @Test
   public void testAddLoggingModuleIfNotPresent() {
      List<Module> modules = new ArrayList<Module>();
      LoggingModule module = new NullLoggingModule();
      modules.add(module);
      new TestCloudContextBuilder(new Properties()).addLoggingModuleIfNotPresent(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.remove(0), module);
   }

   @Test
   public void testAddNone() {
      List<Module> modules = new ArrayList<Module>();
      LoggingModule loggingModule = new NullLoggingModule();
      modules.add(loggingModule);
      HttpModule httpModule = new HttpModule();
      modules.add(httpModule);
      TestCloudContextBuilder builder = new TestCloudContextBuilder(new Properties());
      builder.addHttpModuleIfNeededAndNotPresent(modules);
      builder.addLoggingModuleIfNotPresent(modules);
      assertEquals(modules.size(), 2);
      assertEquals(modules.remove(0), loggingModule);
      assertEquals(modules.remove(0), httpModule);
   }

   @Test
   public void testAddBothWhenDoesntRequireHttp() {
      List<Module> modules = new ArrayList<Module>();
      TestCloudContextBuilder builder = new TestCloudContextBuilder(new Properties());
      builder.addHttpModuleIfNeededAndNotPresent(modules);
      builder.addLoggingModuleIfNotPresent(modules);
      assertEquals(modules.size(), 1);
      assert modules.remove(0) instanceof JDKLoggingModule;
   }

   @RequiresHttp
   class RequiresHttpModule implements Module {

      public void configure(Binder arg0) {
      }

   }

   @Test
   public void testAddBothWhenLive() {
      List<Module> modules = new ArrayList<Module>();
      modules.add(new RequiresHttpModule());
      TestCloudContextBuilder builder = new TestCloudContextBuilder(new Properties());
      builder.addHttpModuleIfNeededAndNotPresent(modules);
      builder.addLoggingModuleIfNotPresent(modules);
      assertEquals(modules.size(), 3);
      assert modules.remove(0) instanceof RequiresHttpModule;
      assert modules.remove(0) instanceof JavaUrlHttpCommandExecutorServiceModule;
      assert modules.remove(0) instanceof JDKLoggingModule;
   }

   public void testBuilder() {
      String id = "awsAccessKeyId";
      String secret = "awsSecretAccessKey";
      String httpAddress = "httpAddress";
      int httpMaxRetries = 9875;
      int httpPort = 3827;
      boolean httpSecure = false;
      int poolIoWorkerThreads = 2727;
      int poolMaxConnectionReuse = 3932;
      int poolMaxConnections = 3382;
      int poolMaxSessionFailures = 857;
      int poolRequestInvokerThreads = 8362;

      AbstractModule module1 = new AbstractModule() {

         @Override
         protected void configure() {
         }
      };
      AbstractModule module2 = new AbstractModule() {

         @Override
         protected void configure() {
         }
      };
      TestCloudContextBuilder builder = new TestCloudContextBuilder(new Properties());
      builder.authenticate(id, secret);
      builder.withHttpAddress(httpAddress);
      builder.withHttpMaxRetries(httpMaxRetries);
      builder.withHttpPort(httpPort);
      builder.withHttpSecure(httpSecure);
      builder.withModule(module1);
      builder.withModules(module2);
      builder.withPoolIoWorkerThreads(poolIoWorkerThreads);
      builder.withPoolMaxConnectionReuse(poolMaxConnectionReuse);
      builder.withPoolMaxConnections(poolMaxConnections);
      builder.withPoolMaxSessionFailures(poolMaxSessionFailures);
      builder.withPoolRequestInvokerThreads(poolRequestInvokerThreads);
   }
}
