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

package org.jclouds.vcloud.hostingdotcom.config;

import java.net.URI;

import javax.inject.Singleton;

import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.vcloud.VCloudExpressAsyncClient;
import org.jclouds.vcloud.VCloudExpressClient;
import org.jclouds.vcloud.config.BaseVCloudExpressRestClientModule;
import org.jclouds.vcloud.hostingdotcom.HostingDotComVCloudAsyncClient;
import org.jclouds.vcloud.hostingdotcom.HostingDotComVCloudClient;

import com.google.inject.Provides;

/**
 * Configures the VCloud authentication service connection, including logging
 * and http transport.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class HostingDotComVCloudRestClientModule
      extends
      BaseVCloudExpressRestClientModule<HostingDotComVCloudClient, HostingDotComVCloudAsyncClient> {

   public HostingDotComVCloudRestClientModule() {
      super(HostingDotComVCloudClient.class,
            HostingDotComVCloudAsyncClient.class);
   }

   @Provides
   @Singleton
   protected VCloudExpressAsyncClient provideVCloudAsyncClient(
         HostingDotComVCloudAsyncClient in) {
      return in;
   }

   @Provides
   @Singleton
   protected VCloudExpressClient provideVCloudClient(HostingDotComVCloudClient in) {
      return in;
   }

   @Override
   protected URI provideDefaultNetwork(VCloudExpressClient client) {
      return URI.create("https://vcloud.safesecureweb.com/network/1990");
   }

}
