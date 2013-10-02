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

import static com.google.common.base.Preconditions.checkArgument;
import static org.jclouds.collect.PagedIterables.advance;
import static org.jclouds.collect.PagedIterables.onlyPage;

import java.util.List;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;

/**
 * Used to propagate the invoked method arguments during an advance in a
 * {@link PagedIterable}.
 * <p>
 * In order to fetch the next page in the result set, subclasses may need to
 * have the context of the initial request. This class propagates the arguments
 * used in the original request, so they can be used to fetch the next page in
 * the result set.
 * 
 * @author Ignasi Barrera
 */
@Beta
public abstract class ArgsToPagedIterable<T, I extends ArgsToPagedIterable<T, I>> implements
      Function<IterableWithMarker<T>, PagedIterable<T>>, InvocationContext<I> {

   protected GeneratedHttpRequest request;

   @Override
   public PagedIterable<T> apply(IterableWithMarker<T> input) {
      return input.nextMarker().isPresent() ? advance(input, markerToNextForArgs(getArgs(request))) : onlyPage(input);
   }

   protected List<Object> getArgs(GeneratedHttpRequest request) {
      return request.getInvocation().getArgs();
   }

   protected abstract Function<Object, IterableWithMarker<T>> markerToNextForArgs(List<Object> args);

   @SuppressWarnings("unchecked")
   @Override
   public I setContext(HttpRequest request) {
      checkArgument(request instanceof GeneratedHttpRequest,
            "ArgsToPagedIterable only supports a GeneratedHttpRequest");
      this.request = GeneratedHttpRequest.class.cast(request);
      return (I) this;
   }

   /**
    * Sometimes the arguments in the invoked method do not provide enough
    * information to fetch the next page of the result set. This, for example,
    * is common in APIs dealing with several endpoints.
    * <p>
    * This class provides a way to propagate the arguments passed to the caller
    * of the method, so they can be used to fetch the next page of the result
    * set. For example, in the call {@code api.getUserApiForZone(zone).list()},
    * the caller arg0 is the value of {@code zone}, and the callee is
    * {@code UserApi.list()}
    * 
    * @author Adrian Cole
    * @see ParseImages function in openstack-glance for a usage example.
    */
   public abstract static class FromCaller<T, I extends FromCaller<T, I>> extends ArgsToPagedIterable<T, I> {
      @Override
      protected List<Object> getArgs(GeneratedHttpRequest request) {
         return request.getCaller().get().getArgs();
      }
   }
}
