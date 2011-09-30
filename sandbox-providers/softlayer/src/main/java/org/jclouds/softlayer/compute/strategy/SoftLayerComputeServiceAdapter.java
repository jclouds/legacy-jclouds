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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.categoryCode;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.matches;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.units;
import static org.jclouds.softlayer.predicates.ProductPackagePredicates.named;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Credentials;
import org.jclouds.softlayer.SoftLayerClient;
import org.jclouds.softlayer.compute.functions.ProductItems;
import org.jclouds.softlayer.compute.options.SoftLayerTemplateOptions;
import org.jclouds.softlayer.domain.BillingItemVirtualGuest;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.OperatingSystem;
import org.jclouds.softlayer.domain.Password;
import org.jclouds.softlayer.domain.ProductItem;
import org.jclouds.softlayer.domain.ProductItemPrice;
import org.jclouds.softlayer.domain.ProductOrder;
import org.jclouds.softlayer.domain.ProductPackage;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.jclouds.softlayer.features.AccountClient;
import org.jclouds.softlayer.features.ProductPackageClient;
import org.jclouds.softlayer.reference.SoftLayerConstants;

import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * defines the connection between the {@link SoftLayerClient} implementation and the jclouds
 * {@link ComputeService}
 * 
 */
@Singleton
public class SoftLayerComputeServiceAdapter implements
      ComputeServiceAdapter<VirtualGuest, Set<ProductItem>, ProductItem, Datacenter> {

   public static final String SAN_DESCRIPTION_REGEX=".*GB \\(SAN\\).*";
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
   public VirtualGuest createNodeWithGroupEncodedIntoNameThenStoreCredentials(String group, String name,
            Template template, Map<String, Credentials> credentialStore) {
      checkNotNull(template, "template was null");
      checkNotNull(template.getOptions(), "template options was null");
      checkArgument(template.getOptions().getClass().isAssignableFrom(SoftLayerTemplateOptions.class),
               "options class %s should have been assignable from SoftLayerTemplateOptions", template.getOptions()
                        .getClass());
      
      Iterable<VirtualGuest> existing = findVirtualGuests(name,group);
      if(!Iterables.isEmpty(existing)) {
         throw new IllegalStateException(
               "VirtualGuest(s) already exist with hostname:"+name+", group:"+group+". Existing:"+existing);
      }

      VirtualGuest newGuest = VirtualGuest.builder()
                                          .domain(template.getOptions().as(SoftLayerTemplateOptions.class).getDomainName())
                                          .hostname(name)
                                          .build();

      ProductOrder order = ProductOrder.builder()
                                       .packageId(getProductPackage().getId())
                                       .location(template.getLocation().getId())
                                       .quantity(1)
                                       .useHourlyPricing(true)
                                       .prices(getPrices(template))
                                       .virtualGuest(newGuest)
                                       .build();

      client.getVirtualGuestClient().orderVirtualGuest(order);


      VirtualGuest result = Iterables.getOnlyElement(findVirtualGuests(name, group));
      Credentials credentials = new Credentials(null,null);

      // This information is not always available.
      OperatingSystem os = result.getOperatingSystem();
      if(os!=null) {
         Set<Password> passwords = os.getPasswords();
         if(passwords.size()>0) {
            Password pw = Iterables.get(passwords,0);
            credentials = new Credentials(pw.getUsername(),pw.getPassword());
         }
      }
      credentialStore.put("node#"+result.getId(),credentials);
      return result;
   }

   private Iterable<VirtualGuest> findVirtualGuests(String hostname,String domain) {
      checkNotNull(hostname,"hostname");
      checkNotNull(domain,"domain");

      Set<VirtualGuest> result = Sets.newLinkedHashSet();

      for( VirtualGuest guest : client.getVirtualGuestClient().listVirtualGuests())  {
         if ( guest.getHostname().equals(hostname) && guest.getDomain().equals(domain)) {
            result.add(guest);
         }
      }

      return result;
   }

   private Iterable<ProductItemPrice> getPrices(Template template) {
      Set<ProductItemPrice> result = Sets.newLinkedHashSet();

      int imageId = Integer.parseInt(template.getImage().getId());
      result.add(ProductItemPrice.builder().id(imageId).build());

      Iterable<String> hardwareIds = Splitter.on(",").split(template.getHardware().getId());
      for(String hardwareId: hardwareIds) {
         int id = Integer.parseInt(hardwareId);
         result.add(ProductItemPrice.builder().id(id).build());
      }

      result.addAll(SoftLayerConstants.DEFAULT_VIRTUAL_GUEST_PRICES);

      return result;
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
      VirtualGuest guest = getNode(id);
      if(guest==null) return;

      BillingItemVirtualGuest billingItem = guest.getBillingItem();
      if (billingItem==null) return;

      client.getVirtualGuestClient().cancelService(billingItem.getId());
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