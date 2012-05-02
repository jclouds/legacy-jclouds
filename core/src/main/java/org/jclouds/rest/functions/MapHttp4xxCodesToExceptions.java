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
package org.jclouds.rest.functions;

import javax.inject.Singleton;

import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;

import com.google.common.base.Function;
import com.google.common.base.Throwables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class MapHttp4xxCodesToExceptions implements Function<Exception, Object> {

   public Object apply(Exception from) {
      if (from instanceof HttpResponseException) {
         HttpResponseException responseException = (HttpResponseException) from;
         if (responseException.getResponse() != null)
            switch (responseException.getResponse().getStatusCode()) {
               case 401:
                  throw new AuthorizationException(from);
               case 403:
                  throw new AuthorizationException(from);
               case 404:
                  throw new ResourceNotFoundException(from);
               case 409:
                  throw new IllegalStateException(from);
            }
      }
      throw Throwables.propagate(from);
   }

}
