/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.inject.Singleton;

import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.terremark.ecloud.features.DataCenterOperationsAsyncClient;
import org.jclouds.terremark.ecloud.features.DataCenterOperationsClient;
import org.jclouds.terremark.ecloud.features.TagOperationsAsyncClient;
import org.jclouds.terremark.ecloud.features.TagOperationsClient;
import org.jclouds.vcloud.VCloudExpressAsyncClient;
import org.jclouds.vcloud.VCloudExpressClient;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.terremark.TerremarkECloudAsyncClient;
import org.jclouds.vcloud.terremark.TerremarkECloudClient;
import org.jclouds.vcloud.terremark.TerremarkVCloudAsyncClient;
import org.jclouds.vcloud.terremark.TerremarkVCloudClient;
import org.jclouds.vcloud.terremark.domain.TerremarkNetwork;
import org.jclouds.vcloud.terremark.domain.TerremarkOrgNetwork;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 * Configures the VCloud authentication service connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class TerremarkECloudRestClientModule extends
         TerremarkRestClientModule<TerremarkECloudClient, TerremarkECloudAsyncClient> {

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()//
            .put(DataCenterOperationsClient.class, DataCenterOperationsAsyncClient.class)//
            .put(TagOperationsClient.class, TagOperationsAsyncClient.class)//
            .build();

   public TerremarkECloudRestClientModule() {
      super(TerremarkECloudClient.class, TerremarkECloudAsyncClient.class, DELEGATE_MAP);
   }

   @Provides
   @Singleton
   protected VCloudExpressAsyncClient provideVCloudAsyncClient(TerremarkECloudAsyncClient in) {
      return in;
   }

   @Provides
   @Singleton
   protected VCloudExpressClient provideVCloudClient(TerremarkECloudClient in) {
      return in;
   }

   @Provides
   @Singleton
   protected TerremarkVCloudAsyncClient provideTerremarkAsyncClient(TerremarkECloudAsyncClient in) {
      return in;
   }

   @Provides
   @Singleton
   protected TerremarkVCloudClient provideTerremarkClient(TerremarkECloudClient in) {
      return in;
   }

   @Override
   protected URI findDefaultNetworkForVDC(org.jclouds.vcloud.domain.VDC vDC, Map<String, ReferenceType> networks,
            final Injector injector) {
      // TODO FIXME XXX: In Terremark Enterprise environment with multiple VDC's this does not
      // work well.
      // Each VDC will have differnt network subnets. So we cannot assume the default VDC's
      // networks will
      // work with non-default VDC's. So make PROPERTY_VCLOUD_DEFAULT_NETWORK optional. If
      // this property
      // is not set, they are expected to add NetworkConfig to the options when launching a
      // server.
      logger.warn("default network for vdc %s not set", vDC.getName());
      try {
         return Iterables.find(networks.values(), new Predicate<ReferenceType>() {

            @Override
            public boolean apply(ReferenceType input) {
               TerremarkOrgNetwork network = injector.getInstance(TerremarkECloudClient.class).getNetwork(
                        input.getHref());
               TerremarkNetwork terremarkNetwork = injector.getInstance(TerremarkECloudClient.class)
                        .getTerremarkNetwork(
                                 checkNotNull(checkNotNull(network, "network at: " + input).getNetworkExtension(),
                                          "network extension for: " + input).getHref());
               return checkNotNull(terremarkNetwork, "terremark network extension at: " + network.getNetworkExtension())
                        .getNetworkType() == TerremarkNetwork.Type.DMZ;
            }

         }).getHref();
      } catch (NoSuchElementException e) {
         throw new ResourceNotFoundException("no dmz networks in vdc " + vDC.getName() + ": " + networks);
      }
   }
}
