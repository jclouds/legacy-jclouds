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
package org.jclouds.rest;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.transform;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.jclouds.javax.annotation.Nullable;

import org.jclouds.PropertiesBuilder;
import org.jclouds.util.SaxUtils;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;

/**
 * 
 * @author Adrian Cole
 */
public class Providers {

   /**
    * Gets a set of supported providers. Idea stolen from pallets (supported-clouds). Uses
    * rest.properties to populate the set.
    * 
    */
   public static Iterable<String> getSupportedProviders() {
      return Providers.getSupportedProvidersOfType(RestContextBuilder.class);
   }

   /**
    * Gets a set of supported providers. Idea stolen from pallets (supported-clouds). Uses
    * rest.properties to populate the set.
    * 
    */
   public static Iterable<String> getSupportedProvidersOfType(
         @SuppressWarnings("rawtypes") Class<? extends RestContextBuilder> type) {
      Properties properties = new Properties();
      try {
         properties.load(SaxUtils.class.getResourceAsStream("/rest.properties"));
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
      return Providers.getSupportedProvidersOfTypeInProperties(type, properties);
   }

   public static Iterable<String> getSupportedProvidersOfTypeInProperties(
         @SuppressWarnings("rawtypes") final Class<? extends RestContextBuilder> type, final Properties properties) {
      return filter(transform(filter(properties.entrySet(), new Predicate<Map.Entry<Object, Object>>() {

         @Override
         public boolean apply(Entry<Object, Object> input) {
            String keyString = input.getKey().toString();
            return keyString.endsWith(".contextbuilder") || keyString.endsWith(".sync");
         }

      }), new Function<Map.Entry<Object, Object>, String>() {

         @Override
         public String apply(Entry<Object, Object> from) {
            String keyString = from.getKey().toString();
            try {
               String provider = get(Splitter.on('.').split(keyString), 0);
               Class<RestContextBuilder<Object, Object>> clazz = Providers.resolveContextBuilderClass(provider,
                     properties);
               if (type.isAssignableFrom(clazz))
                  return provider;
            } catch (ClassNotFoundException e) {
            } catch (Exception e) {
               propagate(e);
            }
            return null;
         }

      }), notNull());
   }

   @SuppressWarnings("unchecked")
   public static <S, A> Class<RestContextBuilder<S, A>> resolveContextBuilderClass(String provider,
         Properties properties) throws ClassNotFoundException, IllegalArgumentException, SecurityException,
         InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
      String contextBuilderClassName = properties.getProperty(provider + ".contextbuilder");
      String syncClassName = properties.getProperty(provider + ".sync");
      String asyncClassName = properties.getProperty(provider + ".async");
      if (syncClassName != null) {
         checkArgument(asyncClassName != null, "please configure async class for " + syncClassName);
         Class.forName(syncClassName);
         Class.forName(asyncClassName);
         return (Class<RestContextBuilder<S, A>>) (contextBuilderClassName != null ? Class
               .forName(contextBuilderClassName) : RestContextBuilder.class);
      } else {
         checkArgument(contextBuilderClassName != null, "please configure contextbuilder class for " + provider);
         return (Class<RestContextBuilder<S, A>>) Class.forName(contextBuilderClassName);
      }
   }

   public static <S, A> RestContextBuilder<S, A> initContextBuilder(
         Class<RestContextBuilder<S, A>> contextBuilderClass, @Nullable Class<S> sync, @Nullable Class<A> async,
         Properties properties) throws ClassNotFoundException, IllegalArgumentException, SecurityException,
         InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
      checkArgument(properties != null, "please configure properties for " + contextBuilderClass);
      try {
         return (RestContextBuilder<S, A>) contextBuilderClass.getConstructor(Properties.class).newInstance(properties);
      } catch (NoSuchMethodException e) {
         checkArgument(sync != null, "please configure sync class for " + contextBuilderClass);
         checkArgument(async != null, "please configure async class for " + contextBuilderClass);
         return (RestContextBuilder<S, A>) contextBuilderClass.getConstructor(sync.getClass(), async.getClass(),
               Properties.class).newInstance(sync, async, properties);
      }
   }

   @SuppressWarnings("unchecked")
   public static Class<PropertiesBuilder> resolvePropertiesBuilderClass(String providerName, Properties props)
         throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException,
         NoSuchMethodException {
      String propertiesBuilderClassName = props.getProperty(providerName + ".propertiesbuilder", null);
      if (propertiesBuilderClassName != null) {
         return (Class<PropertiesBuilder>) Class.forName(propertiesBuilderClassName);
      } else {
         return PropertiesBuilder.class;
      }
   }

}
