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
package org.jclouds.osgi;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.notNull;
import static org.jclouds.util.Strings2.toStringAndClose;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.osgi.framework.Bundle;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

/**
 * Utility functions helpful in working with {@link Bundle bundles}.
 * 
 * @author Adrian Cole
 */
public final class Bundles {
   private Bundles() {
   }

   /**
    * instantiates the supplied classnames using the bundle classloader, and casts to the supplied type. Any errors are
    * silently ignored.
    * 
    * @return instances that could be instantiated without error.
    */
   public static <T> ImmutableSet<T> instantiateAvailableClasses(Bundle bundle, Iterable<String> classNames,
         Class<T> type) {
      checkNotNull(bundle, "bundle");
      checkNotNull(classNames, "classNames");
      checkNotNull(type, "type");
      return FluentIterable.from(classNames)
                           .transform(loadClassIfAssignableFrom(bundle, type))
                           .filter(notNull())
                           .transform(instantiateIfPossible(type))
                           .filter(notNull())
                           .toSet();
   }

   /**
    * A function that loads classes from the bundle, or returns null if the class isn't found or assignable by the input
    * parameter
    * 
    * @param bundle
    *           where to find classes
    * @param clazz
    *           type classes must be assignable from
    */
   private static Function<String, Class<?>> loadClassIfAssignableFrom(final Bundle bundle, final Class<?> clazz) {
      checkNotNull(bundle, "bundle");
      checkNotNull(clazz, "clazz");
      return new Function<String, Class<?>>() {
         @Override
         public Class<?> apply(String in) {
            checkNotNull(in, "classname");
            try {
               Class<?> thing = bundle.loadClass(in);
               // Classes loaded by other class loaders are not assignable.
               if (clazz.isAssignableFrom(thing))
                  return thing.asSubclass(clazz);
            } catch (ClassNotFoundException e) {
            }
            return null;
         }
      };
   }

   /**
    * A function that instantiates classes or returns null, if it encounters any problems.
    * 
    * @param clazz
    *           superclass to cast as
    */
   private static <T> Function<Class<?>, T> instantiateIfPossible(final Class<T> clazz) {
      return new Function<Class<?>, T>() {
         @Override
         public T apply(Class<?> in) {
            checkNotNull(in, "input class");
            try {
               return clazz.cast(in.newInstance());
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            }
            return null;
         }
      };
   }

   /**
    * Reads the resource from a {@link Bundle}.
    * 
    * @param resourcePath
    *           The path to the resource.
    * @param bundle
    *           The bundle to read from.
    * @return strings delimited by newline in the stream or empty set, on any exception.
    */
   public static ImmutableSet<String> stringsForResourceInBundle(String resourcePath, Bundle bundle) {
      checkNotNull(resourcePath, "resourcePath");
      checkNotNull(bundle, "bundle");

      URL resource = bundle.getEntry(resourcePath);
      if (resource == null)
         return ImmutableSet.of();
      try {
         return ImmutableSet.copyOf(splitOrEmptyAndClose(resource.openStream()));
      } catch (IOException e) {
      } catch (RuntimeException ex) {
      }
      return ImmutableSet.of();
   }

   private static Iterable<String> splitOrEmptyAndClose(InputStream in) throws IOException {
      return Splitter.on('\n').omitEmptyStrings().split(toStringAndClose(in));
   }
}
