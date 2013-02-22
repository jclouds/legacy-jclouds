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

import com.google.common.base.Optional;
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
                     return Module.class.cast(loadClass(input).newInstance());
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

   /**
    * Loads a {@link Class} from the current {@link ClassLoader} or the Thread context class loader.
    *
    * @param className
    * @return
    * @throws ClassNotFoundException
    */
   private Class loadClass(String className) throws ClassNotFoundException {
      Optional<ClassLoader> classLoader = Optional.of(getClass().getClassLoader());
      Optional<ClassLoader> threadContextClassLoader = Optional.fromNullable(Thread.currentThread().getContextClassLoader());
      Optional<Class> clazz = tryLoad(className, classLoader).or(tryLoad(className, threadContextClassLoader));
      if (clazz.isPresent()) {
         return clazz.get();
      } else {
         throw new ClassNotFoundException("Failed to load class: " + className);
      }
   }

   /**
    * Attempts to load {@link Class} from the specified {@link ClassLoader}.
    *
    * @param className   The class name.
    * @param classLoader The classLoader to use.
    * @return
    */
   private Optional<Class> tryLoad(String className, Optional<ClassLoader> classLoader) {
      if (!classLoader.isPresent()) {
         return Optional.absent();
      }
      try {
         return Optional.<Class>fromNullable(classLoader.get().loadClass(className));
      } catch (ClassNotFoundException e) {
         return Optional.absent();
      }
   }
}
