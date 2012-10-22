/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Arrays;

import javax.inject.Singleton;

import org.jclouds.abiquo.rest.annotations.EndpointLink;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;
import org.jclouds.rest.binders.BindException;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.SingleResourceTransportDto;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 * Binds the given object to the path..
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class BindToPath implements Binder {
   @Override
   public <R extends HttpRequest> R bindToRequest(final R request, final Object input) {
      checkArgument(checkNotNull(request, "request") instanceof GeneratedHttpRequest,
            "this binder is only valid for GeneratedHttpRequests");
      GeneratedHttpRequest gRequest = (GeneratedHttpRequest) request;
      checkState(gRequest.getArgs() != null, "args should be initialized at this point");

      // Update the request URI with the configured link URI
      String newEndpoint = getNewEndpoint(gRequest, input);
      return bindToPath(request, newEndpoint);
   }

   /**
    * Get the new endpoint to use.
    * 
    * @param gRequest
    *           The request.
    * @param input
    *           The input parameter.
    * @return The new endpoint to use.
    */
   protected String getNewEndpoint(final GeneratedHttpRequest gRequest, final Object input) {
      SingleResourceTransportDto dto = checkValidInput(input);
      return getLinkToUse(gRequest, dto).getHref();
   }

   /**
    * Get the link to be used to build the request URI.
    * 
    * @param request
    *           The current request.
    * @param payload
    *           The object containing the link.
    * @return The link to be used to build the request URI.
    */
   static RESTLink getLinkToUse(final GeneratedHttpRequest request, final SingleResourceTransportDto payload) {
      int argIndex = request.getArgs().indexOf(payload);
      Annotation[] annotations = request.getJavaMethod().getParameterAnnotations()[argIndex];

      EndpointLink linkName = (EndpointLink) Iterables.find(Arrays.asList(annotations),
            Predicates.instanceOf(EndpointLink.class), null);

      if (linkName == null) {
         throw new BindException(request, "Expected a EndpointLink annotation but not found in the parameter");
      }

      return checkNotNull(payload.searchLink(linkName.value()), "No link was found in object with rel: " + linkName);
   }

   /**
    * Bind the given link to the request URI.
    * 
    * @param request
    *           The request to modify.
    * @param endpoint
    *           The endpoint to use as the request URI.
    * @return The updated request.
    */
   @SuppressWarnings("unchecked")
   static <R extends HttpRequest> R bindToPath(final R request, final String endpoint) {
      // Preserve current query and matrix parameters
      String newEndpoint = endpoint + getParameterString(request);

      // Replace the URI with the edit link in the DTO
      URI path = URI.create(newEndpoint);
      return (R) request.toBuilder().endpoint(path).build();
   }

   protected static SingleResourceTransportDto checkValidInput(final Object input) {
      checkArgument(checkNotNull(input, "input") instanceof SingleResourceTransportDto,
            "this binder is only valid for SingleResourceTransportDto objects");

      return (SingleResourceTransportDto) input;
   }

   protected static <R extends HttpRequest> String getParameterString(final R request) {
      String endpoint = request.getEndpoint().toString();

      int query = endpoint.indexOf('?');
      int matrix = endpoint.indexOf(';');

      if (query == -1 && matrix == -1) {
         // No parameters
         return "";
      } else if (query != -1 && matrix != -1) {
         // Both parameter types
         return endpoint.substring(query < matrix ? query : matrix);
      } else if (query != -1) {
         // Only request parameters
         return endpoint.substring(query);
      } else {
         // Only matrix parameters
         return endpoint.substring(matrix);
      }

   }
}
