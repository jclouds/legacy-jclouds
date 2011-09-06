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
package org.jclouds.twitter.config;

import org.jclouds.http.RequiresHttp;
import org.jclouds.json.config.GsonModule.CDateAdapter;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.twitter.TwitterAsyncClient;
import org.jclouds.twitter.TwitterClient;

/**
 * Configures the twitter connection.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class TwitterRestClientModule extends RestClientModule<TwitterClient, TwitterAsyncClient> {

   public TwitterRestClientModule() {
      super(TwitterClient.class, TwitterAsyncClient.class);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(DateAdapter.class).to(CDateAdapter.class);
   }

   @Override
   protected void bindErrorHandlers() {
//      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(TwitterErrorHandler.class);
//      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(TwitterErrorHandler.class);
//      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(TwitterErrorHandler.class);
   }

   @Override
   protected void bindRetryHandlers() {
      // TODO
   }

}
