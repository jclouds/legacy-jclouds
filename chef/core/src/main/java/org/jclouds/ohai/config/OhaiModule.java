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
import static org.jclouds.chef.util.ChefUtils.toOhaiTime;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.domain.JsonBall;
import org.jclouds.json.Json;
import org.jclouds.ohai.Automatic;
import org.jclouds.ohai.AutomaticSupplier;
import org.jclouds.ohai.config.multibindings.MapBinder;
import org.jclouds.ohai.functions.ByteArrayToMacAddress;
import org.jclouds.ohai.functions.MapSetToMultimap;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Multimap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Wires the components needed to parse ohai data
 * 
 * @author Adrian Cole
 */
@ConfiguresOhai
public class OhaiModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(new TypeLiteral<Function<byte[], String>>() {
      }).to(new TypeLiteral<ByteArrayToMacAddress>() {
      });
      bindOhai();
   }

   @Provides
   @Automatic
   protected Supplier<Map<String, JsonBall>> provideAutomatic(AutomaticSupplier in) {
      return in;
   }

   @Provides
   @Automatic
   Multimap<String, Supplier<JsonBall>> provideAutomatic(MapSetToMultimap<String, Supplier<JsonBall>> converter,
         @Automatic Map<String, Set<Supplier<JsonBall>>> input) {
      return converter.apply(input);

   }

   @Named("systemProperties")
   @Provides
   protected Properties systemProperties() {
      return System.getProperties();
   }

   public MapBinder<String, Supplier<JsonBall>> bindOhai() {
      MapBinder<String, Supplier<JsonBall>> mapbinder = ohaiAutomaticAttributeBinder(binder()).permitDuplicates();
      mapbinder.addBinding("ohai_time").to(OhaiTimeProvider.class);
      mapbinder.addBinding("jvm/system").to(SystemPropertiesProvider.class);
      mapbinder.addBinding("platform").to(PlatformProvider.class);
      mapbinder.addBinding("platform_version").to(PlatformVersionProvider.class);
      mapbinder.addBinding("current_user").to(CurrentUserProvider.class);
      return mapbinder;
   }

   @Singleton
   public static class OhaiTimeProvider implements Supplier<JsonBall> {
      private final Provider<Long> timeProvider;

      @Inject
      OhaiTimeProvider(Provider<Long> timeProvider) {
         this.timeProvider = timeProvider;
      }

      @Override
      public JsonBall get() {
         return toOhaiTime(timeProvider.get());
      }

   }

   @Provides
   protected Long millis() {
      return System.currentTimeMillis();
   }

   @Singleton
   public static class SystemPropertiesProvider implements Supplier<JsonBall> {

      private final Json json;
      private final Properties systemProperties;

      @Inject
      SystemPropertiesProvider(Json json, @Named("systemProperties") Properties systemProperties) {
         this.json = json;
         this.systemProperties = systemProperties;
      }

      @Override
      public JsonBall get() {
         return new JsonBall(json.toJson(systemProperties));
      }

   }

   @Singleton
   public static class PlatformProvider extends SystemPropertyProvider {

      @Inject
      PlatformProvider(@Named("systemProperties") Properties systemProperties) {
         super("os.name", systemProperties);
      }

      @Override
      public JsonBall get() {
         JsonBall returnValue = super.get();
         return returnValue != null ? new JsonBall(returnValue.toString().replaceAll("[ -]", "").toLowerCase()) : null;
      }

   }

   @Singleton
   public static class PlatformVersionProvider extends SystemPropertyProvider {

      @Inject
      PlatformVersionProvider(@Named("systemProperties") Properties systemProperties) {
         super("os.version", systemProperties);
      }

   }

   @Singleton
   public static class CurrentUserProvider extends SystemPropertyProvider {

      @Inject
      CurrentUserProvider(@Named("systemProperties") Properties systemProperties) {
         super("user.name", systemProperties);
      }

   }

   public static class SystemPropertyProvider implements Supplier<JsonBall> {
      private final Properties systemProperties;
      private final String property;

      @Inject
      SystemPropertyProvider(String property, @Named("systemProperties") Properties systemProperties) {
         this.property = property;
         this.systemProperties = systemProperties;
      }

      @Override
      public JsonBall get() {
         return systemProperties.containsKey(property) ? new JsonBall(systemProperties.getProperty(property)) : null;
      }

   }

}