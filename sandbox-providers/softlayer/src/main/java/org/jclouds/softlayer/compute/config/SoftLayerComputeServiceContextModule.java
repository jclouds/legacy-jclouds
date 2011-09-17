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
package org.jclouds.softlayer.compute.config;

import java.util.Set;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.domain.Location;
import org.jclouds.location.suppliers.OnlyLocationOrFirstZone;
import org.jclouds.softlayer.SoftLayerAsyncClient;
import org.jclouds.softlayer.SoftLayerClient;
import org.jclouds.softlayer.compute.functions.DatacenterToLocation;
import org.jclouds.softlayer.compute.functions.ProductItemPriceToImage;
import org.jclouds.softlayer.compute.functions.ProductItemPricesToHardware;
import org.jclouds.softlayer.compute.functions.VirtualGuestToNodeMetadata;
import org.jclouds.softlayer.compute.strategy.SoftLayerComputeServiceAdapter;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.ProductItemPrice;
import org.jclouds.softlayer.domain.VirtualGuest;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
public class SoftLayerComputeServiceContextModule extends
      ComputeServiceAdapterContextModule<SoftLayerClient, SoftLayerAsyncClient, VirtualGuest, Set<ProductItemPrice>, ProductItemPrice, Datacenter> {

   public SoftLayerComputeServiceContextModule() {
      super(SoftLayerClient.class, SoftLayerAsyncClient.class);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceAdapter<VirtualGuest, Set<ProductItemPrice>, ProductItemPrice, Datacenter>>() {
      }).to(SoftLayerComputeServiceAdapter.class);
      bind(new TypeLiteral<Function<VirtualGuest, NodeMetadata>>() {
      }).to(VirtualGuestToNodeMetadata.class);
      bind(new TypeLiteral<Function<ProductItemPrice, org.jclouds.compute.domain.Image>>() {
      }).to(ProductItemPriceToImage.class);
      bind(new TypeLiteral<Function<Set<ProductItemPrice>, org.jclouds.compute.domain.Hardware>>() {
      }).to(ProductItemPricesToHardware.class);
      bind(new TypeLiteral<Function<Datacenter, Location>>() {
      }).to(DatacenterToLocation.class);
      bind(new TypeLiteral<Supplier<Location>>() {
      }).to(OnlyLocationOrFirstZone.class);
   }
}