/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.nirvanix.sdn.functions;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.functions.ParseJson;

import com.google.gson.Gson;

/**
 * This parses the Nirvanix SessionToken from a gson string.
 * 
 * @author Adrian Cole
 */
@Singleton
public class ParseSessionTokenFromJsonResponse extends ParseJson<String> {

   @Inject
   public ParseSessionTokenFromJsonResponse(Gson gson) {
      super(gson);
   }

   private static class SessionTokenResponse {
      Integer ResponseCode;
      String SessionToken;
   }

   public String apply(InputStream stream) {

      try {
         SessionTokenResponse response = gson.fromJson(new InputStreamReader(stream, "UTF-8"),
                  SessionTokenResponse.class);
         if (response.ResponseCode == null || response.ResponseCode != 0)
            throw new RuntimeException("bad response code: " + response.ResponseCode);
         return response.SessionToken;
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("jclouds requires UTF-8 encoding", e);
      }
   }
}