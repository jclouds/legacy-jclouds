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

package org.jclouds.savvis.config;

import javax.inject.Singleton;

import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.savvis.SymphonyVPDCAsyncClient;
import org.jclouds.savvis.SymphonyVPDCClient;
import org.jclouds.savvis.handlers.SymphonyVPDCErrorHandler;
import org.jclouds.vcloud.VCloudExpressAsyncClient;
import org.jclouds.vcloud.VCloudExpressClient;
import org.jclouds.vcloud.config.BaseVCloudExpressRestClientModule;

import com.google.inject.Provides;

public class SymphonyVPDCRestClientModule extends
      BaseVCloudExpressRestClientModule<SymphonyVPDCClient, SymphonyVPDCAsyncClient> {

   public SymphonyVPDCRestClientModule() {
      super(SymphonyVPDCClient.class, SymphonyVPDCAsyncClient.class);
   }

   @Provides
   @Singleton
   protected VCloudExpressAsyncClient provideVCloudAsyncClient(SymphonyVPDCAsyncClient in) {
      return in;
   }

   @Provides
   @Singleton
   protected VCloudExpressClient provideVCloudClient(SymphonyVPDCClient in) {
      return in;
   }

   
   
   @Override
   protected void configure() {
	super.configure();
//	no longer needed.. just here to show an example of how to override an xml handler
//	bind(OrgListHandler.class).to(SymphonyVPDCOrgListHandler.class);
   }

@Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(SymphonyVPDCErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(SymphonyVPDCErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(SymphonyVPDCErrorHandler.class);
   }

}