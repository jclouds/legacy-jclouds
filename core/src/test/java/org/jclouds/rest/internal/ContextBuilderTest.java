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
package org.jclouds.rest.internal;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.events.config.EventBusModule;
import org.jclouds.http.IntegrationTestAsyncClient;
import org.jclouds.http.IntegrationTestClient;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.config.ConfiguresHttpCommandExecutorService;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.config.LoggingModule;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.providers.AnonymousProviderMetadata;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.CredentialStoreModule;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Module;

/**
 * Tests behavior of modules configured in ContextBuilder
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName="ContextBuilderTest")
public class ContextBuilderTest {

   @ConfiguresHttpCommandExecutorService
   static class HttpModule extends AbstractModule {

      @Override
      protected void configure() {

      }
   }

   private ContextBuilder<?, ?, ?, ?> testContextBuilder() {
      return ContextBuilder.newBuilder(AnonymousProviderMetadata.forClientMappedToAsyncClientOnEndpoint(
            IntegrationTestClient.class, IntegrationTestAsyncClient.class, "http://localhost"));
   }
   
   @Test
   public void testAddHttpModuleIfNotPresent() {
      List<Module> modules = new ArrayList<Module>();
      HttpModule module = new HttpModule();
      modules.add(module);
      testContextBuilder().addHttpModuleIfNeededAndNotPresent(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.remove(0), module);
   }

   @Test
   public void testAddLoggingModuleIfNotPresent() {
      List<Module> modules = new ArrayList<Module>();
      LoggingModule module = new NullLoggingModule();
      modules.add(module);
      testContextBuilder().addLoggingModuleIfNotPresent(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.remove(0), module);
   }
   
   @Test
   public void testAddEventBusModuleIfNotPresent() {
      List<Module> modules = new ArrayList<Module>();
      EventBusModule module = new EventBusModule();
      modules.add(module);
      testContextBuilder().addEventBusIfNotPresent(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.remove(0), module);
   }

   @Test
   public void testAddExecutorServiceModuleIfNotPresent() {
      List<Module> modules = new ArrayList<Module>();
      ExecutorServiceModule module = new ExecutorServiceModule();
      modules.add(module);
      testContextBuilder().addExecutorServiceIfNotPresent(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.remove(0), module);
   }

   @Test
   public void testAddCredentialStoreModuleIfNotPresent() {
      List<Module> modules = new ArrayList<Module>();
      CredentialStoreModule module = new CredentialStoreModule();
      modules.add(module);
      testContextBuilder().addCredentialStoreIfNotPresent(modules);
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
      ContextBuilder<?, ?, ?, ?> builder = testContextBuilder();
      builder.addHttpModuleIfNeededAndNotPresent(modules);
      builder.addLoggingModuleIfNotPresent(modules);
      assertEquals(modules.size(), 2);
      assertEquals(modules.remove(0), loggingModule);
      assertEquals(modules.remove(0), httpModule);
   }

   @Test
   public void testAddBothWhenDoesntRequireHttp() {
      List<Module> modules = new ArrayList<Module>();
      modules.add(new ConfiguresClientModule());
      ContextBuilder<?, ?, ?, ?> builder = testContextBuilder();
      builder.addHttpModuleIfNeededAndNotPresent(modules);
      builder.addLoggingModuleIfNotPresent(modules);
      assertEquals(modules.size(), 2);
      assert modules.remove(0) instanceof ConfiguresClientModule;
      assert modules.remove(0) instanceof JDKLoggingModule;
   }

   @ConfiguresRestClient
   static class ConfiguresClientModule implements Module {

      public void configure(Binder arg0) {
      }

   }

   @Test
   public void testAddBothWhenDefault() {
      List<Module> modules = new ArrayList<Module>();
      ContextBuilder<?, ?, ?, ?> builder = testContextBuilder();
      builder.addHttpModuleIfNeededAndNotPresent(modules);
      builder.addLoggingModuleIfNotPresent(modules);
      assertEquals(modules.size(), 2);
      assert modules.remove(0) instanceof JavaUrlHttpCommandExecutorServiceModule;
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
      ContextBuilder<?, ?, ?, ?> builder = testContextBuilder();
      builder.addHttpModuleIfNeededAndNotPresent(modules);
      builder.addLoggingModuleIfNotPresent(modules);
      assertEquals(modules.size(), 3);
      assert modules.remove(0) instanceof RequiresHttpModule;
      assert modules.remove(0) instanceof JavaUrlHttpCommandExecutorServiceModule;
      assert modules.remove(0) instanceof JDKLoggingModule;
   }

   public void testBuilder() {

      Module module1 = new AbstractModule() {

         @Override
         protected void configure() {
         }
      };
      Module module2 = new AbstractModule() {

         @Override
         protected void configure() {
         }
      };
      ContextBuilder<?, ?, ?, ?> builder = testContextBuilder();
      builder.modules(Arrays.asList(module1, module2));

   }
}
