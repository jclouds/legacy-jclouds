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
package org.jclouds.blobstore.functions;

import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;

import com.google.common.base.Function;
import com.google.common.base.Throwables;

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
         HttpResponse response = ((HttpResponseException) from).getResponse();
         if (response != null && response.getStatusCode() == 404) {
            // TODO: parse to get the bucket name
            throw new ContainerNotFoundException(from);
         }
      }
      throw Throwables.propagate(from);
   }

}
