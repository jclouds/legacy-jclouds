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
package org.jclouds.internal;

import static org.testng.Assert.assertEquals;

import javax.inject.Named;

import org.testng.annotations.Test;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import com.google.inject.name.Names;

/**
 * 
 * @author Adrian Cole
 */
@Test(testName = "FilterStringsBoundToInjectorByNameTest")
public class FilterStringsBoundToInjectorByNameTest {

   public void testEmptyWhenNoStringsBound() {
      FilterStringsBoundToInjectorByName fn = Guice.createInjector().getInstance(FilterStringsBoundToInjectorByName.class);
      assertEquals(fn.apply(Predicates.<String> alwaysTrue()), ImmutableMap.<String, String> of());
   }

   public void testEmptyWhenNotStringsBound() {
      FilterStringsBoundToInjectorByName fn = Guice.createInjector(new AbstractModule() {

         @Override
         protected void configure() {
            bindConstant().annotatedWith(Names.named("foo")).to(1l);
         }

      }).getInstance(FilterStringsBoundToInjectorByName.class);

      assertEquals(fn.apply(Predicates.<String> alwaysTrue()), ImmutableMap.<String, String> of());
   }

   public void testReturnsGuiceNamedString() {
      FilterStringsBoundToInjectorByName fn = Guice.createInjector(new AbstractModule() {

         @Override
         protected void configure() {
            bindConstant().annotatedWith(Names.named("foo")).to("bar");
         }

      }).getInstance(FilterStringsBoundToInjectorByName.class);

      assertEquals(fn.apply(Predicates.<String> alwaysTrue()), ImmutableMap.<String, String> of("foo", "bar"));
   }

   public void testReturnsJavaNamedString() {
      FilterStringsBoundToInjectorByName fn = Guice.createInjector(new AbstractModule() {
         @Named("foo")
         @Provides
         String provideFoo() {
            return "bar";
         }

         @Override
         protected void configure() {
         }

      }).getInstance(FilterStringsBoundToInjectorByName.class);

      assertEquals(fn.apply(Predicates.<String> alwaysTrue()), ImmutableMap.<String, String> of("foo", "bar"));
   }

   public void testFilterWorks() {
      FilterStringsBoundToInjectorByName fn = Guice.createInjector(new AbstractModule() {

         @Override
         protected void configure() {
            bindConstant().annotatedWith(Names.named("foo")).to("bar");
            bindConstant().annotatedWith(Names.named("bing")).to("bong");
         }

      }).getInstance(FilterStringsBoundToInjectorByName.class);

      assertEquals(fn.apply(Predicates.<String> equalTo("bing")), ImmutableMap.<String, String> of("bing", "bong"));
   }

}
