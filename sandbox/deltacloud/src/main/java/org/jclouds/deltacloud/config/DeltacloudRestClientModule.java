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

package org.jclouds.deltacloud.config;

import org.jclouds.deltacloud.DeltacloudAsyncClient;
import org.jclouds.deltacloud.DeltacloudClient;
import org.jclouds.deltacloud.handlers.DeltacloudErrorHandler;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

/**
 * Configures the deltacloud connection.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class DeltacloudRestClientModule extends RestClientModule<DeltacloudClient, DeltacloudAsyncClient> {

   public DeltacloudRestClientModule() {
      super(DeltacloudClient.class, DeltacloudAsyncClient.class);
   }


   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(DeltacloudErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(DeltacloudErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(DeltacloudErrorHandler.class);
   }

   @Override
   protected void bindRetryHandlers() {
      // TODO
   }

}
