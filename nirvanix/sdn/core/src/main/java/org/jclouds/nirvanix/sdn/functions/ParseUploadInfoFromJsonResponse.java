/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import java.net.URI;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.functions.ParseJson;
import org.jclouds.nirvanix.sdn.domain.UploadInfo;

import com.google.gson.Gson;

/**
 * This parses the Nirvanix Upload host and token from a gson string.
 * 
 * @see UploadInfo
 * @author Adrian Cole
 */
@Singleton
public class ParseUploadInfoFromJsonResponse extends ParseJson<UploadInfo> {

   @Inject
   public ParseUploadInfoFromJsonResponse(Gson gson) {
      super(gson);
   }

   private static class StorageNodeResponse {
      Integer ResponseCode;
      Map<String, String> GetStorageNode;
   }

   public UploadInfo apply(InputStream stream) {
      try {
         StorageNodeResponse response = gson.fromJson(new InputStreamReader(stream, "UTF-8"),
                  StorageNodeResponse.class);
         if (response.ResponseCode == null || response.ResponseCode != 0)
            throw new RuntimeException("bad response code: " + response.ResponseCode);
         return new UploadInfo(response.GetStorageNode.get("UploadToken"),
                  URI.create("https://" + response.GetStorageNode.get("UploadHost")));
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("jclouds requires UTF-8 encoding", e);
      }
   }
}