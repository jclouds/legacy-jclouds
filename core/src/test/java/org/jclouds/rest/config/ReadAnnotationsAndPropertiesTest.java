/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.rest.config;

import static org.jclouds.reflect.Reflection2.method;
import static org.testng.Assert.assertEquals;

import java.util.Properties;

import javax.inject.Named;

import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.http.HttpResponse;
import org.jclouds.internal.FilterStringsBoundToInjectorByName;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.annotations.Fallback;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true)
public class ReadAnnotationsAndPropertiesTest {

   public static interface ThingApi {
      HttpResponse get();

      HttpResponse namedGet();
   }

   public static interface ThingAsyncApi {
      ListenableFuture<HttpResponse> get();

      @Named("ns:get")
      @Fallback(FalseOnNotFoundOr404.class)
      ListenableFuture<HttpResponse> namedGet();
   }

   private Invocation asyncGet;
   private Invocation asyncNamedGet;
   private org.jclouds.Fallback<Object> defaultFallback;

   @BeforeClass
   void setupInvocations() throws SecurityException, NoSuchMethodException {
      asyncGet = Invocation.create(method(ThingAsyncApi.class, "get"), ImmutableList.of());
      asyncNamedGet = Invocation.create(method(ThingAsyncApi.class, "namedGet"), ImmutableList.of());
      defaultFallback = new NullOnNotFoundOr404();
   }

   /**
    * this functionality will be removed once Named annotations are on all async
    * classes.
    */
   public void testInvocationsSetDefaultTimeoutOnAsyncMethods() throws Exception {
      final Properties props = new Properties();
      props.setProperty("jclouds.timeouts.default", "250");
      Injector injector = Guice.createInjector(new AbstractModule() {
         protected void configure() {
            Names.bindProperties(binder(), props);
         }
      });
      ReadAnnotationsAndProperties config = new ReadAnnotationsAndProperties(injector,
            new FilterStringsBoundToInjectorByName(injector), defaultFallback);
      assertEquals(config.getTimeoutNanos(asyncGet), Optional.of(250000000l));
      assertEquals(config.getTimeoutNanos(asyncNamedGet), Optional.of(250000000l));
   }

   public void testNamedInvocationGetsTimeoutOverrideOnAsyncMethods() throws Exception {
      final Properties props = new Properties();
      props.setProperty("jclouds.timeouts.default", "50");
      props.setProperty("jclouds.timeouts.ThingApi", "100");
      props.setProperty("jclouds.timeouts.ns:get", "250");
      Injector injector = Guice.createInjector(new AbstractModule() {
         protected void configure() {
            Names.bindProperties(binder(), props);
         }
      });
      ReadAnnotationsAndProperties config = new ReadAnnotationsAndProperties(injector,
            new FilterStringsBoundToInjectorByName(injector), defaultFallback);
      assertEquals(config.getTimeoutNanos(asyncNamedGet), Optional.of(250000000l));
   }

   /**
    * this functionality will be removed once Named annotations are on all async
    * classes.
    */
   public void testNamingConventionOfUnnamedMethods() throws Exception {
      Injector injector = Guice.createInjector();
      ReadAnnotationsAndProperties config = new ReadAnnotationsAndProperties(injector,
            new FilterStringsBoundToInjectorByName(injector), defaultFallback);
      assertEquals(config.getCommandName(asyncGet), "ThingApi.get");
   }

   public void testNamingConventionOfNamedAsyncMethods() throws Exception {
      Injector injector = Guice.createInjector();
      ReadAnnotationsAndProperties config = new ReadAnnotationsAndProperties(injector,
            new FilterStringsBoundToInjectorByName(injector), defaultFallback);
      assertEquals(config.getCommandName(asyncNamedGet), "ns:get");
   }

   public void testFallbackOverride() throws Exception {
      Injector injector = Guice.createInjector();
      ReadAnnotationsAndProperties config = new ReadAnnotationsAndProperties(injector,
            new FilterStringsBoundToInjectorByName(injector), defaultFallback);
      assertEquals(config.getFallback(asyncNamedGet).getClass(), FalseOnNotFoundOr404.class);
      assertEquals(config.getFallback(asyncGet), defaultFallback);
   }
}
