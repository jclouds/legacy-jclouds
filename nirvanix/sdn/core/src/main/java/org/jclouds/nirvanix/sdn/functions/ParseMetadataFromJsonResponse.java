/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.nirvanix.sdn.functions;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jclouds.http.functions.ParseJson;

import com.google.common.collect.Maps;
import com.google.gson.Gson;

/**
 * This parses a Map of Metadata from a Nirvanix response
 * 
 * @author Adrian Cole
 */
public class ParseMetadataFromJsonResponse extends ParseJson<Map<String, String>> {

   @Inject
   public ParseMetadataFromJsonResponse(Gson gson) {
      super(gson);
   }

   private static class SessionTokenResponse {
      Integer ResponseCode;
      List<Map<String, String>> Metadata;
   }

   public Map<String, String> apply(InputStream stream) {

      try {
         SessionTokenResponse response = gson.fromJson(new InputStreamReader(stream, "UTF-8"),
                  SessionTokenResponse.class);
         if (response.ResponseCode == null || response.ResponseCode != 0)
            throw new RuntimeException("bad response code: " + response.ResponseCode);
         Map<String, String> metadata = Maps.newHashMap();
         for (Map<String, String> keyValue : response.Metadata) {
            metadata.put(keyValue.get("Type"), keyValue.get("Value"));
         }
         return metadata;
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("jclouds requires UTF-8 encoding", e);
      }
   }
}