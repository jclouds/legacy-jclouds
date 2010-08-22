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

package org.jclouds.vcloud.terremark.config;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;

import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.suppliers.RetryOnTimeOutButNotOnAuthorizationExceptionSupplier;
import org.jclouds.util.Utils;
import org.jclouds.vcloud.VCloudExpressAsyncClient;
import org.jclouds.vcloud.VCloudExpressClient;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.VCloudSession;
import org.jclouds.vcloud.terremark.TerremarkVCloudAsyncClient;
import org.jclouds.vcloud.terremark.TerremarkVCloudClient;
import org.jclouds.vcloud.terremark.TerremarkVCloudExpressAsyncClient;
import org.jclouds.vcloud.terremark.TerremarkVCloudExpressClient;
import org.jclouds.vcloud.terremark.endpoints.KeysList;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
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
   protected VCloudExpressAsyncClient provideVCloudAsyncClient(TerremarkVCloudExpressAsyncClient in) {
      return in;
   }

   @Provides
   @Singleton
   protected VCloudExpressClient provideVCloudClient(TerremarkVCloudExpressClient in) {
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

   @Singleton
   public static class OrgNameToKeysListSupplier implements Supplier<Map<String, NamedResource>> {
      protected final Supplier<VCloudSession> sessionSupplier;
      private final TerremarkVCloudExpressClient client;

      @Inject
      protected OrgNameToKeysListSupplier(Supplier<VCloudSession> sessionSupplier, TerremarkVCloudExpressClient client) {
         this.sessionSupplier = sessionSupplier;
         this.client = client;
      }

      @Override
      public Map<String, NamedResource> get() {
         return Maps.transformValues(sessionSupplier.get().getOrgs(), new Function<NamedResource, NamedResource>() {

            @Override
            public NamedResource apply(NamedResource from) {
               return client.findOrgNamed(from.getName()).getKeysList();
            }

         });
      }

   }

   @Provides
   @Singleton
   @KeysList
   protected Supplier<Map<String, NamedResource>> provideOrgToKeysListCache(
            @Named(PROPERTY_SESSION_INTERVAL) long seconds, final OrgNameToKeysListSupplier supplier) {
      return new RetryOnTimeOutButNotOnAuthorizationExceptionSupplier<Map<String, NamedResource>>(authException,
               seconds, new Supplier<Map<String, NamedResource>>() {
                  @Override
                  public Map<String, NamedResource> get() {
                     return supplier.get();
                  }

               });
   }

   @Singleton
   @Provides
   @Named("CreateKey")
   String provideCreateKey() throws IOException {
      return Utils.toStringAndClose(getClass().getResourceAsStream("/terremark/CreateKey.xml"));
   }

}
