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
package org.jclouds.nirvanix.sdn.functions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;

import com.google.common.base.Function;

/**
 * This parses the Nirvanix SessionToken from a gson string.
 * 
 * @author Adrian Cole
 */
@Singleton
public class ParseSessionTokenFromJsonResponse implements
      Function<HttpResponse, String> {

   private final ParseJson<Response> json;

   @Inject
   ParseSessionTokenFromJsonResponse(ParseJson<Response> json) {
      this.json = json;
   }

   @Override
   public String apply(HttpResponse arg0) {
      Response response = json.apply(arg0);
      if (response.ResponseCode == null || response.ResponseCode != 0)
         throw new RuntimeException("bad response code: "
               + response.ResponseCode);
      return response.SessionToken;
   }

   private static class Response {
      Integer ResponseCode;
      String SessionToken;
   }
}
