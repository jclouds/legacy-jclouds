/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.trmk.ecloud.config;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.trmk.ecloud.TerremarkECloudApi;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudApi;
import org.jclouds.trmk.vcloud_0_8.config.DefaultVCloudReferencesModule;
import org.jclouds.trmk.vcloud_0_8.config.TerremarkVCloudHttpApiModule;
import org.jclouds.trmk.vcloud_0_8.domain.Network;
import org.jclouds.trmk.vcloud_0_8.domain.NetworkExtendedInfo;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;

import com.google.common.base.Predicate;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 * Configures the VCloud authentication service connection, including logging
 * and http transport.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class TerremarkECloudHttpApiModule extends
        TerremarkVCloudHttpApiModule<TerremarkECloudApi> {

   @Provides
   @Singleton
   protected TerremarkVCloudApi provideTerremarkApi(TerremarkECloudApi in) {
      return in;
   }

   @Singleton
   public static class IsDMZNetwork implements Predicate<ReferenceType> {
      private final TerremarkECloudApi client;

      @Inject
      public IsDMZNetwork(TerremarkECloudApi client) {
         this.client = client;
      }

      @Override
      public boolean apply(ReferenceType arg0) {
         // TODO FIXME XXX: In Terremark Enterprise environment with multiple
         // VDC's
         // this does not
         // work well.
         // Each VDC will have different network subnets. So we cannot assume the
         // default VDC's
         // networks will
         // work with non-default VDC's. So make PROPERTY_VCLOUD_DEFAULT_NETWORK
         // optional. If
         // this property
         // is not set, they are expected to add NetworkConfig to the options
         // when
         // launching a
         // server.
         Network orgNetwork = client.getNetwork(arg0.getHref());
         NetworkExtendedInfo terremarkNetwork = client.getNetworkExtendedInfo(checkNotNull(
               checkNotNull(orgNetwork, "network at: " + arg0).getNetworkExtension(), "network extension for: " + arg0)
               .getHref());
         return checkNotNull(terremarkNetwork, "terremark network extension at: " + orgNetwork.getNetworkExtension())
               .getNetworkType() == NetworkExtendedInfo.Type.DMZ;
      }
   }

   @Override
   protected void installDefaultVCloudEndpointsModule() {
      install(new DefaultVCloudReferencesModule() {

         @Override
         protected Predicate<ReferenceType> provideDefaultNetworkSelector(Injector i) {
            return i.getInstance(IsDMZNetwork.class);
         }

      });

   }

}
