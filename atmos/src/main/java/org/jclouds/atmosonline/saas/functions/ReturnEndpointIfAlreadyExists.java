/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.atmosonline.saas.functions;

import static org.jclouds.util.Utils.propagateOrNull;

import java.net.URI;

import org.jclouds.blobstore.KeyAlreadyExistsException;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.InvocationContext;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
public class ReturnEndpointIfAlreadyExists implements Function<Exception, URI>, InvocationContext {

   private URI endpoint;

   public URI apply(Exception from) {
      if (from instanceof KeyAlreadyExistsException) {
         return endpoint;
      }
      return URI.class.cast(propagateOrNull(from));
   }

   @Override
   public ReturnEndpointIfAlreadyExists setContext(HttpRequest request) {
      this.endpoint = request == null ? null : request.getEndpoint();
      return this;
   }

}