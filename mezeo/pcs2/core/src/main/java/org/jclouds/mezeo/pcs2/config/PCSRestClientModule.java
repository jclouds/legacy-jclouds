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
package org.jclouds.mezeo.pcs2.config;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.mezeo.pcs2.PCS;
import org.jclouds.mezeo.pcs2.PCSAsyncClient;
import org.jclouds.mezeo.pcs2.PCSClient;
import org.jclouds.mezeo.pcs2.PCSCloud;
import org.jclouds.mezeo.pcs2.PCSCloud.Response;
import org.jclouds.mezeo.pcs2.endpoints.Contacts;
import org.jclouds.mezeo.pcs2.endpoints.Metacontainers;
import org.jclouds.mezeo.pcs2.endpoints.Projects;
import org.jclouds.mezeo.pcs2.endpoints.Recyclebin;
import org.jclouds.mezeo.pcs2.endpoints.RootContainer;
import org.jclouds.mezeo.pcs2.endpoints.Shares;
import org.jclouds.mezeo.pcs2.endpoints.Tags;
import org.jclouds.mezeo.pcs2.endpoints.WebDAV;
import org.jclouds.mezeo.pcs2.handlers.PCSClientErrorRetryHandler;
import org.jclouds.mezeo.pcs2.reference.PCSConstants;
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
   public BasicAuthentication provideBasicAuthentication(
            @Named(PCSConstants.PROPERTY_PCS2_USER) String user,
            @Named(PCSConstants.PROPERTY_PCS2_PASSWORD) String password,
            EncryptionService encryptionService) throws UnsupportedEncodingException {
      return new BasicAuthentication(user, password, encryptionService);
   }

   @Provides
   @Singleton
   protected Response provideCloudResponse(AsyncClientFactory factory, @PCS URI authenticationUri)
            throws InterruptedException, ExecutionException, TimeoutException {
      return factory.create(PCSCloud.class).authenticate().get(10, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   @PCS
   protected URI provideAuthenticationURI(
            @Named(PCSConstants.PROPERTY_PCS2_ENDPOINT) String endpoint) {
      return URI.create(endpoint);
   }

   @Provides
   @Singleton
   @WebDAV
   protected URI provideWebDAVURI(@Named(PCSConstants.PROPERTY_PCS2_ENDPOINT) String endpoint) {
      return URI.create(endpoint.replaceAll("v2", "dav"));
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
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(
               PCSClientErrorRetryHandler.class);
   }

}