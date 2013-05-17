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
package org.jclouds.softlayer.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.FluentIterable.from;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Location;
import org.jclouds.location.predicates.LocationPredicates;
import org.jclouds.softlayer.SoftLayerClient;
import org.jclouds.softlayer.domain.ProductItem;
import org.jclouds.softlayer.domain.ProductOrder;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.jclouds.softlayer.predicates.ProductItemPredicates;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class VirtualGuestToNodeMetadata implements Function<VirtualGuest, NodeMetadata> {

   public static final Map<VirtualGuest.State, Status> serverStateToNodeStatus = ImmutableMap
         .<VirtualGuest.State, Status> builder().put(VirtualGuest.State.HALTED, Status.PENDING)
         .put(VirtualGuest.State.PAUSED, Status.SUSPENDED).put(VirtualGuest.State.RUNNING, Status.RUNNING)
         .put(VirtualGuest.State.UNRECOGNIZED, Status.UNRECOGNIZED).build();

   private final Supplier<Set<? extends Location>> locations;
   private final GetHardwareForVirtualGuest hardware;
   private final GetImageForVirtualGuest images;
   private final GroupNamingConvention nodeNamingConvention;

   @Inject
   VirtualGuestToNodeMetadata(@Memoized Supplier<Set<? extends Location>> locations,
         GetHardwareForVirtualGuest hardware, GetImageForVirtualGuest images,
         GroupNamingConvention.Factory namingConvention) {
      this.nodeNamingConvention = checkNotNull(namingConvention, "namingConvention").createWithoutPrefix();
      this.locations = checkNotNull(locations, "locations");
      this.hardware = checkNotNull(hardware, "hardware");
      this.images = checkNotNull(images, "images");
   }

   @Override
   public NodeMetadata apply(VirtualGuest from) {
      // convert the result object to a jclouds NodeMetadata
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.ids(from.getId() + "");
      builder.name(from.getHostname());
      builder.hostname(from.getHostname());
      if (from.getDatacenter() != null)
         builder.location(from(locations.get()).firstMatch(
               LocationPredicates.idEquals(from.getDatacenter().getId() + "")).orNull());
      builder.group(nodeNamingConvention.groupInUniqueNameOrNull(from.getHostname()));

      Image image = images.getImage(from);
      if (image != null) {
         builder.imageId(image.getId());
         builder.operatingSystem(image.getOperatingSystem());
      }

      builder.hardware(hardware.getHardware(from));

      builder.status(serverStateToNodeStatus.get(from.getPowerState().getKeyName()));

      // These are null for 'bad' guest orders in the HALTED state.
      if (from.getPrimaryIpAddress() != null)
         builder.publicAddresses(ImmutableSet.<String> of(from.getPrimaryIpAddress()));
      if (from.getPrimaryBackendIpAddress() != null)
         builder.privateAddresses(ImmutableSet.<String> of(from.getPrimaryBackendIpAddress()));
      return builder.build();
   }

   @Singleton
   public static class GetHardwareForVirtualGuest {

      private final SoftLayerClient client;
      private final Function<Iterable<ProductItem>, Hardware> productItemsToHardware;

      @Inject
      public GetHardwareForVirtualGuest(SoftLayerClient client,
            Function<Iterable<ProductItem>, Hardware> productItemsToHardware) {
         this.client = checkNotNull(client, "client");
         this.productItemsToHardware = checkNotNull(productItemsToHardware, "productItemsToHardware");

      }

      public Hardware getHardware(VirtualGuest guest) {
         // 'bad' orders have no start cpu's and cause the order lookup to fail.
         if (guest.getStartCpus() < 1)
            return null;
         ProductOrder order = client.getVirtualGuestClient().getOrderTemplate(guest.getId());
         if (order == null)
            return null;
         Iterable<ProductItem> items = Iterables.transform(order.getPrices(), ProductItems.item());
         return productItemsToHardware.apply(items);
      }
   }

   @Singleton
   public static class GetImageForVirtualGuest {

      private SoftLayerClient client;

      @Inject
      public GetImageForVirtualGuest(SoftLayerClient client) {
         this.client = client;
      }

      public Image getImage(VirtualGuest guest) {
         // 'bad' orders have no start cpu's and cause the order lookup to fail.
         if (guest.getStartCpus() < 1)
            return null;
         ProductOrder order = client.getVirtualGuestClient().getOrderTemplate(guest.getId());
         if (order == null)
            return null;
         Iterable<ProductItem> items = Iterables.transform(order.getPrices(), ProductItems.item());
         ProductItem os = Iterables.find(items, ProductItemPredicates.categoryCode("os"));
         return new ProductItemToImage().apply(os);
      }
   }

}
