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
package org.jclouds.blobstore.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;
import org.jclouds.util.Maps2;

import com.google.common.base.Function;
import com.google.common.collect.Multimaps;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindMapToHeadersWithPrefix implements Binder {
   private final Function<String, String> FN;

   @Inject
   public BindMapToHeadersWithPrefix(@Named(PROPERTY_USER_METADATA_PREFIX) final String metadataPrefix) {
      checkNotNull(metadataPrefix, PROPERTY_USER_METADATA_PREFIX);
      FN = new Function<String, String>() {

         @Override
         public String apply(String arg0) {
            String inLowercase = arg0.toLowerCase();
            return (inLowercase.startsWith(metadataPrefix)) ? inLowercase : metadataPrefix + inLowercase;
         }

         @Override
         public String toString() {
            return "prefix: " + metadataPrefix;
         }

      };

   }

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof Map<?,?>, "this binder is only valid for Maps!");
      checkNotNull(request, "request");

      Map<String, String> userMetadata = Maps2.transformKeys((Map<String, String>) input, FN);
      return (R) request.toBuilder().replaceHeaders(Multimaps.forMap(userMetadata)).build();
   }

}
