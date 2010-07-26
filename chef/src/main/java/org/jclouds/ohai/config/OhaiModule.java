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

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.domain.JsonBall;
import org.jclouds.ohai.JMX;
import org.jclouds.ohai.WhiteListCompliantJVM;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 * Wires the components needed to parse ohai data from a JVM
 * 
 * @author Adrian Cole
 */
public class OhaiModule extends BaseOhaiModule {

   @Provides
   @Singleton
   protected RuntimeMXBean provideRuntimeMXBean() {
      return ManagementFactory.getRuntimeMXBean();
   }

   @Override
   Iterable<Supplier<Map<String, JsonBall>>> suppliers(Injector injector) {
      return ImmutableList.<Supplier<Map<String, JsonBall>>> of(injector.getInstance(WhiteListCompliantJVM.class),
            injector.getInstance(JMX.class));
   }
}