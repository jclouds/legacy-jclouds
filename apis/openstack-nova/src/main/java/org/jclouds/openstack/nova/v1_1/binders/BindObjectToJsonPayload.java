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
package org.jclouds.openstack.nova.v1_1.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Iterables;
import com.google.inject.TypeLiteral;

/**
 * @author Adam Lowe
 */
public abstract class BindObjectToJsonPayload<T> extends BindToJsonPayload implements MapBinder {
   private final String fieldName;
   private final String wrapperName;
   private final TypeLiteral<T> type;

      /** Bind a specific argument to the json payload
      * 
      * @param fieldName the name of the output json field
      * @param fieldType the type of the object to select from the method arguments
      * @param wrapperName the name of the json field wrapper (if any)
      */
   public BindObjectToJsonPayload(Json jsonBinder, String fieldName, TypeLiteral<T> fieldType, String wrapperName) {
      super(jsonBinder);
      this.fieldName = fieldName;
      this.wrapperName = wrapperName;
      this.type = fieldType;
   }

   public BindObjectToJsonPayload(Json jsonBinder, String fieldName, TypeLiteral<T> fieldType) {
      this(jsonBinder, fieldName, fieldType, null);
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object toBind) {
      throw new IllegalStateException("BindMapToJsonPayload needs parameters");
   }

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, String> postParams) {
      Builder<String, Object> payload = ImmutableMap.builder();
      payload.putAll(postParams);
      checkArgument(checkNotNull(request, "request") instanceof GeneratedHttpRequest<?>,
            "this binder is only valid for GeneratedHttpRequests!");
      GeneratedHttpRequest<?> gRequest = (GeneratedHttpRequest<?>) request;

      T specs = (T) Iterables.find(gRequest.getArgs(), Predicates.instanceOf(type.getRawType()));
      payload.put(fieldName, specs);
      
      if (wrapperName != null) {
         return super.bindToRequest(request, ImmutableMap.of(wrapperName, payload.build()));  
      }

      return super.bindToRequest(request, payload.build());
   }
}