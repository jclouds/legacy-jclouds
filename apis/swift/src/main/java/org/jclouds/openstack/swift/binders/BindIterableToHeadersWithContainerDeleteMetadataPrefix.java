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
package org.jclouds.openstack.swift.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.openstack.swift.reference.SwiftHeaders;
import org.jclouds.rest.Binder;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;

/**
 * @author Everett Toews
 */
@Singleton
public class BindIterableToHeadersWithContainerDeleteMetadataPrefix implements Binder {
   private final Function<String, String> FN;

   public BindIterableToHeadersWithContainerDeleteMetadataPrefix() {
      FN = new Function<String, String>() {

         @Override
         public String apply(String element) {
            String inLowercase = element.toLowerCase();
            return (inLowercase.startsWith(SwiftHeaders.CONTAINER_DELETE_METADATA_PREFIX)) ? inLowercase : SwiftHeaders.CONTAINER_DELETE_METADATA_PREFIX + inLowercase;
         }

         @Override
         public String toString() {
            return "prefix: " + SwiftHeaders.CONTAINER_DELETE_METADATA_PREFIX;
         }
      };
   }

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof Iterable<?>, "this binder is only valid for Iterable!");
      checkNotNull(request, "request");

      Iterable<String> metadataKeys = Iterables.transform((Iterable<String>) input, FN);
      HashMultimap<String, String> headers = HashMultimap.create();
      
      for (String metadataKey: metadataKeys) {
    	  headers.put(metadataKey, "");
      }
      
      return (R) request.toBuilder().replaceHeaders(headers).build();
   }
}
