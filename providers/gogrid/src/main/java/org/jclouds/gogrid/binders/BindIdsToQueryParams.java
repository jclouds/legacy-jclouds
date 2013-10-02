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
package org.jclouds.gogrid.binders;

import static com.google.common.base.Functions.toStringFunction;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.gogrid.reference.GoGridQueryParams.ID_KEY;

import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Longs;

/**
 * Binds IDs to corresponding query parameters
 * 
 * @author Oleksiy Yarmula
 */
@Singleton
public class BindIdsToQueryParams implements Binder {

   /**
    * Binds the ids to query parameters. The pattern, as specified by GoGrid's specification, is:
    * 
    * https://api.gogrid.com/api/grid/server/get ?id=5153 &id=3232
    * 
    * @param request
    *           request where the query params will be set
    * @param input
    *           array of String params
    */
   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {

      if (checkNotNull(input, "input is null") instanceof Long[]) {
         Long[] names = (Long[]) input;
         return (R) request.toBuilder()
                  .replaceQueryParam(ID_KEY, transform(ImmutableList.copyOf(names), toStringFunction())).build();
      } else if (input instanceof long[]) {
         long[] names = (long[]) input;
         return (R) request.toBuilder().replaceQueryParam(ID_KEY, transform(Longs.asList(names), toStringFunction()))
                  .build();
      } else {
         throw new IllegalArgumentException("this binder is only valid for Long[] arguments: " + input.getClass());
      }
   }
}
