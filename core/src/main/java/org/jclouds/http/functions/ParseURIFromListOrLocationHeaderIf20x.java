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
package org.jclouds.http.functions;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static com.google.common.net.HttpHeaders.LOCATION;
import static org.jclouds.http.HttpUtils.releasePayload;

import java.io.IOException;
import java.net.URI;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.Uris;
import org.jclouds.rest.InvocationContext;
import org.jclouds.util.Strings2;

import com.google.common.base.Function;

/**
 * parses a single URI from a list
 * 
 * @author Adrian Cole
 */
public class ParseURIFromListOrLocationHeaderIf20x implements Function<HttpResponse, URI>,
      InvocationContext<ParseURIFromListOrLocationHeaderIf20x> {

   private HttpRequest request;

   public URI apply(HttpResponse from) {
      if (from.getStatusCode() > 206)
         throw new HttpException(String.format("Unhandled status code  - %1$s", from));
      if ("text/uri-list".equals(from.getFirstHeaderOrNull(CONTENT_TYPE))) {
         try {
            if (from.getPayload().getInput() == null)
               throw new HttpResponseException("no content", null, from);
            String toParse = Strings2.toString(from.getPayload());
            return URI.create(toParse.trim());
         } catch (IOException e) {
            throw new HttpResponseException("couldn't parse uri from content", null, from, e);
         } finally {
            releasePayload(from);
         }
      } else {
         releasePayload(from);
         String location = from.getFirstHeaderOrNull(LOCATION);
         if (location == null)
            location = from.getFirstHeaderOrNull("location");
         if (location != null) {
            URI locationUri = URI.create(location);
            if (locationUri.getHost() != null)
               return locationUri;
            checkState(request != null, "request should have been initialized");
            if (!location.startsWith("/"))
               location = "/" + location;
            locationUri = URI.create(location);
            return Uris.uriBuilder(request.getEndpoint()).path(locationUri.getPath()).query(locationUri.getQuery()).build();
         } else {
            throw new HttpResponseException("no uri in headers or content", null, from);
         }

      }
   }

   @Override
   public ParseURIFromListOrLocationHeaderIf20x setContext(HttpRequest request) {
      this.request = request;
      return this;
   }
}
