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
package org.jclouds.vcloud.terremark.config;

import java.io.IOException;
import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.util.Utils;
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.domain.Organization;
import org.jclouds.vcloud.terremark.TerremarkVCloudAsyncClient;
import org.jclouds.vcloud.terremark.TerremarkVCloudClient;
import org.jclouds.vcloud.terremark.TerremarkVCloudExpressAsyncClient;
import org.jclouds.vcloud.terremark.TerremarkVCloudExpressClient;
import org.jclouds.vcloud.terremark.domain.TerremarkOrganization;
import org.jclouds.vcloud.terremark.endpoints.KeysList;

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
   protected VCloudAsyncClient provideVCloudAsyncClient(TerremarkVCloudExpressAsyncClient in) {
      return in;
   }

   @Provides
   @Singleton
   protected VCloudClient provideVCloudClient(TerremarkVCloudExpressClient in) {
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

   @Provides
   @KeysList
   @Singleton
   protected URI provideDefaultKeysList(Organization org) {
      return TerremarkOrganization.class.cast(org).getKeysList().getLocation();
   }

   @Singleton
   @Provides
   @Named("CreateKey")
   String provideCreateKey() throws IOException {
      return Utils.toStringAndClose(getClass().getResourceAsStream("/terremark/CreateKey.xml"));
   }

}
