/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.openstack.internal;

import java.net.URI;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.domain.Credentials;
import org.jclouds.openstack.config.OpenStackAuthenticationModule;
import org.jclouds.openstack.domain.AuthenticationResponse;
import org.jclouds.openstack.reference.AuthHeaders;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Adrian Cole
 */
public class TestOpenStackAuthenticationModule extends OpenStackAuthenticationModule {
   @Override
   protected void configure() {
      super.configure();
      bind(GetAuthenticationResponse.class).to(TestGetAuthenticationResponse.class);
   }

   @Singleton
   public static class TestGetAuthenticationResponse extends GetAuthenticationResponse {

      @Inject
      protected TestGetAuthenticationResponse() {
         super(null);
      }

      @Override
      public AuthenticationResponse load(Credentials input) {
         return new AuthenticationResponse("authToken", ImmutableMap.<String, URI> of(
                  AuthHeaders.SERVER_MANAGEMENT_URL, URI.create("http://endpoint/vapi-version"),
                  AuthHeaders.STORAGE_URL, URI.create("http://storage")));
      }

   }

   @Override
   public Supplier<String> provideAuthenticationTokenCache(Supplier<AuthenticationResponse> supplier) {
      return new Supplier<String>() {
         public String get() {
            return "testtoken";
         }
      };
   }

   @Override
   public Supplier<Date> provideCacheBusterDate() {
      return new Supplier<Date>() {
         public Date get() {
            return new Date();
         }
      };
   }

}
