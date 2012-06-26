/*
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
package org.jclouds.nodepool.config;

import java.util.Set;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.nodepool.Backend;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Module;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;

public class BindJcloudsModules extends PrivateModule {

   @Override
   protected void configure() {
   }

   @Provides
   @Singleton
   @Backend
   protected Set<Module> provideBackendModules(@Named(NodePoolProperties.BACKEND_MODULES) String moduleString) {
      return ImmutableSet.copyOf(Iterables.transform(Splitter.on(',').split(moduleString),
            new Function<String, Module>() {

               @Override
               public Module apply(String input) {
                  try {
                     return Module.class.cast(Class.forName(input).newInstance());
                  } catch (InstantiationException e) {
                     throw Throwables.propagate(e);
                  } catch (IllegalAccessException e) {
                     throw Throwables.propagate(e);
                  } catch (ClassNotFoundException e) {
                     throw Throwables.propagate(e);
                  }
               }
            }));
   }
}
