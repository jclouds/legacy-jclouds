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
package org.jclouds.glesys.config;

import java.lang.reflect.Type;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.glesys.domain.GleSYSBoolean;
import org.jclouds.glesys.domain.Server;
import org.jclouds.glesys.functions.internal.GleSYSTypeAdapters;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * @author Adrian Cole
 * @author Adam Lowe
 */
public class GleSYSParserModule extends AbstractModule {

   @Provides
   @Singleton
   public Map<Type, Object> provideCustomAdapterBindings() {
      return ImmutableMap.<Type, Object>of(Server.State.class, new GleSYSTypeAdapters.ServerStateAdapter(),
                                           GleSYSBoolean.class, new GleSYSTypeAdapters.GleSYSBooleanAdapter());
   }

   @Override
   protected void configure() {
      bind(DateAdapter.class).to(Iso8601DateAdapter.class);
   }

}
