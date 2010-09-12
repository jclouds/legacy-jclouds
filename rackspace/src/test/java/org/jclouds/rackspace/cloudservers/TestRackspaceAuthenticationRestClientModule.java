/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.rackspace.cloudservers;

import java.util.Date;

import org.jclouds.http.RequiresHttp;
import org.jclouds.rackspace.RackspaceAuthAsyncClient.AuthenticationResponse;
import org.jclouds.rackspace.config.RackspaceAuthenticationRestModule;
import org.jclouds.rackspace.functions.ParseAuthenticationResponseFromHeaders.AuthenticationResponseImpl;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.common.base.Supplier;

@RequiresHttp
@ConfiguresRestClient
public class TestRackspaceAuthenticationRestClientModule extends RackspaceAuthenticationRestModule {
   @Override
   protected void configure() {
      super.configure();
   }

   @Override
   protected AuthenticationResponse provideAuthenticationResponse(Supplier<AuthenticationResponse> supplier) {
      return new AuthenticationResponseImpl("authToken", "http://CDNManagementUrl", "http://serverManagementUrl",
               "http://storageUrl");
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