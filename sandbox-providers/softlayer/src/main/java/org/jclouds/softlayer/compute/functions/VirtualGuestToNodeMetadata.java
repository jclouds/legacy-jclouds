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
package org.jclouds.softlayer.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.parseGroupFromName;
import static org.jclouds.softlayer.compute.functions.ProductItemsToHardware.CORE_SPEED;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.jclouds.collect.FindResourceInSet;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.*;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.http.HttpResponseException;
import org.jclouds.softlayer.SoftLayerClient;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.ProductItem;
import org.jclouds.softlayer.domain.ProductOrder;
import org.jclouds.softlayer.domain.VirtualGuest;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.jclouds.softlayer.predicates.ProductItemPredicates;

/**
 * @author Adrian Cole
 */
@Singleton
public class VirtualGuestToNodeMetadata implements Function<VirtualGuest, NodeMetadata> {

   public static final Map<VirtualGuest.State, NodeState> serverStateToNodeState = ImmutableMap
            .<VirtualGuest.State, NodeState> builder().put(VirtualGuest.State.HALTED, NodeState.PENDING).put(
               VirtualGuest.State.PAUSED, NodeState.SUSPENDED).put(VirtualGuest.State.RUNNING, NodeState.RUNNING)
            .put(VirtualGuest.State.UNRECOGNIZED, NodeState.UNRECOGNIZED).build();

   private final Map<String, Credentials> credentialStore;
   private final FindLocationForVirtualGuest findLocationForVirtualGuest;
   private final GetHardwareForVirtualGuest getHardwareForVirtualGuest;
   private final GetImageForVirtualGuest getImageForVirtualGuest;

   @Inject
   VirtualGuestToNodeMetadata(Map<String, Credentials> credentialStore,
            FindLocationForVirtualGuest findLocationForVirtualGuest,
            GetHardwareForVirtualGuest getHardwareForVirtualGuest,
            GetImageForVirtualGuest getImageForVirtualGuest
            ) {
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.findLocationForVirtualGuest = checkNotNull(findLocationForVirtualGuest, "findLocationForVirtualGuest");
      this.getHardwareForVirtualGuest = checkNotNull(getHardwareForVirtualGuest, "getHardwareForVirtualGuest");
      this.getImageForVirtualGuest = checkNotNull(getImageForVirtualGuest, "getImageForVirtualGuest");
   }

   @Override
   public NodeMetadata apply(VirtualGuest from) {
      // convert the result object to a jclouds NodeMetadata
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.ids(from.getId() + "");
      builder.name(from.getHostname());
      builder.hostname(from.getHostname());
      builder.location(findLocationForVirtualGuest.apply(from));
      builder.group(parseGroupFromName(from.getHostname()));

      Image image = getImageForVirtualGuest.getImage(from);
      if (image!=null) {
         builder.imageId(image.getId());
         builder.operatingSystem(image.getOperatingSystem());
      }

      Hardware hardware = getHardwareForVirtualGuest.getHardware(from);
      if (hardware!=null) builder.hardware(hardware);

      builder.state(serverStateToNodeState.get(from.getPowerState().getKeyName()));

      // These are null for 'bad' guest orders in the HALTED state.
      if (from.getPrimaryIpAddress() != null)
         builder.publicAddresses(ImmutableSet.<String> of(from.getPrimaryIpAddress()));
      if (from.getPrimaryBackendIpAddress() != null)
         builder.privateAddresses(ImmutableSet.<String> of(from.getPrimaryBackendIpAddress()));

      builder.credentials(credentialStore.get("node#" + from.getId()));
      return builder.build();
   }

   @Singleton
   public static class FindLocationForVirtualGuest extends FindResourceInSet<VirtualGuest, Location> {

      @Inject
      public FindLocationForVirtualGuest(@Memoized Supplier<Set<? extends Location>> location) {
         super(location);
      }

      @Override
      public boolean matches(VirtualGuest from, Location input) {
         Datacenter dc = from.getDatacenter();
         if (dc == null)
            return false;
         return input.getId().equals(Integer.toString(dc.getId()));
      }
   }

   @Singleton
   public static class GetHardwareForVirtualGuest {

      private SoftLayerClient client;

      @Inject
      public GetHardwareForVirtualGuest(SoftLayerClient client) {
         this.client = client;
      }

      public Hardware getHardware(VirtualGuest guest) {
         // 'bad' orders have no start cpu's and cause the order lookup to fail.
         if (guest.getStartCpus()<1) return null;
         try {
            ProductOrder order = client.getVirtualGuestClient().getOrderTemplate(guest.getId());
            Iterable<ProductItem> items = Iterables.transform(order.getPrices(),ProductItems.item());
            return new ProductItemsToHardware().apply(Sets.newLinkedHashSet(items));
         } catch (HttpResponseException e) {
            //For singapore
            return null;
         }
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
         if (guest.getStartCpus()<1) return null;
         try {
            ProductOrder order = client.getVirtualGuestClient().getOrderTemplate(guest.getId());
            Iterable<ProductItem> items = Iterables.transform(order.getPrices(),ProductItems.item());
            ProductItem os = Iterables.find(items, ProductItemPredicates.categoryCode("os"));
            return new ProductItemToImage().apply(os);
         } catch (HttpResponseException e) {
            //For singapore
            return null;
         }
      }
   }

}
