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

package org.jclouds.servermanager.compute.config;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.domain.Location;
import org.jclouds.location.suppliers.OnlyLocationOrFirstZone;
import org.jclouds.servermanager.Datacenter;
import org.jclouds.servermanager.Hardware;
import org.jclouds.servermanager.Image;
import org.jclouds.servermanager.Server;
import org.jclouds.servermanager.ServerManager;
import org.jclouds.servermanager.compute.functions.DatacenterToLocation;
import org.jclouds.servermanager.compute.functions.ServerManagerHardwareToHardware;
import org.jclouds.servermanager.compute.functions.ServerManagerImageToImage;
import org.jclouds.servermanager.compute.functions.ServerToNodeMetadata;
import org.jclouds.servermanager.compute.strategy.ServerManagerComputeServiceAdapter;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
public class ServerManagerComputeServiceContextModule extends
      ComputeServiceAdapterContextModule<ServerManager, ServerManager, Server, Hardware, Image, Datacenter> {

   public ServerManagerComputeServiceContextModule() {
      super(ServerManager.class, ServerManager.class);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceAdapter<Server, Hardware, Image, Datacenter>>() {
      }).to(ServerManagerComputeServiceAdapter.class);
      bind(new TypeLiteral<Function<Server, NodeMetadata>>() {
      }).to(ServerToNodeMetadata.class);
      bind(new TypeLiteral<Function<Image, org.jclouds.compute.domain.Image>>() {
      }).to(ServerManagerImageToImage.class);
      bind(new TypeLiteral<Function<Hardware, org.jclouds.compute.domain.Hardware>>() {
      }).to(ServerManagerHardwareToHardware.class);
      bind(new TypeLiteral<Function<Datacenter, Location>>() {
      }).to(DatacenterToLocation.class);
      bind(new TypeLiteral<Supplier<Location>>() {
      }).to(OnlyLocationOrFirstZone.class);
   }
}