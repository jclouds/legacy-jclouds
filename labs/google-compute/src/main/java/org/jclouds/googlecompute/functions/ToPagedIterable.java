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

package org.jclouds.googlecompute.functions;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.collect.PagedIterables;
import org.jclouds.googlecompute.GoogleComputeApi;
import org.jclouds.googlecompute.domain.Resource;
import org.jclouds.googlecompute.options.ListOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import javax.inject.Inject;
import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Used to annotate list() methods to transform PagedList to PagedIterable. The methods are expected to receive the
 * project name as the first argument and ListOptions as the second, so this only actually builds a PagedIterable
 * that advances when annotating methods such as {@link org.jclouds.googlecompute.features.OperationApi#list(String,
 * org.jclouds.googlecompute.options.ListOptions)}
 *
 * @author David Alves
 */
public class ToPagedIterable implements Function<IterableWithMarker<?>, PagedIterable<?>>,
        InvocationContext<ToPagedIterable> {

   private GoogleComputeApi api;

   @Inject
   public ToPagedIterable(GoogleComputeApi api) {
      this.api = api;
   }

   private GeneratedHttpRequest request;

   @Override
   public PagedIterable<?> apply(IterableWithMarker<?> input) {
      if (input.nextMarker() == null || request == null)
         return new UwrappablePagedIterable(input);

      checkNotNull(request);
      checkState(
              request.getJavaMethod().getName().equals("list")
                      && request.getJavaMethod().getParameterTypes().length == 2
                      && request.getJavaMethod().getParameterTypes()[0] == String.class
                      && request.getJavaMethod().getParameterTypes()[1] == ListOptions.class
              , "ToPagedIterable can only be applied to list() methods that received to arguments (String and " +
              "ListOptions) and return PagedIterable " + request.getJavaMethod());


      checkNotNull(request.getArgs().size() > 0);
      final String projectName = (String) request.getArgs().get(0);
      final ListOptions current = request.getArgs().size() == 2 ? (ListOptions)
              request.getArgs().get(1) : ListOptions.NONE;

      // get synch feature api required to advance the page
      final Object featureApi;
      try {
         featureApi = api.getClass().getMethod("get" + request.getDeclaring().getSimpleName().replace
                 ("Async", "")).invoke(api);
      } catch (Exception e) {
         throw Throwables.propagate(e);
      }

      return advance((IterableWithMarker<Resource>) input, new Function<Object, IterableWithMarker<Resource>>() {
         @Override
         public IterableWithMarker<Resource> apply(Object input) {
            ListOptions next = current.toBuilder().nextPageToken(input.toString()).build();
            try {
               // for the following calls must get the wrapped iterable with marker and not the pagediterable
               UwrappablePagedIterable<Resource> wrapped = (UwrappablePagedIterable<Resource>) featureApi.getClass()
                       .getMethod("list", String.class, ListOptions.class).invoke(featureApi, projectName, next);
               return wrapped.wrapped;
            } catch (Exception e) {
               throw Throwables.propagate(e);
            }
         }
      });
   }

   @Override
   public ToPagedIterable setContext(HttpRequest request) {
      this.request = GeneratedHttpRequest.class.cast(request);
      return this;
   }

   /**
    * Needed to unwrap the underlying IterableWithMarker in successive list() calls
    * to avoid replicating boilerplate to all list() methods and to avoid the need to have 2 different methods
    */
   protected static class UwrappablePagedIterable<T> extends PagedIterable<T> {

      IterableWithMarker<T> wrapped;

      UwrappablePagedIterable(IterableWithMarker<T> wrapped) {
         this.wrapped = wrapped;
      }

      @Override
      public Iterator<IterableWithMarker<T>> iterator() {
         return ImmutableSet.of(wrapped).iterator();
      }
   }

   public static <T> PagedIterable<T> advance(final IterableWithMarker<T> initial,
                                              final Function<Object, IterableWithMarker<T>> markerToNext) {
      return new UwrappablePagedIterable<T>(initial) {
         @Override
         public Iterator<IterableWithMarker<T>> iterator() {
            return PagedIterables.advancingIterator(initial, markerToNext);
         }

      };
   }
}
