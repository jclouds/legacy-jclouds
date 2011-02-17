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

package org.jclouds.cloudsigma.compute.config;

import static org.jclouds.compute.domain.OsFamily.UBUNTU;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.cloudsigma.CloudSigmaAsyncClient;
import org.jclouds.cloudsigma.CloudSigmaClient;
import org.jclouds.cloudsigma.compute.CloudSigmaComputeServiceAdapter;
import org.jclouds.cloudsigma.compute.functions.ParseOsFamilyVersion64BitFromImageName;
import org.jclouds.cloudsigma.compute.functions.PreinstalledDiskToImage;
import org.jclouds.cloudsigma.compute.functions.ServerInfoToNodeMetadata;
import org.jclouds.cloudsigma.compute.functions.ServerInfoToNodeMetadata.DeviceToVolume;
import org.jclouds.cloudsigma.compute.functions.ServerInfoToNodeMetadata.FindImageForId;
import org.jclouds.cloudsigma.compute.functions.ServerInfoToNodeMetadata.GetImageIdFromServer;
import org.jclouds.cloudsigma.domain.Device;
import org.jclouds.cloudsigma.domain.DriveInfo;
import org.jclouds.cloudsigma.domain.Server;
import org.jclouds.cloudsigma.domain.ServerInfo;
import org.jclouds.cloudsigma.predicates.DriveClaimed;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.os.OsFamilyVersion64Bit;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.location.suppliers.OnlyLocationOrFirstZone;
import org.jclouds.predicates.RetryablePredicate;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.MapMaker;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
public class CloudSigmaComputeServiceContextModule
      extends
      ComputeServiceAdapterContextModule<CloudSigmaClient, CloudSigmaAsyncClient, ServerInfo, Hardware, DriveInfo, Location> {

   public CloudSigmaComputeServiceContextModule() {
      super(CloudSigmaClient.class, CloudSigmaAsyncClient.class);
   }

   @Override
   protected TemplateBuilder provideTemplate(Injector injector, TemplateBuilder template) {
      return template.osFamily(UBUNTU).osVersionMatches("10.10").os64Bit(true).minRam(1024);
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceAdapter<ServerInfo, Hardware, DriveInfo, Location>>() {
      }).to(CloudSigmaComputeServiceAdapter.class);
      bind(new TypeLiteral<Function<ServerInfo, NodeMetadata>>() {
      }).to(ServerInfoToNodeMetadata.class);
      bind(new TypeLiteral<Function<Hardware, Hardware>>() {
      }).to((Class) IdentityFunction.class);
      bind(new TypeLiteral<Function<DriveInfo, Image>>() {
      }).to(PreinstalledDiskToImage.class);
      bind(new TypeLiteral<Function<Location, Location>>() {
      }).to((Class) IdentityFunction.class);
      bind(new TypeLiteral<Function<Device, Volume>>() {
      }).to(DeviceToVolume.class);
      bind(new TypeLiteral<Function<Server, String>>() {
      }).to(GetImageIdFromServer.class);
      bind(new TypeLiteral<Function<String, Image>>() {
      }).to(FindImageForId.class);
      bind(new TypeLiteral<Function<String, OsFamilyVersion64Bit>>() {
      }).to(ParseOsFamilyVersion64BitFromImageName.class);
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
      private final CloudSigmaClient client;

      @Inject
      public GetDrive(CloudSigmaClient client) {
         this.client = client;
      }

      @Override
      public DriveInfo apply(String input) {
         return client.getDriveInfo(input);
      }
   }

   @Provides
   @Singleton
   protected Predicate<DriveInfo> supplyDriveUnclaimed(DriveClaimed driveClaimed,
         ComputeServiceConstants.Timeouts timeouts) {
      return new RetryablePredicate<DriveInfo>(Predicates.not(driveClaimed), timeouts.nodeRunning, 1000,
            TimeUnit.MILLISECONDS);
   }
}