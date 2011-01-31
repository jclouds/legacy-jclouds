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

package org.jclouds.elasticstack.compute.config;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.elasticstack.ElasticStackAsyncClient;
import org.jclouds.elasticstack.ElasticStackClient;
import org.jclouds.elasticstack.compute.ElasticStackComputeServiceAdapter;
import org.jclouds.elasticstack.compute.functions.ServerInfoToNodeMetadata;
import org.jclouds.elasticstack.compute.functions.WellKnownImageToImage;
import org.jclouds.elasticstack.compute.functions.ServerInfoToNodeMetadata.DeviceToVolume;
import org.jclouds.elasticstack.compute.functions.ServerInfoToNodeMetadata.FindImageForId;
import org.jclouds.elasticstack.compute.functions.ServerInfoToNodeMetadata.GetImageIdFromServer;
import org.jclouds.elasticstack.domain.Device;
import org.jclouds.elasticstack.domain.DriveInfo;
import org.jclouds.elasticstack.domain.Server;
import org.jclouds.elasticstack.domain.ServerInfo;
import org.jclouds.elasticstack.domain.WellKnownImage;
import org.jclouds.elasticstack.predicates.DriveClaimed;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.json.Json;
import org.jclouds.location.Provider;
import org.jclouds.location.suppliers.OnlyLocationOrFirstZone;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.util.Strings2;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
public class ElasticStackComputeServiceContextModule
         extends
         ComputeServiceAdapterContextModule<ElasticStackClient, ElasticStackAsyncClient, ServerInfo, Hardware, DriveInfo, Location> {

   public ElasticStackComputeServiceContextModule() {
      super(ElasticStackClient.class, ElasticStackAsyncClient.class);
   }

   @SuppressWarnings( { "unchecked", "rawtypes" })
   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceAdapter<ServerInfo, Hardware, DriveInfo, Location>>() {
      }).to(ElasticStackComputeServiceAdapter.class);
      bind(new TypeLiteral<Function<ServerInfo, NodeMetadata>>() {
      }).to(ServerInfoToNodeMetadata.class);
      bind(new TypeLiteral<Function<Image, Image>>() {
      }).to((Class) IdentityFunction.class);
      bind(new TypeLiteral<Function<Hardware, Hardware>>() {
      }).to((Class) IdentityFunction.class);
      bind(new TypeLiteral<Function<Location, Location>>() {
      }).to((Class) IdentityFunction.class);
      bind(new TypeLiteral<Function<Device, Volume>>() {
      }).to(DeviceToVolume.class);
      bind(new TypeLiteral<Function<Server, String>>() {
      }).to(GetImageIdFromServer.class);
      bind(new TypeLiteral<Function<String, Image>>() {
      }).to(FindImageForId.class);
      bind(new TypeLiteral<Function<DriveInfo, Image>>() {
      }).to(WellKnownImageToImage.class);
      bind(new TypeLiteral<Supplier<Location>>() {
      }).to(OnlyLocationOrFirstZone.class);
   }

   @Provides
   @Singleton
   protected Map<String, DriveInfo> cache(GetDrive getDrive) {
      return new MapMaker().makeComputingMap(getDrive);
   }

   @Singleton
   public static class GetDrive implements Function<String, DriveInfo> {
      private final ElasticStackClient client;

      @Inject
      public GetDrive(ElasticStackClient client) {
         this.client = client;
      }

      @Override
      public DriveInfo apply(String input) {
         return client.getDriveInfo(input);
      }
   }

   @Singleton
   @Provides
   protected Map<String, WellKnownImage> provideImages(Json json, @Provider String providerName) throws IOException {
      List<WellKnownImage> wellKnowns = json.fromJson(Strings2.toStringAndClose(getClass().getResourceAsStream(
               "/" + providerName + "/preinstalled_images.json")), new TypeLiteral<List<WellKnownImage>>() {
      }.getType());
      return Maps.uniqueIndex(wellKnowns, new Function<WellKnownImage, String>() {

         @Override
         public String apply(WellKnownImage input) {
            return input.getUuid();
         }

      });
   }

   @Provides
   @Singleton
   protected Predicate<DriveInfo> supplyDriveUnclaimed(DriveClaimed driveClaimed,
            ComputeServiceConstants.Timeouts timeouts) {
      return new RetryablePredicate<DriveInfo>(Predicates.not(driveClaimed), timeouts.nodeRunning, 1000,
               TimeUnit.MILLISECONDS);
   }
}