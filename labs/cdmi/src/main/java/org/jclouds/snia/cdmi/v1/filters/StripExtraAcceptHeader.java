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
package org.jclouds.snia.cdmi.v1.filters;

import javax.inject.Singleton;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.utils.ModifyRequest;

/**
 * current CDMI spec doesn't indicate the form of the response. It would be nice, if it could take 2
 * {@code Accept} headers. Until then, let's strip off the mediaType header, which we use to ensure
 * responses are parsed with json.
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class StripExtraAcceptHeader implements HttpRequestFilter {
 
   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      return ModifyRequest.replaceHeader(request, "Accept", request.getFirstHeaderOrNull("Accept"));
   }
}