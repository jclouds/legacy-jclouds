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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.jenkins.v1.JenkinsApiMetadata;
import org.jclouds.rest.annotations.Identity;

import com.google.common.base.Optional;

/**
 * @author Adrian Cole
 * 
 */
@Singleton
public class BasicAuthenticationUnlessAnonymous implements HttpRequestFilter {

   private final Optional<BasicAuthentication> auth;

   @Inject
   public BasicAuthenticationUnlessAnonymous(@Identity String user, BasicAuthentication auth) {
      this.auth = JenkinsApiMetadata.ANONYMOUS_IDENTITY.equals(checkNotNull(user, "user")) ? Optional
               .<BasicAuthentication> absent() : Optional.of(checkNotNull(auth, "auth"));
   }

   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      if (auth.isPresent())
         return auth.get().filter(request);
      return request;
   }
}