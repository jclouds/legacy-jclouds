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
package org.jclouds;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.equalTo;
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
import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * @author Adrian Cole
 */
public final class Fallbacks {
   private Fallbacks() {
   }

   public static final class NullOnNotFoundOr404 implements Fallback<Object> {
      public ListenableFuture<Object> create(Throwable t) throws Exception {
         return immediateFuture(createOrPropagate(t));
      }

      public Object createOrPropagate(Throwable t) throws Exception {
         return valOnNotFoundOr404(null, checkNotNull(t, "throwable"));
      }
   }

   public static final class VoidOnNotFoundOr404 implements Fallback<Void> {
      public ListenableFuture<Void> create(Throwable t) throws Exception {
         return immediateFuture(createOrPropagate(t));
      }

      public Void createOrPropagate(Throwable t) throws Exception {
         return valOnNotFoundOr404(null, checkNotNull(t, "throwable"));
      }
   }

   public static final class TrueOnNotFoundOr404 implements Fallback<Boolean> {
      public ListenableFuture<Boolean> create(Throwable t) throws Exception {
         return immediateFuture(createOrPropagate(t));
      }

      public Boolean createOrPropagate(Throwable t) throws Exception {
         return valOnNotFoundOr404(true, checkNotNull(t, "throwable"));
      }
   }

   public static final class FalseOnNotFoundOr404 implements Fallback<Boolean> {
      public ListenableFuture<Boolean> create(Throwable t) throws Exception {
         return immediateFuture(createOrPropagate(t));
      }

      public Boolean createOrPropagate(Throwable t) throws Exception {
         return valOnNotFoundOr404(false, checkNotNull(t, "throwable"));
      }
   }

   public static final class FalseOnNotFoundOr422 implements Fallback<Boolean> {
      public ListenableFuture<Boolean> create(Throwable t) throws Exception {
         return immediateFuture(createOrPropagate(t));
      }

      public Boolean createOrPropagate(Throwable t) throws Exception {
         if (containsResourceNotFoundException(checkNotNull(t, "throwable"))
               || returnValueOnCodeOrNull(t, true, equalTo(422)) != null)
            return false;
         throw propagate(t);
      }
   }

   /**
    * @author Leander Beernaert
    */
   public static final class AbsentOn403Or404Or500 implements Fallback<Optional<Object>> {
      public ListenableFuture<Optional<Object>> create(Throwable t) throws Exception {
         return immediateFuture(createOrPropagate(t));
      }

      public Optional<Object> createOrPropagate(Throwable t) throws Exception {
         Boolean returnVal = returnValueOnCodeOrNull(checkNotNull(t, "throwable"), true, in(asList(403, 404, 500)));
         if (returnVal != null)
            return Optional.absent();
         throw propagate(t);
      }
   }

   public static final class EmptyFluentIterableOnNotFoundOr404 implements Fallback<FluentIterable<Object>> {
      public ListenableFuture<FluentIterable<Object>> create(Throwable t) throws Exception {
         return immediateFuture(createOrPropagate(t));
      }

      public FluentIterable<Object> createOrPropagate(Throwable t) throws Exception {
         return valOnNotFoundOr404(FluentIterable.from(ImmutableSet.of()), checkNotNull(t, "throwable"));
      }
   }

   public static final class EmptyIterableWithMarkerOnNotFoundOr404 implements Fallback<IterableWithMarker<Object>> {
      public ListenableFuture<IterableWithMarker<Object>> create(Throwable t) throws Exception {
         return immediateFuture(createOrPropagate(t));
      }

      public IterableWithMarker<Object> createOrPropagate(Throwable t) throws Exception {
         return valOnNotFoundOr404(IterableWithMarkers.from(ImmutableSet.of()), checkNotNull(t, "throwable"));
      }
   }

   public static final class EmptyPagedIterableOnNotFoundOr404 implements Fallback<PagedIterable<Object>> {
      public ListenableFuture<PagedIterable<Object>> create(Throwable t) throws Exception {
         return immediateFuture(createOrPropagate(t));
      }

      public PagedIterable<Object> createOrPropagate(Throwable t) throws Exception {
         return valOnNotFoundOr404(PagedIterables.of(IterableWithMarkers.from(ImmutableSet.of())),
               checkNotNull(t, "throwable"));
      }
   }

   public static final class EmptyListOnNotFoundOr404 implements Fallback<ImmutableList<Object>> { // NO_UCD
                                                                                                   // (unused
                                                                                                   // code)
      public ListenableFuture<ImmutableList<Object>> create(Throwable t) throws Exception {
         return immediateFuture(createOrPropagate(t));
      }

      public ImmutableList<Object> createOrPropagate(Throwable t) throws Exception {
         return valOnNotFoundOr404(ImmutableList.of(), checkNotNull(t, "throwable"));
      }
   }

   public static final class EmptySetOnNotFoundOr404 implements Fallback<ImmutableSet<Object>> {
      public ListenableFuture<ImmutableSet<Object>> create(Throwable t) throws Exception {
         return immediateFuture(createOrPropagate(t));
      }

      public ImmutableSet<Object> createOrPropagate(Throwable t) throws Exception {
         return valOnNotFoundOr404(ImmutableSet.of(), checkNotNull(t, "throwable"));
      }
   }

   public static final class EmptyMapOnNotFoundOr404 implements Fallback<ImmutableMap<Object, Object>> {
      public ListenableFuture<ImmutableMap<Object, Object>> create(Throwable t) throws Exception {
         return immediateFuture(createOrPropagate(t));
      }

      public ImmutableMap<Object, Object> createOrPropagate(Throwable t) throws Exception {
         return valOnNotFoundOr404(ImmutableMap.of(), checkNotNull(t, "throwable"));
      }
   }

   public static final class EmptyMultimapOnNotFoundOr404 implements Fallback<ImmutableMultimap<Object, Object>> { // NO_UCD
                                                                                                                   // (unused
                                                                                                                   // code)
      public ListenableFuture<ImmutableMultimap<Object, Object>> create(Throwable t) throws Exception {
         return immediateFuture(createOrPropagate(t));
      }

      public ImmutableMultimap<Object, Object> createOrPropagate(Throwable t) throws Exception {
         return valOnNotFoundOr404(ImmutableMultimap.of(), checkNotNull(t, "throwable"));
      }
   }

   public static <T> T valOnNotFoundOr404(T val, Throwable t) {
      if (containsResourceNotFoundException(checkNotNull(t, "throwable")) || contains404(t))
         return val;
      throw propagate(t);
   }

   private static boolean containsResourceNotFoundException(Throwable from) {
      return getFirstThrowableOfType(from, ResourceNotFoundException.class) != null;
   }

}
