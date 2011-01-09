/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.openstack.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.http.HttpUtils.releasePayload;
import static org.jclouds.openstack.reference.AuthHeaders.AUTH_TOKEN;
import static org.jclouds.openstack.reference.AuthHeaders.URL_SUFFIX;

import java.net.URI;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.OpenStackAuthAsyncClient.AuthenticationResponse;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * This parses {@link AuthenticationResponse} from HTTP headers.
 * 
 * @author Adrian Cole
 */
@Singleton
public class ParseAuthenticationResponseFromHeaders implements Function<HttpResponse, AuthenticationResponse> {

   @Resource
   protected Logger logger = Logger.NULL;

   /**
    * parses the http response headers to create a new {@link AuthenticationResponse} object.
    */
   public AuthenticationResponse apply(HttpResponse from) {
      releasePayload(from);
      Builder<String, URI> builder = ImmutableMap.<String, URI> builder();
      for (Entry<String, String> entry : from.getHeaders().entries()) {
         if (entry.getKey().endsWith(URL_SUFFIX))
            builder.put(entry.getKey(), URI.create(entry.getValue()));
      }
      AuthenticationResponse response = new AuthenticationResponse(checkNotNull(from.getFirstHeaderOrNull(AUTH_TOKEN),
               AUTH_TOKEN), builder.build());
      logger.debug("will connect to: ", response);
      return response;
   }
}