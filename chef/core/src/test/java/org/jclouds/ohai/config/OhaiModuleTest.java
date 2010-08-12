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

package org.jclouds.ohai.config;

import static org.jclouds.chef.util.ChefUtils.ohaiAutomaticAttributeBinder;
import static org.testng.Assert.assertEquals;

import java.net.SocketException;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

import org.jclouds.chef.config.ChefParserModule;
import org.jclouds.domain.JsonBall;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.jclouds.ohai.Automatic;
import org.jclouds.ohai.config.multibindings.MapBinder;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Providers;

/**
 * Tests behavior of {@code OhaiModule}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ohai.OhaiModuleTest")
public class OhaiModuleTest {

   @Test
   public void test() throws SocketException {

      final Properties sysProperties = new Properties();

      sysProperties.setProperty("os.name", "Mac OS X");
      sysProperties.setProperty("os.version", "10.3.0");
      sysProperties.setProperty("user.name", "user");

      Injector injector = Guice.createInjector(new ChefParserModule(), new GsonModule(), new OhaiModule() {
         @Override
         protected Long millis() {
            return 127999291932529l;
         }

         @Override
         protected Properties systemProperties() {
            return sysProperties;
         }

      });
      Ohai ohai = injector.getInstance(Ohai.class);
      Json json = injector.getInstance(Json.class);

      assertEquals(
            json.toJson(ohai.ohai.get(), new TypeLiteral<Map<String, JsonBall>>() {
            }.getType()),
            "{\"ohai_time\":127999291932529,\"platform\":\"macosx\",\"platform_version\":\"10.3.0\",\"current_user\":\"user\",\"jvm\":{\"system\":{\"user.name\":\"user\",\"os.version\":\"10.3.0\",\"os.name\":\"Mac OS X\"}}}");
   }

   public void test2modules() throws SocketException {

      final Properties sysProperties = new Properties();

      sysProperties.setProperty("os.name", "Mac OS X");
      sysProperties.setProperty("os.version", "10.3.0");
      sysProperties.setProperty("user.name", "user");

      Injector injector = Guice.createInjector(new ChefParserModule(), new GsonModule(), new OhaiModule() {
         @Override
         protected Long millis() {
            return 1279992919l;
         }

         @Override
         protected Properties systemProperties() {
            return sysProperties;
         }

      }, new AbstractModule() {

         @Override
         protected void configure() {
            MapBinder<String, Supplier<JsonBall>> mapbinder = ohaiAutomaticAttributeBinder(binder());
            mapbinder.addBinding("test").toProvider(
                  Providers.of(Suppliers.ofInstance(new JsonBall("{\"prop1\":\"test1\"}"))));
         }

      }, new AbstractModule() {

         @Override
         protected void configure() {
            MapBinder<String, Supplier<JsonBall>> mapbinder = ohaiAutomaticAttributeBinder(binder());
            mapbinder.addBinding("test").toProvider(
                  Providers.of(Suppliers.ofInstance(new JsonBall("{\"prop2\":\"test2\"}"))));
         }

      });
      Ohai ohai = injector.getInstance(Ohai.class);
      Json json = injector.getInstance(Json.class);

      assertEquals(
            json.toJson(ohai.ohai.get(), new TypeLiteral<Map<String, JsonBall>>() {
            }.getType()),
            "{\"ohai_time\":1279992919,\"platform\":\"macosx\",\"platform_version\":\"10.3.0\",\"current_user\":\"user\",\"test\":{\"prop1\":\"test1\",\"prop2\":\"test2\"},\"jvm\":{\"system\":{\"user.name\":\"user\",\"os.version\":\"10.3.0\",\"os.name\":\"Mac OS X\"}}}");
   }

   static class Ohai {
      private Supplier<Map<String, JsonBall>> ohai;

      @Inject
      public Ohai(@Automatic Supplier<Map<String, JsonBall>> ohai) {
         this.ohai = ohai;
      }
   }
}
