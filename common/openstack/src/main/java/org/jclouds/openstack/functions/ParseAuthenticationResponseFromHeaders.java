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
package org.jclouds.openstack.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.http.HttpUtils.releasePayload;
import static org.jclouds.http.Uris.uriBuilder;
import static org.jclouds.openstack.reference.AuthHeaders.AUTH_TOKEN;
import static org.jclouds.openstack.reference.AuthHeaders.URL_SUFFIX;

import java.net.URI;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.domain.AuthenticationResponse;
import org.jclouds.rest.InvocationContext;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * This parses {@link AuthenticationResponse} from HTTP headers.
 * 
 * @author Adrian Cole
 */
public class ParseAuthenticationResponseFromHeaders implements Function<HttpResponse, AuthenticationResponse>,
         InvocationContext<ParseAuthenticationResponseFromHeaders> {

   @Resource
   protected Logger logger = Logger.NULL;

   private String hostToReplace;

   /**
    * parses the http response headers to create a new {@link AuthenticationResponse} object.
    */
   public AuthenticationResponse apply(HttpResponse from) {
      releasePayload(from);

      // HTTP headers are case insensitive (RFC 2616) so we must allow for that when looking an header names for the URL keyword
      Builder<String, URI> builder = ImmutableMap.builder();
      for (Entry<String, String> entry : from.getHeaders().entries()) {
         if (entry.getKey().toLowerCase().endsWith(URL_SUFFIX.toLowerCase()))
            builder.put(entry.getKey(), getURI(entry.getValue()));
      }
      AuthenticationResponse response = new AuthenticationResponse(checkNotNull(from.getFirstHeaderOrNull(AUTH_TOKEN),
               AUTH_TOKEN), builder.build());
      logger.debug("will connect to: ", response);
      return response;
   }

   // TODO: find the swift configuration or bug related to returning localhost
   protected URI getURI(String headerValue) {
      if (headerValue == null)
         return null;
      URI toReturn = URI.create(headerValue);
      if (!"127.0.0.1".equals(toReturn.getHost()))
         return toReturn;
      return uriBuilder(toReturn).host(hostToReplace).build();
   }

   @Override
   public ParseAuthenticationResponseFromHeaders setContext(HttpRequest request) {
      String host = request.getEndpoint().getHost();
      return setHostToReplace(host);
   }

   @VisibleForTesting
   ParseAuthenticationResponseFromHeaders setHostToReplace(String hostToReplace) {
      this.hostToReplace = hostToReplace;
      return this;
   }
}
