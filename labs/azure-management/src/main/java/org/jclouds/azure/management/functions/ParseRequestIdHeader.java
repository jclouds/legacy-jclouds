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
package org.jclouds.azure.management.functions;

import static org.jclouds.http.HttpUtils.releasePayload;

import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;

import com.google.common.base.Function;

/**
 * Parses an x-ms-request-id the header
 * 
 * A value that uniquely identifies a request made against the management service. For an
 * asynchronous operation, you can call get operation status with the value of the header to
 * determine whether the operation is complete, has failed, or is still in progress.
 * 
 * @author Gerald Pereira
 */
@Singleton
public class ParseRequestIdHeader implements Function<HttpResponse, String> {

   public String apply(HttpResponse from) {
      releasePayload(from);
      String requestId = from.getFirstHeaderOrNull("x-ms-request-id");
      if (requestId != null) {
         return requestId;
      }
      throw new IllegalStateException("did not receive RequestId in: " + from);
   }

}
