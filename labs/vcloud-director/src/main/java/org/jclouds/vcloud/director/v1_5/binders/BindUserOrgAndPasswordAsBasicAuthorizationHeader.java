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
package org.jclouds.vcloud.director.v1_5.binders;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.crypto.CryptoStreams;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.utils.ModifyRequest;
import org.jclouds.rest.MapBinder;

import com.google.common.base.Throwables;

/**
 * Uses Basic Authentication to sign the request.
 * 
 * @see <a href= "http://en.wikipedia.org/wiki/Basic_access_authentication" />
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindUserOrgAndPasswordAsBasicAuthorizationHeader implements MapBinder {

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, String> postParams) {
      try {
         String header = "Basic "
                  + CryptoStreams.base64(String.format("%s@%s:%s", checkNotNull(postParams.get("user"), "user"),
                           checkNotNull(postParams.get("org"), "org"),
                           checkNotNull(postParams.get("password"), "password")).getBytes("UTF-8"));
         return ModifyRequest.replaceHeader(request, HttpHeaders.AUTHORIZATION, header);
      } catch (UnsupportedEncodingException e) {
         throw Throwables.propagate(e);
      }
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new UnsupportedOperationException();
   }
}