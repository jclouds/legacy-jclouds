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
package org.jclouds.gogrid.config;

import java.lang.reflect.Type;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.gogrid.domain.IpState;
import org.jclouds.gogrid.domain.JobState;
import org.jclouds.gogrid.domain.LoadBalancerOs;
import org.jclouds.gogrid.domain.LoadBalancerPersistenceType;
import org.jclouds.gogrid.domain.LoadBalancerState;
import org.jclouds.gogrid.domain.LoadBalancerType;
import org.jclouds.gogrid.domain.ObjectType;
import org.jclouds.gogrid.domain.ServerImageState;
import org.jclouds.gogrid.domain.ServerImageType;
import org.jclouds.gogrid.domain.ServerState;
import org.jclouds.gogrid.functions.internal.CustomDeserializers;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.LongDateAdapter;

import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the GoGrid connection.
 * 
 * @author Adrian Cole
 * @author Oleksiy Yarmula
 */

public class GoGridParserModule extends AbstractModule {

   @Provides
   @Singleton
   public Map<Type, Object> provideCustomAdapterBindings() {
      Map<Type, Object> bindings = Maps.newHashMap();
      bindings.put(ObjectType.class, new CustomDeserializers.ObjectTypeAdapter());
      bindings.put(LoadBalancerOs.class, new CustomDeserializers.LoadBalancerOsAdapter());
      bindings.put(LoadBalancerState.class, new CustomDeserializers.LoadBalancerStateAdapter());
      bindings.put(LoadBalancerPersistenceType.class, new CustomDeserializers.LoadBalancerPersistenceTypeAdapter());
      bindings.put(LoadBalancerType.class, new CustomDeserializers.LoadBalancerTypeAdapter());
      bindings.put(ServerState.class, new CustomDeserializers.ServerStateAdapter());
      bindings.put(IpState.class, new CustomDeserializers.IpStateAdapter());
      bindings.put(JobState.class, new CustomDeserializers.JobStateAdapter());
      bindings.put(ServerImageState.class, new CustomDeserializers.ServerImageStateAdapter());
      bindings.put(ServerImageType.class, new CustomDeserializers.ServerImageTypeAdapter());
      return bindings;
   }

   @Override
   protected void configure() {
      bind(DateAdapter.class).to(LongDateAdapter.class);
   }

}
