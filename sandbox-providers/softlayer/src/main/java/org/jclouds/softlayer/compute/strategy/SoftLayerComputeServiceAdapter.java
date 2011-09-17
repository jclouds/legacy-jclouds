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

import static com.google.common.base.Preconditions.checkNotNull;

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
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.ProductItemPrice;
import org.jclouds.softlayer.domain.ProductPackage;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.jclouds.softlayer.predicates.ProductPackagePredicates;
import org.jclouds.softlayer.reference.SoftLayerConstants;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * defines the connection between the {@link SoftLayerClient} implementation and the jclouds
 * {@link ComputeService}
 * 
 */
@Singleton
public class SoftLayerComputeServiceAdapter implements
         ComputeServiceAdapter<VirtualGuest, Set<ProductItemPrice>, ProductItemPrice, Datacenter> {
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
   public Iterable<Set<ProductItemPrice>> listHardwareProfiles() {
      // TODO: get the set of product item prices corresponding to the hardware profiles
      return ImmutableSet.of();
   }

   @Override
   public Iterable<ProductItemPrice> listImages() {
      // TODO: get the list of product item prices corresponding to images
      return ImmutableSet.of();
   }

   @Override
   public Iterable<VirtualGuest> listNodes() {
      return client.getVirtualGuestClient().listVirtualGuests();
   }

   @Override
   public Iterable<Datacenter> listLocations() {
      // TODO we should be able to specify a filter that gets the datacenters here.
      ProductPackage virtualGuestPackage = Iterables.find(client.getAccountClient().getActivePackages(),
               ProductPackagePredicates.named(virtualGuestPackageName));
      return client.getProductPackageClient().getProductPackage(virtualGuestPackage.getId()).getDatacenters();
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