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
import java.util.Set;
import java.util.Map.Entry;
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
import org.jclouds.location.suppliers.JustProvider;
import org.jclouds.rest.ResourceNotFoundException;

import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

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
   private final Supplier<Set<? extends Location>> locationSupplier;
   private final Map<OsFamily, Map<String, String>> osToVersionMap;

   @Inject
   public StubComputeServiceAdapter(ConcurrentMap<String, NodeMetadata> nodes, Supplier<Location> location,
            @Named("NODE_ID") Provider<Integer> idProvider, @Named("PUBLIC_IP_PREFIX") String publicIpPrefix,
            @Named("PRIVATE_IP_PREFIX") String privateIpPrefix, @Named("PASSWORD_PREFIX") String passwordPrefix,
            JustProvider locationSupplier, Map<OsFamily, Map<String, String>> osToVersionMap) {
      this.nodes = nodes;
      this.location = location;
      this.idProvider = idProvider;
      this.publicIpPrefix = publicIpPrefix;
      this.privateIpPrefix = privateIpPrefix;
      this.passwordPrefix = passwordPrefix;
      this.locationSupplier = locationSupplier;
      this.osToVersionMap = osToVersionMap;
   }

   @Override
   public NodeMetadata createNodeWithGroupEncodedIntoNameThenStoreCredentials(String group, String name, Template template,
            Map<String, Credentials> credentialStore) {
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      String id = idProvider.get() + "";
      builder.ids(id);
      builder.name(name);
      builder.group(group);
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
      Credentials defaultCredentials = new Credentials("root", null);
      Set<Image> images = Sets.newLinkedHashSet();
      int id = 1;
      for (boolean is64Bit : new boolean[] { true, false })
         for (Entry<OsFamily, Map<String, String>> osVersions : this.osToVersionMap.entrySet()) {
            for (String version : Sets.newLinkedHashSet(osVersions.getValue().values())) {
               String desc = String.format("stub %s %s", osVersions.getKey(), is64Bit);
               images.add(new ImageBuilder().ids(id++ + "").name(osVersions.getKey().name()).location(location.get())
                        .operatingSystem(new OperatingSystem(osVersions.getKey(), desc, version, null, desc, is64Bit))
                        .description(desc).defaultCredentials(defaultCredentials).build());
            }
         }
      return images;
   }

   @Override
   public Iterable<NodeMetadata> listNodes() {
      return nodes.values();
   }

   @SuppressWarnings("unchecked")
   @Override
   public Iterable<Location> listLocations() {
      return (Iterable<Location>) locationSupplier.get();
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

   @Override
   public void resumeNode(String id) {
      NodeMetadata node = nodes.get(id);
      if (node == null)
         throw new ResourceNotFoundException("node not found: " + id);
      if (node.getState() == NodeState.RUNNING)
         return;
      if (node.getState() != NodeState.SUSPENDED)
         throw new IllegalStateException("to resume a node, it must be in suspended state, not: " + node.getState());
      StubComputeServiceDependenciesModule.setState(node, NodeState.PENDING, 0);
      StubComputeServiceDependenciesModule.setState(node, NodeState.RUNNING, 50);
   }

   @Override
   public void suspendNode(String id) {
      NodeMetadata node = nodes.get(id);
      if (node == null)
         throw new ResourceNotFoundException("node not found: " + id);
      if (node.getState() == NodeState.SUSPENDED)
         return;
      if (node.getState() != NodeState.RUNNING)
         throw new IllegalStateException("to suspend a node, it must be in running state, not: " + node.getState());
      StubComputeServiceDependenciesModule.setState(node, NodeState.PENDING, 0);
      StubComputeServiceDependenciesModule.setState(node, NodeState.SUSPENDED, 50);
   }
}