/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.rimuhosting.miro.filters;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.Constants;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.utils.ModifyRequest;

/**
 * RimuHosting Authentication is a Authorization Header.
 * 
 * Authorization: rimuhosting apikey=&lt;key>
 * 
 * @author Ivan Meredith
 */
@Singleton
public class RimuHostingAuthentication implements HttpRequestFilter {
   private final String header;

   @Inject
   public RimuHostingAuthentication(@Named(Constants.PROPERTY_IDENTITY) String apikey) {
      this.header = String.format("rimuhosting apikey=%s", checkNotNull(apikey, "apikey"));
   }

   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      return ModifyRequest.replaceHeader(request, HttpHeaders.AUTHORIZATION, header);
   }
}
