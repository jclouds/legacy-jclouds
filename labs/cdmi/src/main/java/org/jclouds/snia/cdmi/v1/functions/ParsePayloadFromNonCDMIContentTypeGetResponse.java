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
package org.jclouds.snia.cdmi.v1.functions;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payload;
import org.jclouds.rest.InvocationContext;

import com.google.common.base.Function;

/**
 * Parses payload from Non-CDMI Content Type Get response .
 * 
 * @author Kenneth Nagin
 */
public class ParsePayloadFromNonCDMIContentTypeGetResponse implements Function<HttpResponse, Payload>,
         InvocationContext<ParsePayloadFromNonCDMIContentTypeGetResponse> {

   public Payload apply(HttpResponse from) {
      Payload object = from.getPayload();
      return object;
   }

   @Override
   public ParsePayloadFromNonCDMIContentTypeGetResponse setContext(HttpRequest request) {
      return this;
   }
}
