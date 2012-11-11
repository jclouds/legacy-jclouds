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
package org.jclouds.rest.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.internal.ClassMethodArgsAndReturnVal;
import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.rest.annotations.SinceApiVersion;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * 
 * @author Adrian Cole
 */
@Beta
@Singleton
public class PresentWhenApiVersionLexicographicallyAtOrAfterSinceApiVersion implements ImplicitOptionalConverter {

   @VisibleForTesting
   static final class Loader extends CacheLoader<ClassMethodArgsAndReturnVal, Optional<Object>> {
      private final String apiVersion;

      @Inject
      Loader(@ApiVersion String apiVersion) {
         this.apiVersion = checkNotNull(apiVersion, "apiVersion");
      }

      @Override
      public Optional<Object> load(ClassMethodArgsAndReturnVal input) {
         Optional<SinceApiVersion> sinceApiVersion = Optional.fromNullable(input.getClazz().getAnnotation(
               SinceApiVersion.class));
         if (sinceApiVersion.isPresent()) {
            String since = sinceApiVersion.get().value();
            if (since.compareTo(apiVersion) <= 0)
               return Optional.of(input.getReturnVal());
            return Optional.absent();
         } else {
            // No SinceApiVersion annotation, so return present
            return Optional.of(input.getReturnVal());
         }
      }
   }

   private final LoadingCache<ClassMethodArgsAndReturnVal, Optional<Object>> lookupCache;

   @Inject
   protected PresentWhenApiVersionLexicographicallyAtOrAfterSinceApiVersion(@ApiVersion String apiVersion) {
      // no need to read class annotations for every request
      this.lookupCache = CacheBuilder.newBuilder().build(new Loader(apiVersion));
   }

   @Override
   public Optional<Object> apply(ClassMethodArgsAndReturnVal input) {
      return lookupCache.getUnchecked(input);
   }

}
