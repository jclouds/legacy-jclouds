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

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Map;
import java.util.Properties;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.domain.JsonBall;
import org.jclouds.ohai.WhiteListCompliantJVM;
import org.jclouds.ohai.functions.ByteArrayToMacAddress;
import org.jclouds.util.Utils;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Wires the components needed to parse ohai data from a JVM
 * 
 * @author Adrian Cole
 */
public class BaseOhaiModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(new TypeLiteral<Function<byte[], String>>() {
      }).to(new TypeLiteral<ByteArrayToMacAddress>() {
      });
   }

   @Named("nanoTime")
   @Provides
   protected Long nanoTime() {
      return System.nanoTime();
   }

   @Named("systemProperties")
   @Provides
   protected Properties systemProperties() {
      return System.getProperties();
   }

   @Named("automatic")
   @Provides
   @Singleton
   Supplier<Map<String, JsonBall>> automaticSupplier(
         @Named("automatic") Iterable<Supplier<Map<String, JsonBall>>> suppliers) {
      return Utils.composeMapSupplier(suppliers);
   }

   @Named("automatic")
   @Singleton
   @Provides
   Iterable<Supplier<Map<String, JsonBall>>> suppliers(Injector injector) {
      return ImmutableList.<Supplier<Map<String, JsonBall>>> of(injector.getInstance(WhiteListCompliantJVM.class));
   }

   @Provides
   @Singleton
   protected NetworkInterface provideDefaultNetworkInterface() throws SocketException {
      return NetworkInterface.getNetworkInterfaces().nextElement();
   }

}