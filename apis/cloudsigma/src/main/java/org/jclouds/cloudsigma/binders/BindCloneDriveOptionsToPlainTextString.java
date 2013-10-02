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
package org.jclouds.cloudsigma.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;

import org.jclouds.cloudsigma.functions.ListOfMapsToListOfKeyValuesDelimitedByBlankLines;
import org.jclouds.cloudsigma.options.CloneDriveOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindCloneDriveOptionsToPlainTextString implements MapBinder {
   private final ListOfMapsToListOfKeyValuesDelimitedByBlankLines listOfMapsToListOfKeyValuesDelimitedByBlankLines;

   @Inject
   public BindCloneDriveOptionsToPlainTextString(
         ListOfMapsToListOfKeyValuesDelimitedByBlankLines listOfMapsToListOfKeyValuesDelimitedByBlankLines) {
      this.listOfMapsToListOfKeyValuesDelimitedByBlankLines = listOfMapsToListOfKeyValuesDelimitedByBlankLines;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      checkArgument(checkNotNull(request, "request") instanceof GeneratedHttpRequest,
            "this binder is only valid for GeneratedHttpRequests!");
      GeneratedHttpRequest gRequest = GeneratedHttpRequest.class.cast(request);

      CloneDriveOptions options = findOptionsInArgsOrNull(gRequest);
      if (options != null) {
         postParams = ImmutableMap.<String, Object> builder().putAll(postParams).putAll(options.getOptions()).build();
      }

      request.setPayload(listOfMapsToListOfKeyValuesDelimitedByBlankLines.apply(ImmutableSet.of(Maps.transformValues(postParams, new Function<Object, String>() {
         @Override
         public String apply(Object input) {
            return input == null ? null : input.toString();
         }
      }))));
      request.getPayload().getContentMetadata().setContentType(MediaType.TEXT_PLAIN);
      return request;
   }

   static CloneDriveOptions findOptionsInArgsOrNull(GeneratedHttpRequest gRequest) {
      for (Object arg : gRequest.getInvocation().getArgs()) {
         if (arg instanceof CloneDriveOptions) {
            return (CloneDriveOptions) arg;
         } else if (arg instanceof CloneDriveOptions[]) {
            CloneDriveOptions[] options = (CloneDriveOptions[]) arg;
            return (options.length > 0) ? options[0] : null;
         }
      }
      return null;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new UnsupportedOperationException();
   }

}
