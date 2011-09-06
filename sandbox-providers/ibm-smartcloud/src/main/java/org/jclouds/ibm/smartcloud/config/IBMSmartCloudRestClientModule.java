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
package org.jclouds.ibm.smartcloud.config;

import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.ibm.smartcloud.IBMSmartCloudAsyncClient;
import org.jclouds.ibm.smartcloud.IBMSmartCloudClient;
import org.jclouds.ibm.smartcloud.handlers.IBMSmartCloudErrorHandler;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

/**
 * Configures the IBMSmartCloud connection.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class IBMSmartCloudRestClientModule extends
         RestClientModule<IBMSmartCloudClient, IBMSmartCloudAsyncClient> {

   public IBMSmartCloudRestClientModule() {
      super(IBMSmartCloudClient.class, IBMSmartCloudAsyncClient.class);
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(
               IBMSmartCloudErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(
               IBMSmartCloudErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(
               IBMSmartCloudErrorHandler.class);
   }

   @Override
   protected void configure() {
      install(new IBMSmartCloudParserModule());
      super.configure();
   }
}
