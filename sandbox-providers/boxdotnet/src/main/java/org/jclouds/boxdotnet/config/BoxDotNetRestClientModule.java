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
package org.jclouds.boxdotnet.config;

import org.jclouds.boxdotnet.BoxDotNetAsyncClient;
import org.jclouds.boxdotnet.BoxDotNetClient;
import org.jclouds.boxdotnet.handlers.BoxDotNetErrorHandler;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

/**
 * Configures the BoxDotNet connection.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class BoxDotNetRestClientModule extends
         RestClientModule<BoxDotNetClient, BoxDotNetAsyncClient> {

   public BoxDotNetRestClientModule() {
      super(BoxDotNetClient.class, BoxDotNetAsyncClient.class);
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(BoxDotNetErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(BoxDotNetErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(BoxDotNetErrorHandler.class);
   }

   @Override
   protected void bindRetryHandlers() {
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(BackoffLimitedRetryHandler.class);
   }

}
