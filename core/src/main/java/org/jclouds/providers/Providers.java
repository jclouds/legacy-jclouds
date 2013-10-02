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
package org.jclouds.providers;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static org.jclouds.reflect.Reflection2.typeToken;

import java.util.NoSuchElementException;
import java.util.ServiceLoader;

import org.jclouds.Context;
import org.jclouds.View;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.osgi.ProviderRegistry;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;

/**
 * The Providers class provides static methods for accessing providers.
 * 
 * @author Jeremy Whitlock <jwhitlock@apache.org>
 */
public class Providers {

   public static enum IdFunction implements Function<ProviderMetadata, String> {
      INSTANCE;

      @Override
      public String apply(ProviderMetadata input) {
         return input.getId();
      }

   }

   public static Function<ProviderMetadata, String> idFunction() {
      return IdFunction.INSTANCE;
   }

   public static class ApiMetadataFunction implements Function<ProviderMetadata, ApiMetadata> {
      @Override
      public ApiMetadata apply(ProviderMetadata input) {
         return input.getApiMetadata();
      }

   }

   public static Function<ProviderMetadata, ApiMetadata> apiMetadataFunction() {
      return new ApiMetadataFunction();
   }

   /**
    * Returns the providers located on the classpath via {@link java.util.ServiceLoader}.
    * 
    * @return all available providers loaded from classpath via ServiceLoader
    */
   public static Iterable<ProviderMetadata> fromServiceLoader() {
      return ServiceLoader.load(ProviderMetadata.class);
   }

   /**
    * Returns all available providers.
    * 
    * @return all available providers
    */
   public static Iterable<ProviderMetadata> all() {
     return ImmutableSet.<ProviderMetadata>builder()
                        .addAll(fromServiceLoader())
                        .addAll(ProviderRegistry.fromRegistry()).build();
   }

   /**
    * Returns the first provider with the provided id
    * 
    * @param id
    *           the id of the provider to return
    * 
    * @return the provider with the given id
    * 
    * @throws NoSuchElementException
    *            whenever there are no providers with the provided id
    */
   public static ProviderMetadata withId(String id) throws NoSuchElementException {
      return find(all(), ProviderPredicates.id(id));
   }

   /**
    * Returns the providers that are of the provided viewableAs.
    * 
    * @param viewableAs
    *           the viewableAs to providers to return
    * 
    * @return the providers of the provided viewableAs
    */
   public static Iterable<ProviderMetadata> viewableAs(TypeToken<? extends View> viewableAs) {
      return filter(all(), ProviderPredicates.viewableAs(viewableAs));
   }

   public static Iterable<ProviderMetadata> viewableAs(Class<? extends View> viewableAs) {
      return filter(all(), ProviderPredicates.viewableAs(typeToken(viewableAs)));
   }

   /**
    * Returns the providers that are of the provided api.
    * 
    * @param api
    *           the api to providers to return
    * 
    * @return the providers of the provided api
    */
   public static Iterable<ProviderMetadata> apiMetadataAssignableFrom(TypeToken<? extends ApiMetadata> api) {
      Preconditions.checkNotNull(api, "api must be defined");
      return filter(all(), ProviderPredicates.apiMetadataAssignableFrom(api));
   }

   /**
    * Returns the providers that are of the provided context.
    * 
    * @param context
    *           the context to providers to return
    * 
    * @return the providers of the provided context
    */
   public static <C extends Context> Iterable<ProviderMetadata> contextAssignableFrom(
            TypeToken<? extends Context> context) {
      Preconditions.checkNotNull(context, "context must be defined");
      return filter(all(), new ProviderPredicates.ContextAssignableFrom(context));
   }

   /**
    * Returns the providers that are bound to the same location as the given ISO 3166 code
    * regardless of viewableAs.
    * 
    * @param isoCode
    *           the ISO 3166 code to filter providers by
    * 
    * @return the providers bound by the given ISO 3166 code
    */
   public static Iterable<ProviderMetadata> boundedByIso3166Code(String iso3166Code) {
      return filter(all(), ProviderPredicates.boundedByIso3166Code(iso3166Code));
   }

   /**
    * Returns the providers that are bound to the same location as the given ISO 3166 code and of
    * the given viewableAs.
    * 
    * @param iso3166Code
    *           the ISO 3166 code to filter providers by
    * @param viewableAs
    *           the viewableAs to filter providers by
    * 
    * @return the providers bound by the given ISO 3166 code and of the proper viewableAs
    */
   public static Iterable<ProviderMetadata> boundedByIso3166Code(String iso3166Code,
            TypeToken<? extends View> viewableAs) {
      return filter(all(), Predicates.and(ProviderPredicates.boundedByIso3166Code(iso3166Code), ProviderPredicates
               .viewableAs(viewableAs)));
   }

   public static Iterable<ProviderMetadata> boundedByIso3166Code(String iso3166Code,
            Class<? extends View> viewableAs) {
      return boundedByIso3166Code(iso3166Code, typeToken(viewableAs));
   }

   /**
    * Returns the providers that have at least one common ISO 3166 code in common regardless of
    * viewableAs.
    * 
    * @param providerMetadata
    *           the provider metadata to use to filter providers by
    * 
    * @return the providers that share at least one common ISO 3166 code
    */
   public static Iterable<ProviderMetadata> collocatedWith(ProviderMetadata providerMetadata) {
      return filter(all(), ProviderPredicates.intersectingIso3166Code(providerMetadata));
   }

   /**
    * Returns the providers that have at least one common ISO 3166 code and are of the given
    * viewableAs.
    * 
    * @param providerMetadata
    *           the provider metadata to use to filter providers by
    * @param viewableAs
    *           the viewableAs to filter providers by
    * 
    * @return the providers that share at least one common ISO 3166 code and of the given
    *         viewableAs
    */
   public static Iterable<ProviderMetadata> collocatedWith(ProviderMetadata providerMetadata,
            TypeToken<? extends View> viewableAs) {
      return filter(all(), Predicates.and(ProviderPredicates.intersectingIso3166Code(providerMetadata),
               ProviderPredicates.viewableAs(viewableAs)));
   }

   public static Iterable<ProviderMetadata> collocatedWith(ProviderMetadata providerMetadata,
            Class<? extends View> viewableAs) {
      return collocatedWith(providerMetadata, typeToken(viewableAs));
   }
}
