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
package org.jclouds.softlayer.compute.strategy;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Credentials;
import org.jclouds.softlayer.SoftLayerClient;
import org.jclouds.softlayer.compute.functions.ProductItems;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.ProductItem;
import org.jclouds.softlayer.domain.ProductPackage;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.jclouds.softlayer.features.AccountClient;
import org.jclouds.softlayer.features.ProductPackageClient;
import org.jclouds.softlayer.reference.SoftLayerConstants;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.*;
import static org.jclouds.softlayer.predicates.ProductPackagePredicates.named;

/**
 * defines the connection between the {@link SoftLayerClient} implementation and the jclouds
 * {@link ComputeService}
 * 
 */
@Singleton
public class SoftLayerComputeServiceAdapter implements
      ComputeServiceAdapter<VirtualGuest, Set<ProductItem>, ProductItem, Datacenter> {

   public static final String SAN_DESCRIPTION_REGEX=".*GB \\(SAN\\).*";
   //TODO: Better to pass this in as a property like virtualGuestPackageName?
   private static final Float BOOT_VOLUME_CAPACITY = 100F;

   private final SoftLayerClient client;
   private final String virtualGuestPackageName;

   @Inject
   public SoftLayerComputeServiceAdapter(SoftLayerClient client,
            @Named(SoftLayerConstants.PROPERTY_SOFTLAYER_VIRTUALGUEST_PACKAGE_NAME) String virtualGuestPackageName) {
      this.client = checkNotNull(client, "client");
      this.virtualGuestPackageName = checkNotNull(virtualGuestPackageName, "virtualGuestPackageName");
   }

   @Override
   public VirtualGuest createNodeWithGroupEncodedIntoNameThenStoreCredentials(String tag, String name,
            Template template, Map<String, Credentials> credentialStore) {
      VirtualGuest from = null; // TODO create the backend object using parameters from the
                                // template. ex.
      // VirtualGuest from =
      // client.getVirtualGuestClient().createServerInDC(template.getLocation().getId(), name,
      // Long.parseLong(template.getImage().getProviderId()),
      // Long.parseLong(template.getHardware().getProviderId()));
      // store the credentials so that later functions can use them
      // credentialStore.put("node#"+ from.getId() + "", new Credentials(from.loginUser,
      // from.password));
      return from;
   }

   @Override
   public Iterable<Set<ProductItem>> listHardwareProfiles() {
      ProductPackage productPackage = getProductPackage();
      Set<ProductItem> items = productPackage.getItems();

      Iterable<ProductItem> cpuItems = Iterables.filter(items, units("PRIVATE_CORE"));
      Iterable<ProductItem> ramItems = Iterables.filter(items,categoryCode("ram"));
      Iterable<ProductItem> sanItems = Iterables.filter(items, Predicates.and(matches(SAN_DESCRIPTION_REGEX),categoryCode("one_time_charge")));

      Map<Float, ProductItem> cpuMap = Maps.uniqueIndex(cpuItems, ProductItems.capacity());
      Map<Float, ProductItem> ramMap = Maps.uniqueIndex(ramItems, ProductItems.capacity());
      Map<Float, ProductItem> sanMap = Maps.uniqueIndex(sanItems, ProductItems.capacity());

      final ProductItem bootVolume = sanMap.get(BOOT_VOLUME_CAPACITY);
      assert bootVolume!=null : "Boot volume capacity not found:"+BOOT_VOLUME_CAPACITY+", available:"+sanItems;

      Set<Set<ProductItem>> result = Sets.newLinkedHashSet();
      for(Map.Entry<Float, ProductItem> coresEntry : cpuMap.entrySet()) {
         Float cores = coresEntry.getKey();
         ProductItem ramItem = ramMap.get(cores);
         //Amount of RAM and number of cores must match.
         if(ramItem==null) continue;

         result.add(ImmutableSet.of(coresEntry.getValue(),ramItem,bootVolume));
      }

      return result;
   }

   @Override
   public Iterable<ProductItem> listImages() {
      return Iterables.filter(getProductPackage().getItems(), categoryCode("os"));
   }

   @Override
   public Iterable<VirtualGuest> listNodes() {
      return client.getVirtualGuestClient().listVirtualGuests();
   }

   @Override
   public Iterable<Datacenter> listLocations() {
      return getProductPackage().getDatacenters();
   }

   private ProductPackage getProductPackage() {
      AccountClient accountClient = client.getAccountClient();
      ProductPackageClient productPackageClient = client.getProductPackageClient();

      ProductPackage p = Iterables.find(accountClient.getActivePackages(),named(virtualGuestPackageName));
      return productPackageClient.getProductPackage(p.getId());
   }

   @Override
   public VirtualGuest getNode(String id) {
      long serverId = Long.parseLong(id);
      return client.getVirtualGuestClient().getVirtualGuest(serverId);
   }

   @Override
   public void destroyNode(String id) {
      // TODO
      // client.getVirtualGuestClient().destroyVirtualGuest(Long.parseLong(id));
   }

   @Override
   public void rebootNode(String id) {
      client.getVirtualGuestClient().rebootHardVirtualGuest(Long.parseLong(id));
   }

   @Override
   public void resumeNode(String id) {
      client.getVirtualGuestClient().resumeVirtualGuest(Long.parseLong(id));
   }

   @Override
   public void suspendNode(String id) {
      client.getVirtualGuestClient().pauseVirtualGuest(Long.parseLong(id));
   }
}