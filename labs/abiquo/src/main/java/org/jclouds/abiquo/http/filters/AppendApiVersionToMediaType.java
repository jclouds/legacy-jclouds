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

package org.jclouds.abiquo.http.filters;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.abiquo.functions.AppendApiVersionToAbiquoMimeType;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Iterables;
import com.google.common.net.HttpHeaders;

/**
 * Appends the api version to the Abiquo mime types to ensure the input and
 * output of api calls will be in the desired format.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class AppendApiVersionToMediaType implements HttpRequestFilter {
   /** The function used to append the version to media types. */
   private AppendApiVersionToAbiquoMimeType versionAppender;

   @Inject
   public AppendApiVersionToMediaType(final AppendApiVersionToAbiquoMimeType versionAppender) {
      super();
      this.versionAppender = versionAppender;
   }

   @Override
   public HttpRequest filter(final HttpRequest request) throws HttpException {
      HttpRequest requestWithVersionInMediaTypes = appendVersionToNonPayloadHeaders(request);
      return appendVersionToPayloadHeaders(requestWithVersionInMediaTypes);
   }

   @VisibleForTesting
   HttpRequest appendVersionToNonPayloadHeaders(final HttpRequest request) {
      Collection<String> accept = request.getHeaders().get(HttpHeaders.ACCEPT);
      return accept.isEmpty() ? request : request
            .toBuilder()
            .replaceHeader(HttpHeaders.ACCEPT,
                  Iterables.toArray(Iterables.transform(accept, versionAppender), String.class)).build();
   }

   @VisibleForTesting
   HttpRequest appendVersionToPayloadHeaders(final HttpRequest request) {
      if (request.getPayload() != null) {
         String contentTypeWithVersion = versionAppender.apply(request.getPayload().getContentMetadata()
               .getContentType());
         request.getPayload().getContentMetadata().setContentType(contentTypeWithVersion);
      }

      return request;
   }
}
