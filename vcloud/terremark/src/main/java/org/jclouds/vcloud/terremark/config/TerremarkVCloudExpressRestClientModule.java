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
package org.jclouds.vcloud.terremark.config;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.concurrent.RetryOnTimeOutExceptionSupplier;
import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.util.Utils;
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.internal.VCloudLoginAsyncClient.VCloudSession;
import org.jclouds.vcloud.terremark.TerremarkVCloudAsyncClient;
import org.jclouds.vcloud.terremark.TerremarkVCloudClient;
import org.jclouds.vcloud.terremark.TerremarkVCloudExpressAsyncClient;
import org.jclouds.vcloud.terremark.TerremarkVCloudExpressClient;
import org.jclouds.vcloud.terremark.endpoints.KeysList;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.inject.Provides;

/**
 * Configures the VCloud authentication service connection, including logging
 * and http transport.
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
   protected VCloudAsyncClient provideVCloudAsyncClient(TerremarkVCloudExpressAsyncClient in) {
      return in;
   }

   @Provides
   @Singleton
   protected VCloudClient provideVCloudClient(TerremarkVCloudExpressClient in) {
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
               return client.getOrganizationNamed(from.getName()).getKeysList();
            }

         });
      }

   }

   @Provides
   @Singleton
   @KeysList
   protected Supplier<Map<String, NamedResource>> provideOrgToKeysListCache(
         @Named(PROPERTY_SESSION_INTERVAL) long seconds, final OrgNameToKeysListSupplier supplier) {
      return Suppliers.memoizeWithExpiration(new RetryOnTimeOutExceptionSupplier<Map<String, NamedResource>>(
            new Supplier<Map<String, NamedResource>>() {
               public Map<String, NamedResource> get() {
                  // http://code.google.com/p/google-guice/issues/detail?id=483
                  // guice doesn't remember when singleton providers throw
                  // exceptions.
                  // in this case, if describeRegions fails, it is called
                  // again for
                  // each provider method that depends on it. To
                  // short-circuit this,
                  // we remember the last exception trusting that guice is
                  // single-threaded
                  if (TerremarkVCloudExpressRestClientModule.this.authException != null)
                     throw TerremarkVCloudExpressRestClientModule.this.authException;
                  try {
                     return supplier.get();
                  } catch (AuthorizationException e) {
                     TerremarkVCloudExpressRestClientModule.this.authException = e;
                     throw e;
                  } catch (Exception e) {
                     Throwables.propagate(e);
                     assert false : e;
                     return null;
                  }
               }

            }), seconds, TimeUnit.SECONDS);
   }

   @Singleton
   @Provides
   @Named("CreateKey")
   String provideCreateKey() throws IOException {
      return Utils.toStringAndClose(getClass().getResourceAsStream("/terremark/CreateKey.xml"));
   }

}
