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

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

/**
 * This parses a Map of Metadata from a Nirvanix response
 * 
 * @author Adrian Cole
 */
@Singleton
public class ParseMetadataFromJsonResponse implements
      Function<HttpResponse, Map<String, String>> {

   private final ParseJson<Response> json;

   @Inject
   ParseMetadataFromJsonResponse(ParseJson<Response> json) {
      this.json = json;
   }

   @Override
   public Map<String, String> apply(HttpResponse arg0) {
      Response response = json.apply(arg0);
      if (response.ResponseCode == null || response.ResponseCode != 0)
         throw new RuntimeException("bad response code: "
               + response.ResponseCode);
      Map<String, String> metadata = Maps.newHashMap();
      for (Map<String, String> keyValue : response.Metadata) {
         metadata.put(keyValue.get("Type"), keyValue.get("Value"));
      }
      return metadata;
   }

   private static class Response {
      Integer ResponseCode;
      List<Map<String, String>> Metadata;
   }

}
