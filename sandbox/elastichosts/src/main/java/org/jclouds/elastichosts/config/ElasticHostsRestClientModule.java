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

package org.jclouds.elastichosts.config;

import org.jclouds.elastichosts.ElasticHostsAsyncClient;
import org.jclouds.elastichosts.ElasticHostsClient;
import org.jclouds.elastichosts.handlers.ElasticHostsErrorHandler;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

/**
 * Configures the ElasticHosts connection.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class ElasticHostsRestClientModule extends
         RestClientModule<ElasticHostsClient, ElasticHostsAsyncClient> {

   public ElasticHostsRestClientModule() {
      super(ElasticHostsClient.class, ElasticHostsAsyncClient.class);
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(ElasticHostsErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(ElasticHostsErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(ElasticHostsErrorHandler.class);
   }

   @Override
   protected void bindRetryHandlers() {
      // TODO
   }

}
