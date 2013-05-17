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
package org.jclouds.collect.internal;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.collect.PagedIterables;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Optional;

/**
 * @author Adrian Cole
 * @deprecated Arg0ToPagedIterable.FromCaller
 */
@Beta
@Deprecated
public abstract class CallerArg0ToPagedIterable<T, I extends CallerArg0ToPagedIterable<T, I>> implements
      Function<IterableWithMarker<T>, PagedIterable<T>>, InvocationContext<I> {

   private GeneratedHttpRequest request;

   @Override
   public PagedIterable<T> apply(IterableWithMarker<T> input) {
      if (input.nextMarker() == null)
         return PagedIterables.of(input);

      Optional<String> arg0Option = Optional.absent();
      if (request.getCaller().get().getArgs().size() > 0) {
         Object arg0 = request.getCaller().get().getArgs().get(0);
         if (arg0 != null)
            arg0Option = Optional.of(arg0.toString());
      }
      final String arg0 = arg0Option.orNull();
      return PagedIterables.advance(input, markerToNextForCallingArg0(arg0));
   }

   protected abstract Function<Object, IterableWithMarker<T>> markerToNextForCallingArg0(String arg0);

   @SuppressWarnings("unchecked")
   @Override
   public I setContext(HttpRequest request) {
      this.request = GeneratedHttpRequest.class.cast(request);
      return (I) this;
   }

}
