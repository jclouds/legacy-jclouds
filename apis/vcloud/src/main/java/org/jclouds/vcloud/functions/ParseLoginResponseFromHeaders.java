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
package org.jclouds.vcloud.functions;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.http.HttpUtils.releasePayload;

import java.util.Map;
import java.util.NoSuchElementException;
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
import org.jclouds.vcloud.VCloudToken;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.VCloudSession;
import org.jclouds.vcloud.endpoints.Org;
import org.jclouds.vcloud.xml.OrgListHandler;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 * This parses {@link VCloudSession} from HTTP headers.
 * 
 * @author Adrian Cole
 */
@Singleton
public class ParseLoginResponseFromHeaders implements Function<HttpResponse, VCloudSession> {
   static final Pattern pattern = Pattern.compile("(vcloud-token)=?([^;]+)(;.*)?");

   private final ParseSax.Factory factory;
   private final Provider<OrgListHandler> orgHandlerProvider;

   @Inject
   private ParseLoginResponseFromHeaders(Factory factory, Provider<OrgListHandler> orgHandlerProvider) {
      this.factory = factory;
      this.orgHandlerProvider = orgHandlerProvider;
   }

   /**
    * parses the http response headers to create a new {@link VCloudSession} object.
    */
   public VCloudSession apply(HttpResponse from) {
      try {
         final String token = parseTokenFromHeaders(from);
         final Map<String, ReferenceType> org = factory.create(orgHandlerProvider.get()).parse(
               checkNotNull(from.getPayload().getInput(), "no payload in http response to login request %s", from));

         return new VCloudSession() {
            @VCloudToken
            public String getVCloudToken() {
               return token;
            }

            @Org
            public Map<String, ReferenceType> getOrgs() {
               return org;
            }
         };
      } finally {
         releasePayload(from);
      }
   }

   public String parseTokenFromHeaders(HttpResponse from) {
      String cookieHeader = from.getFirstHeaderOrNull("x-vcloud-authorization");
      if (cookieHeader != null) {
         Matcher matcher = pattern.matcher(cookieHeader);
         return matcher.find() ? matcher.group(2) : cookieHeader;
      } else {
         try {
            cookieHeader = Iterables.find(from.getHeaders().get(HttpHeaders.SET_COOKIE), Predicates.contains(pattern));
            Matcher matcher = pattern.matcher(cookieHeader);
            matcher.find();
            return matcher.group(2);
         } catch (NoSuchElementException e) {
            throw new HttpResponseException(String.format("Header %s or %s must be present", "x-vcloud-authorization",
                     HttpHeaders.SET_COOKIE), null, from);
         }
      }
   }
}
