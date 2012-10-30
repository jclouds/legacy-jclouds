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
package org.jclouds.http.functions;

import static org.jclouds.http.HttpUtils.releasePayload;

import javax.annotation.Resource;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.logging.Logger;
import org.jclouds.rest.InvocationContext;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
public class ParseContentMD5FromHeaders implements Function<HttpResponse, byte[]>,
      InvocationContext<ParseContentMD5FromHeaders> {

   public static class NoContentMD5Exception extends RuntimeException {

      private static final long serialVersionUID = 1L;
      private final HttpRequest request;
      private final HttpResponse response;

      public NoContentMD5Exception(HttpRequest request, HttpResponse response) {
         super(String.format("no MD5 returned from request: %s; response %s", request, response));
         this.request = request;
         this.response = response;
      }

      public HttpRequest getRequest() {
         return request;
      }

      public HttpResponse getResponse() {
         return response;
      }

   }

   @Resource
   protected Logger logger = Logger.NULL;
   private HttpRequest request;

   public byte[] apply(HttpResponse from) {
      releasePayload(from);
      if (from.getPayload() != null) {
         return from.getPayload().getContentMetadata().getContentMD5();
      }
      throw new NoContentMD5Exception(request, from);
   }

   @Override
   public ParseContentMD5FromHeaders setContext(HttpRequest request) {
      this.request = request;
      return this;
   }

}
