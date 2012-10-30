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
package org.jclouds.mezeo.pcs.config;

import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.mezeo.pcs.PCSAsyncClient;
import org.jclouds.mezeo.pcs.PCSClient;
import org.jclouds.mezeo.pcs.PCSCloudAsyncClient;
import org.jclouds.mezeo.pcs.PCSCloudAsyncClient.Response;
import org.jclouds.mezeo.pcs.endpoints.Contacts;
import org.jclouds.mezeo.pcs.endpoints.Metacontainers;
import org.jclouds.mezeo.pcs.endpoints.Projects;
import org.jclouds.mezeo.pcs.endpoints.Recyclebin;
import org.jclouds.mezeo.pcs.endpoints.RootContainer;
import org.jclouds.mezeo.pcs.endpoints.Shares;
import org.jclouds.mezeo.pcs.endpoints.Tags;
import org.jclouds.mezeo.pcs.endpoints.WebDAV;
import org.jclouds.mezeo.pcs.handlers.PCSClientErrorRetryHandler;
import org.jclouds.rest.AsyncClientFactory;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

import com.google.inject.Provides;

/**
 * Configures the PCSCloudModule authentication service connection.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class PCSRestClientModule extends RestClientModule<PCSClient, PCSAsyncClient> {

   @Override
   protected void configure() {
      install(new PCSObjectModule());
      super.configure();
   }

   public PCSRestClientModule() {
      super(PCSClient.class, PCSAsyncClient.class);
   }

   @Provides
   @Singleton
   protected Response provideCloudResponse(AsyncClientFactory factory) throws InterruptedException, ExecutionException,
         TimeoutException {
      return factory.create(PCSCloudAsyncClient.class).authenticate().get(10, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   @WebDAV
   protected URI provideWebDAVURI(@Named(Constants.PROPERTY_ENDPOINT) String endpoint) {
      return URI.create(endpoint + "/dav");
   }

   @Provides
   @Singleton
   @Contacts
   protected URI provideContactsUrl(Response response) {
      return response.getContactsUrl();
   }

   @Provides
   @Singleton
   @Metacontainers
   protected URI provideMetacontainersUrl(Response response) {
      return response.getMetacontainersUrl();
   }

   @Provides
   @Singleton
   @Projects
   protected URI provideProjectsUrl(Response response) {
      return response.getProjectsUrl();
   }

   @Provides
   @Singleton
   @Recyclebin
   protected URI provideRecyclebinUrl(Response response) {
      return response.getRecyclebinUrl();
   }

   @Provides
   @Singleton
   @RootContainer
   protected URI provideRootContainerUrl(Response response) {
      return response.getRootContainerUrl();
   }

   @Provides
   @Singleton
   @Shares
   protected URI provideSharesUrl(Response response) {
      return response.getSharesUrl();
   }

   @Provides
   @Singleton
   @Tags
   protected URI provideTagsUrl(Response response) {
      return response.getTagsUrl();
   }

   @Override
   protected void bindRetryHandlers() {
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(PCSClientErrorRetryHandler.class);
   }

}
