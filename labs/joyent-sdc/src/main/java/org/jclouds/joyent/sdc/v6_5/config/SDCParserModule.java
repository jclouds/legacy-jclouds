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
package org.jclouds.joyent.sdc.v6_5.config;

import java.lang.reflect.Type;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.joyent.sdc.v6_5.domain.Machine;
import org.jclouds.joyent.sdc.v6_5.functions.internal.SDCTypeAdapters;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * @author Adrian Cole
 */
public class SDCParserModule extends AbstractModule {

   @Provides
   @Singleton
   public Map<Type, Object> provideCustomAdapterBindings() {
      return ImmutableMap.<Type, Object> of(Machine.State.class, new SDCTypeAdapters.ServerStateAdapter(), Type.class,
            new SDCTypeAdapters.SDCTypeAdapter());
   }

   @Override
   protected void configure() {
   }

}
