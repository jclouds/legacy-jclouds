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
package org.jclouds.rest;

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
 * Tests behavior of modules configured in RestContextBuilder<String,String>
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "rest.RestContextBuilderTest")
public class RestContextBuilderTest {

   @ConfiguresHttpCommandExecutorService
   static class HttpModule extends AbstractModule {

      @Override
      protected void configure() {

      }
   }

   @Test
   public void testAddHttpModuleIfNotPresent() {
      List<Module> modules = new ArrayList<Module>();
      HttpModule module = new HttpModule();
      modules.add(module);
      new RestContextBuilder<String, String>(String.class, String.class, new Properties())
               .addHttpModuleIfNeededAndNotPresent(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.remove(0), module);
   }

   @Test
   public void testAddLoggingModuleIfNotPresent() {
      List<Module> modules = new ArrayList<Module>();
      LoggingModule module = new NullLoggingModule();
      modules.add(module);
      new RestContextBuilder<String, String>(String.class, String.class, new Properties())
               .addLoggingModuleIfNotPresent(modules);
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
      RestContextBuilder<String, String> builder = new RestContextBuilder<String, String>(
               String.class, String.class, new Properties());
      builder.addHttpModuleIfNeededAndNotPresent(modules);
      builder.addLoggingModuleIfNotPresent(modules);
      assertEquals(modules.size(), 2);
      assertEquals(modules.remove(0), loggingModule);
      assertEquals(modules.remove(0), httpModule);
   }

   @Test
   public void testAddBothWhenDoesntRequireHttp() {
      List<Module> modules = new ArrayList<Module>();
      modules.add(new ConfiguresRestClientModule());
      RestContextBuilder<String, String> builder = new RestContextBuilder<String, String>(
               String.class, String.class, new Properties());
      builder.addHttpModuleIfNeededAndNotPresent(modules);
      builder.addLoggingModuleIfNotPresent(modules);
      assertEquals(modules.size(), 2);
      assert modules.remove(0) instanceof ConfiguresRestClientModule;
      assert modules.remove(0) instanceof JDKLoggingModule;
   }

   @ConfiguresRestClient
   static class ConfiguresRestClientModule implements Module {

      public void configure(Binder arg0) {
      }

   }

   @Test
   public void testAddBothWhenDefault() {
      List<Module> modules = new ArrayList<Module>();
      RestContextBuilder<String, String> builder = new RestContextBuilder<String, String>(
               String.class, String.class, new Properties());
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
      RestContextBuilder<String, String> builder = new RestContextBuilder<String, String>(
               String.class, String.class, new Properties());
      builder.addHttpModuleIfNeededAndNotPresent(modules);
      builder.addLoggingModuleIfNotPresent(modules);
      assertEquals(modules.size(), 3);
      assert modules.remove(0) instanceof RequiresHttpModule;
      assert modules.remove(0) instanceof JavaUrlHttpCommandExecutorServiceModule;
      assert modules.remove(0) instanceof JDKLoggingModule;
   }

   public void testBuilder() {

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
      RestContextBuilder<String, String> builder = new RestContextBuilder<String, String>(
               String.class, String.class, new Properties());
      builder.withModules(module1, module2);

   }
}
