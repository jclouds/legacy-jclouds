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
package org.jclouds.http.filters;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;

/**
 * Uses Basic Authentication to sign the request.
 * 
 * @see <a href= "http://en.wikipedia.org/wiki/Basic_access_authentication" />
 * @author Adrian Cole
 * 
 */
@Singleton
public class BasicAuthentication implements HttpRequestFilter {

   private final List<String> credentialList;

   public BasicAuthentication(String user, String password, EncryptionService encryptionService)
            throws UnsupportedEncodingException {
      this.credentialList = Collections.singletonList("Basic "
               + encryptionService.toBase64String(String.format("%s:%s",
                        checkNotNull(user, "user"), checkNotNull(password, "password")).getBytes(
                        "UTF-8")));
   }

   public void filter(HttpRequest request) throws HttpException {
      request.getHeaders().replaceValues(HttpHeaders.AUTHORIZATION, credentialList);
   }
}