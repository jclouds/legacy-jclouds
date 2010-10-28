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

package org.jclouds.compute.stub.config;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.compute.JCloudsNativeComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.rest.ResourceNotFoundException;

import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class StubComputeServiceAdapter implements JCloudsNativeComputeServiceAdapter {
   private final Supplier<Location> location;
   private final ConcurrentMap<String, NodeMetadata> nodes;
   private final Provider<Integer> idProvider;
   private final String publicIpPrefix;
   private final String privateIpPrefix;
   private final String passwordPrefix;
   private final String providerName;

   @Inject
   public StubComputeServiceAdapter(ConcurrentMap<String, NodeMetadata> nodes, Supplier<Location> location,
            @Named("NODE_ID") Provider<Integer> idProvider, @Named("PUBLIC_IP_PREFIX") String publicIpPrefix,
            @Named("PRIVATE_IP_PREFIX") String privateIpPrefix, @Named("PASSWORD_PREFIX") String passwordPrefix,
            @org.jclouds.rest.annotations.Provider String providerName) {
      this.nodes = nodes;
      this.location = location;
      this.idProvider = idProvider;
      this.publicIpPrefix = publicIpPrefix;
      this.privateIpPrefix = privateIpPrefix;
      this.passwordPrefix = passwordPrefix;
      this.providerName = providerName;
   }

   @Override
   public NodeMetadata runNodeWithTagAndNameAndStoreCredentials(String tag, String name, Template template,
            Map<String, Credentials> credentialStore) {
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      String id = idProvider.get() + "";
      builder.ids(id);
      builder.name(name);
      builder.tag(tag);
      builder.location(location.get());
      builder.imageId(template.getImage().getId());
      builder.operatingSystem(template.getImage().getOperatingSystem());
      builder.state(NodeState.PENDING);
      builder.publicAddresses(ImmutableSet.<String> of(publicIpPrefix + id));
      builder.privateAddresses(ImmutableSet.<String> of(privateIpPrefix + id));
      builder.credentials(new Credentials("root", passwordPrefix + id));
      NodeMetadata node = builder.build();
      credentialStore.put("node#" + node.getId(), node.getCredentials());
      nodes.put(node.getId(), node);
      StubComputeServiceDependenciesModule.setState(node, NodeState.RUNNING, 100);
      return node;
   }

   @Override
   public Iterable<Hardware> listHardwareProfiles() {
      return ImmutableSet.<Hardware> of(StubComputeServiceDependenciesModule.stub("small", 1, 1740, 160),
               StubComputeServiceDependenciesModule.stub("medium", 4, 7680, 850), StubComputeServiceDependenciesModule
                        .stub("large", 8, 15360, 1690));
   }

   @Override
   public Iterable<Image> listImages() {
      Location zone = location.get().getParent();
      String parentId = zone.getId();
      Credentials defaultCredentials = new Credentials("root", null);
      return ImmutableSet.<Image> of(new ImageBuilder().providerId("1").name(OsFamily.UBUNTU.name())
               .id(parentId + "/1").location(zone).operatingSystem(
                        new OperatingSystem(OsFamily.UBUNTU, "ubuntu 32", null, "X86_32", "ubuntu 32", false))
               .description("stub ubuntu 32").defaultCredentials(defaultCredentials).build(), //
               new ImageBuilder().providerId("2").name(OsFamily.UBUNTU.name()).id(parentId + "/2").location(zone)
                        .operatingSystem(
                                 new OperatingSystem(OsFamily.UBUNTU, "ubuntu 64", null, "X86_64", "ubuntu 64", true))
                        .description("stub ubuntu 64").defaultCredentials(defaultCredentials).build(), //
               new ImageBuilder().providerId("3").name(OsFamily.CENTOS.name()).id(parentId + "/3").location(zone)
                        .operatingSystem(
                                 new OperatingSystem(OsFamily.CENTOS, "centos 64", null, "X86_64", "centos 64", true))
                        .description("stub centos 64").defaultCredentials(defaultCredentials).build() //

               );
   }

   @Override
   public Iterable<NodeMetadata> listNodes() {
      return nodes.values();
   }

   @Override
   public Iterable<Location> listLocations() {
      Location provider = new LocationImpl(LocationScope.PROVIDER, providerName, providerName, null);
      Location region = new LocationImpl(LocationScope.REGION, providerName + "region", providerName + "region",
               provider);
      return ImmutableSet.<Location> of(new LocationImpl(LocationScope.ZONE, providerName + "zone", providerName
               + "zone", region));
   }

   @Override
   public NodeMetadata getNode(String id) {
      return nodes.get(id);
   }

   @Override
   public void destroyNode(final String id) {
      NodeMetadata node = nodes.get(id);
      if (node == null)
         return;
      StubComputeServiceDependenciesModule.setState(node, NodeState.PENDING, 0);
      StubComputeServiceDependenciesModule.setState(node, NodeState.TERMINATED, 50);
      StubComputeServiceDependenciesModule.service.execute(new Runnable() {

         @Override
         public void run() {
            try {
               Thread.sleep(200);
            } catch (InterruptedException e) {
               Throwables.propagate(e);
            } finally {
               nodes.remove(id);
            }
         }

      });
   }

   @Override
   public void rebootNode(String id) {
      NodeMetadata node = nodes.get(id);
      if (node == null)
         throw new ResourceNotFoundException("node not found: " + id);
      StubComputeServiceDependenciesModule.setState(node, NodeState.PENDING, 0);
      StubComputeServiceDependenciesModule.setState(node, NodeState.RUNNING, 50);
   }
}