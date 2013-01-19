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

package org.jclouds.googlecompute.functions.internal;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.collect.PagedIterables;
import org.jclouds.googlecompute.domain.ListPage;
import org.jclouds.googlecompute.options.ListOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.collect.Iterables.tryFind;

/**
 * @author Adrian Cole
 */
@Beta
public abstract class BaseToPagedIterable<T, I extends BaseToPagedIterable<T, I>> implements
      Function<ListPage<T>, PagedIterable<T>>, InvocationContext<I> {

   private GeneratedHttpRequest request;

   @Override
   public PagedIterable<T> apply(ListPage<T> input) {
      if (input.nextMarker() == null)
         return PagedIterables.of(input);

      Optional<Object> project = tryFind(request.getCaller().get().getArgs(), instanceOf(String.class));

      Optional<Object> listOptions = tryFind(request.getInvocation().getArgs(), instanceOf(ListOptions.class));

      assert project.isPresent() : String.format("programming error, method %s should have a string param for the "
            + "project", request.getCaller().get().getInvokable());

      return PagedIterables.advance(
              input, fetchNextPage(project.get().toString(), (ListOptions) listOptions.orNull()));
   }

   protected abstract Function<Object, IterableWithMarker<T>> fetchNextPage(String projectName,
                                                                            ListOptions listOptions);

   @SuppressWarnings("unchecked")
   @Override
   public I setContext(HttpRequest request) {
      this.request = GeneratedHttpRequest.class.cast(request);
      return (I) this;
   }
}
