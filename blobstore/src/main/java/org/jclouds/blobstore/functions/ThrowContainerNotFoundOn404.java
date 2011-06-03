/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.blobstore.functions;

import static org.jclouds.util.Throwables2.propagateOrNull;

import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.http.HttpResponseException;

import com.google.common.base.Function;

/**
 * 
 * {@code bucketParser} is only enacted when the http status code is 2xx. We check for Amazon's
 * {@code NoSuchBucket} message and throw a ContainerNotFoundException.
 * 
 * @author James Murty
 */
public class ThrowContainerNotFoundOn404 implements Function<Exception, Object> {

   public Object apply(Exception from) {
      if (from instanceof HttpResponseException) {
         HttpResponseException responseException = (HttpResponseException) from;
         if (responseException.getResponse().getStatusCode() == 404) {
            // TODO: parse to get the bucket name
            throw new ContainerNotFoundException(from);
         }
      }
      return propagateOrNull(from);
   }

}
