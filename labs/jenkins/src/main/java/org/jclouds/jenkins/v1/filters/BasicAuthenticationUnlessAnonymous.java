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
package org.jclouds.jenkins.v1.filters;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.jenkins.v1.JenkinsApiMetadata.ANONYMOUS_IDENTITY;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.location.Provider;

import com.google.common.base.Supplier;

/**
 * @author Adrian Cole
 * 
 */
@Singleton
public class BasicAuthenticationUnlessAnonymous implements HttpRequestFilter {

   private final Supplier<Credentials> creds;
   private final BasicAuthentication auth;

   @Inject
   public BasicAuthenticationUnlessAnonymous(@Provider Supplier<Credentials> creds, BasicAuthentication auth) {
      this.creds = checkNotNull(creds, "creds");
      this.auth = checkNotNull(auth, "auth");
   }

   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      if (ANONYMOUS_IDENTITY.equals(checkNotNull(creds.get().identity, "user")))
         return request;
      return auth.filter(request);
   }
}
