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
package org.jclouds.trmk.vcloudexpress.config;

import javax.inject.Singleton;

import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudAsyncClient;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudClient;
import org.jclouds.trmk.vcloud_0_8.VCloudExpressAsyncClient;
import org.jclouds.trmk.vcloud_0_8.VCloudExpressClient;
import org.jclouds.trmk.vcloud_0_8.config.TerremarkRestClientModule;
import org.jclouds.trmk.vcloudexpress.TerremarkVCloudExpressAsyncClient;
import org.jclouds.trmk.vcloudexpress.TerremarkVCloudExpressClient;

import com.google.inject.Provides;

/**
 * Configures the VCloud authentication service connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class TerremarkVCloudExpressRestClientModule extends
         TerremarkRestClientModule<TerremarkVCloudExpressClient, TerremarkVCloudExpressAsyncClient> {

   public TerremarkVCloudExpressRestClientModule() {
      super(TerremarkVCloudExpressClient.class, TerremarkVCloudExpressAsyncClient.class);
   }

   @Provides
   @Singleton
   protected VCloudExpressAsyncClient provideVCloudAsyncClient(TerremarkVCloudExpressAsyncClient in) {
      return in;
   }

   @Provides
   @Singleton
   protected VCloudExpressClient provideVCloudClient(TerremarkVCloudExpressClient in) {
      return in;
   }

   @Provides
   @Singleton
   protected TerremarkVCloudAsyncClient provideTerremarkAsyncClient(TerremarkVCloudExpressAsyncClient in) {
      return in;
   }

   @Provides
   @Singleton
   protected TerremarkVCloudClient provideTerremarkClient(TerremarkVCloudExpressClient in) {
      return in;
   }
}
