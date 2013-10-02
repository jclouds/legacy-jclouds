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
package org.jclouds.dynect.v3.filters;

import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.jclouds.http.HttpRequest.NON_PAYLOAD_METHODS;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;

/**
 * 
 * DynECT requires Content-Type even on GET requests.
 * 
 * @author Adrian Cole
 */
public final class AlwaysAddContentType implements HttpRequestFilter {
   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      if (NON_PAYLOAD_METHODS.contains(request.getMethod()))
         return request.toBuilder().replaceHeader(CONTENT_TYPE, APPLICATION_JSON).build();
      return request;
   }
}
