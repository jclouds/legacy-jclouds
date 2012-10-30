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
package org.jclouds.http.ning.config;

import javax.inject.Singleton;

import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.TransformingHttpCommandExecutorService;
import org.jclouds.http.TransformingHttpCommandExecutorServiceImpl;
import org.jclouds.http.config.ConfiguresHttpCommandExecutorService;
import org.jclouds.http.ning.NingHttpCommandExecutorService;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;

/**
 * Configures {@link NingHttpCommandExecutorService}.
 * 
 * Note that this uses threads
 * 
 * @author Sam Tunnicliffe
 * @author Adrian Cole
 */
@ConfiguresHttpCommandExecutorService
public class NingHttpCommandExecutorServiceModule extends AbstractModule {

   @Override
   protected void configure() {
      bindClient();
   }

   @Singleton
   @Provides
   AsyncHttpClient provideNingClient() {
      AsyncHttpClientConfig config = new AsyncHttpClientConfig.Builder().setFollowRedirects(true)
               .build();
      return new AsyncHttpClient(config);
   }

   protected void bindClient() {
      bind(HttpCommandExecutorService.class).to(NingHttpCommandExecutorService.class).in(
               Scopes.SINGLETON);

      bind(TransformingHttpCommandExecutorService.class).to(
               TransformingHttpCommandExecutorServiceImpl.class).in(Scopes.SINGLETON);
   }

}
