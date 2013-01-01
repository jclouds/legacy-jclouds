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
package org.jclouds;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.in;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.primitives.Ints.asList;
import static com.google.common.util.concurrent.Futures.immediateFuture;
import static org.jclouds.http.HttpUtils.contains404;
import static org.jclouds.http.HttpUtils.returnValueOnCodeOrNull;
import static org.jclouds.util.Throwables2.getFirstThrowableOfType;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.IterableWithMarkers;
import org.jclouds.collect.PagedIterable;
import org.jclouds.collect.PagedIterables;
import org.jclouds.rest.ResourceNotFoundException;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.FutureFallback;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * @author Adrian Cole
 */
public final class Fallbacks {
   private Fallbacks() {
   }

   public static final class NullOnNotFoundOr404 implements FutureFallback<Object> {

      @Override
      public ListenableFuture<Object> create(Throwable t) {
         return valOnNotFoundOr404(null, checkNotNull(t, "throwable"));
      }

   }

   public static final class VoidOnNotFoundOr404 implements FutureFallback<Void> {

      @Override
      public ListenableFuture<Void> create(Throwable t) {
         return valOnNotFoundOr404(null, checkNotNull(t, "throwable"));
      }

   }

   public static final class TrueOnNotFoundOr404 implements FutureFallback<Boolean> {

      @Override
      public ListenableFuture<Boolean> create(Throwable t) {
         return valOnNotFoundOr404(true, checkNotNull(t, "throwable"));
      }

   }

   public static final class FalseOnNotFoundOr404 implements FutureFallback<Boolean> {

      @Override
      public ListenableFuture<Boolean> create(Throwable t) {
         return valOnNotFoundOr404(false, checkNotNull(t, "throwable"));
      }

   }

   /**
    * @author Leander Beernaert
    */
   public static final class AbsentOn403Or404Or500 implements FutureFallback<Optional<Object>> {
      @Override
      public ListenableFuture<Optional<Object>> create(Throwable t) {
         Boolean returnVal = returnValueOnCodeOrNull(checkNotNull(t, "throwable"), true, in(asList(403, 404, 500)));
         if (returnVal != null)
            return immediateFuture(Optional.absent());
         throw propagate(t);

      }

   }

   public static final class EmptyFluentIterableOnNotFoundOr404 implements FutureFallback<FluentIterable<Object>> {
      @Override
      public ListenableFuture<FluentIterable<Object>> create(Throwable t) {
         return valOnNotFoundOr404(FluentIterable.from(ImmutableSet.of()), checkNotNull(t, "throwable"));
      }
   }

   public static final class EmptyIterableWithMarkerOnNotFoundOr404 implements
         FutureFallback<IterableWithMarker<Object>> {
      @Override
      public ListenableFuture<IterableWithMarker<Object>> create(Throwable t) {
         return valOnNotFoundOr404(IterableWithMarkers.from(ImmutableSet.of()), checkNotNull(t, "throwable"));
      }
   }

   public static final class EmptyPagedIterableOnNotFoundOr404 implements FutureFallback<PagedIterable<Object>> {
      @Override
      public ListenableFuture<PagedIterable<Object>> create(Throwable t) {
         return valOnNotFoundOr404(PagedIterables.of(IterableWithMarkers.from(ImmutableSet.of())),
               checkNotNull(t, "throwable"));
      }
   }

   public static final class EmptyListOnNotFoundOr404 implements FutureFallback<ImmutableList<Object>> {
      @Override
      public ListenableFuture<ImmutableList<Object>> create(Throwable t) {
         return valOnNotFoundOr404(ImmutableList.of(), checkNotNull(t, "throwable"));
      }
   }

   public static final class EmptySetOnNotFoundOr404 implements FutureFallback<ImmutableSet<Object>> {
      @Override
      public ListenableFuture<ImmutableSet<Object>> create(Throwable t) {
         return valOnNotFoundOr404(ImmutableSet.of(), checkNotNull(t, "throwable"));
      }
   }

   public static final class EmptyMapOnNotFoundOr404 implements FutureFallback<ImmutableMap<Object, Object>> {
      @Override
      public ListenableFuture<ImmutableMap<Object, Object>> create(Throwable t) {
         return valOnNotFoundOr404(ImmutableMap.of(), checkNotNull(t, "throwable"));
      }
   }

   public static final class EmptyMultimapOnNotFoundOr404 implements FutureFallback<ImmutableMultimap<Object, Object>> {
      @Override
      public ListenableFuture<ImmutableMultimap<Object, Object>> create(Throwable t) {
         return valOnNotFoundOr404(ImmutableMultimap.of(), checkNotNull(t, "throwable"));
      }
   }

   public static <T> ListenableFuture<T> valOnNotFoundOr404(T val, Throwable t) {
      if (containsResourceNotFoundException(checkNotNull(t, "throwable")) || contains404(t))
         return immediateFuture(val);
      throw propagate(t);
   }

   private static boolean containsResourceNotFoundException(Throwable from) {
      return getFirstThrowableOfType(from, ResourceNotFoundException.class) != null;
   }

}