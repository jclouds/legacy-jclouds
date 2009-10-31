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
package org.jclouds.vcloudx.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.vcloudx.VCloudToken;
import org.jclouds.vcloudx.VCloudXLogin.VCloudXSession;
import org.jclouds.vcloudx.endpoints.Org;
import org.jclouds.vcloudx.xml.OrgListToOrgUriHandler;

import com.google.common.base.Function;

/**
 * This parses {@link VCloudXSession} from HTTP headers.
 * 
 * @author Adrian Cole
 */
@Singleton
public class ParseLoginResponseFromHeaders implements Function<HttpResponse, VCloudXSession> {
   static final Pattern pattern = Pattern.compile("vcloud-token=(.*); path=.*");

   private final ParseSax.Factory factory;
   private final Provider<OrgListToOrgUriHandler> orgHandlerProvider;

   @Inject
   private ParseLoginResponseFromHeaders(Factory factory,
            Provider<OrgListToOrgUriHandler> orgHandlerProvider) {
      super();
      this.factory = factory;
      this.orgHandlerProvider = orgHandlerProvider;
   }

   /**
    * parses the http response headers to create a new {@link VCloudXSession} object.
    */
   public VCloudXSession apply(HttpResponse from) {
      String cookieHeader = checkNotNull(from.getFirstHeaderOrNull(HttpHeaders.SET_COOKIE),
               HttpHeaders.SET_COOKIE);

      final Matcher matcher = pattern.matcher(cookieHeader);
      boolean matchFound = matcher.find();

      if (matchFound) {
         final URI org = (URI) factory.create(orgHandlerProvider.get()).parse(from.getContent());

         return new VCloudXSession() {
            @VCloudToken
            public String getVCloudToken() {
               return matcher.group(1);
            }

            @Org
            public URI getOrg() {
               return org;
            }
         };

      } else {
         throw new HttpResponseException("vcloud token not found in response ", null, from);
      }
   }
}
