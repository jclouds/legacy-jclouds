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
package org.jclouds.compute.stub.config;

import static com.google.common.base.Predicates.in;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Maps.filterKeys;
import static org.jclouds.compute.util.ComputeServiceUtils.formatStatus;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.JCloudsNativeComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.location.suppliers.all.JustProvider;
import org.jclouds.rest.ResourceNotFoundException;

import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class StubComputeServiceAdapter implements JCloudsNativeComputeServiceAdapter {
   private final Supplier<Location> location;
   private final ConcurrentMap<String, NodeMetadata> nodes;
   private final ListeningExecutorService ioExecutor;
   private final Provider<Integer> idProvider;
   private final String publicIpPrefix;
   private final String privateIpPrefix;
   private final String passwordPrefix;
   private final Supplier<Set<? extends Location>> locationSupplier;
   private final Map<OsFamily, Map<String, String>> osToVersionMap;

   @Inject
   public StubComputeServiceAdapter(ConcurrentMap<String, NodeMetadata> nodes,
            @Named(Constants.PROPERTY_IO_WORKER_THREADS) ListeningExecutorService ioExecutor, Supplier<Location> location,
            @Named("NODE_ID") Provider<Integer> idProvider, @Named("PUBLIC_IP_PREFIX") String publicIpPrefix,
            @Named("PRIVATE_IP_PREFIX") String privateIpPrefix, @Named("PASSWORD_PREFIX") String passwordPrefix,
            JustProvider locationSupplier, Map<OsFamily, Map<String, String>> osToVersionMap) {
      this.nodes = nodes;
      this.ioExecutor = ioExecutor;
      this.location = location;
      this.idProvider = idProvider;
      this.publicIpPrefix = publicIpPrefix;
      this.privateIpPrefix = privateIpPrefix;
      this.passwordPrefix = passwordPrefix;
      this.locationSupplier = locationSupplier;
      this.osToVersionMap = osToVersionMap;
   }

   protected void setStateOnNode(Status status, NodeMetadata node) {
      nodes.put(node.getId(), NodeMetadataBuilder.fromNodeMetadata(node).status(status).build());
   }

   protected void setStateOnNodeAfterDelay(final Status status, final NodeMetadata node, final long millis) {
      if (millis == 0l)
         setStateOnNode(status, node);
      else
         ioExecutor.execute(new Runnable() {

            @Override
            public void run() {
               try {
                  Thread.sleep(millis);
               } catch (InterruptedException e) {
                  Throwables.propagate(e);
               }
               setStateOnNode(status, node);
            }

         });
   }
   @Override
   public NodeWithInitialCredentials createNodeWithGroupEncodedIntoName(String group, String name, Template template) {
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      String id = idProvider.get() + "";
      builder.ids(id);
      builder.name(name);
      // using a predictable name so tests will pass
      builder.hostname(group);
      builder.tags(template.getOptions().getTags());
      builder.userMetadata(template.getOptions().getUserMetadata());
      builder.group(group);
      builder.location(location.get());
      builder.imageId(template.getImage().getId());
      builder.operatingSystem(template.getImage().getOperatingSystem());
      builder.status(Status.PENDING);
      builder.publicAddresses(ImmutableSet.<String> of(publicIpPrefix + id));
      builder.privateAddresses(ImmutableSet.<String> of(privateIpPrefix + id));
      builder.credentials(LoginCredentials.builder().user("root").password(passwordPrefix + id).build());
      NodeMetadata node = builder.build();
      nodes.put(node.getId(), node);
      setStateOnNodeAfterDelay(Status.RUNNING, node, 100);
      return new NodeWithInitialCredentials(node);
   }

   @Override
   public Iterable<Hardware> listHardwareProfiles() {
      return ImmutableSet.<Hardware> of(StubComputeServiceDependenciesModule.stub("small", 1, 1740, 160),
            StubComputeServiceDependenciesModule.stub("medium", 4, 7680, 850),
            StubComputeServiceDependenciesModule.stub("large", 8, 15360, 1690));
   }

   @Override
   public Iterable<Image> listImages() {
      // initializing as a List, as ImmutableSet does not allow you to put
      // duplicates
      Builder<Image> images = ImmutableList.builder();
      int id = 1;
      for (boolean is64Bit : new boolean[] { true, false })
         for (Entry<OsFamily, Map<String, String>> osVersions : this.osToVersionMap.entrySet()) {
            for (String version : ImmutableSet.copyOf(osVersions.getValue().values())) {
               String desc = String.format("stub %s %s", osVersions.getKey(), is64Bit);
               images.add(new ImageBuilder().ids(id++ + "").name(osVersions.getKey().name()).location(location.get())
                     .operatingSystem(new OperatingSystem(osVersions.getKey(), desc, version, null, desc, is64Bit))
                     .description(desc).status(Image.Status.AVAILABLE).build());
            }
         }
      return images.build();
   }
   
   @Override
   public Image getImage(String id) {
      return find(listImages(), ImagePredicates.idEquals(id), null);
   }
   
   @Override
   public Iterable<NodeMetadata> listNodes() {
      return nodes.values();
   }

   @Override
   public Iterable<NodeMetadata> listNodesByIds(Iterable<String> ids) {
      return filterKeys(nodes, in(ImmutableSet.copyOf(ids))).values();
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
      setStateOnNodeAfterDelay(Status.PENDING, node, 0);
      setStateOnNodeAfterDelay(Status.TERMINATED, node, 50);
      ioExecutor.execute(new Runnable() {

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
      setStateOnNode(Status.PENDING, node);
      setStateOnNodeAfterDelay(Status.RUNNING, node, 50);
   }

   @Override
   public void resumeNode(String id) {
      NodeMetadata node = nodes.get(id);
      if (node == null)
         throw new ResourceNotFoundException("node not found: " + id);
      if (node.getStatus() == Status.RUNNING)
         return;
      if (node.getStatus() != Status.SUSPENDED)
         throw new IllegalStateException("to resume a node, it must be in suspended status, not: " + formatStatus(node));
      setStateOnNode(Status.PENDING, node);
      setStateOnNodeAfterDelay(Status.RUNNING, node, 50);
   }

   @Override
   public void suspendNode(String id) {
      NodeMetadata node = nodes.get(id);
      if (node == null)
         throw new ResourceNotFoundException("node not found: " + id);
      if (node.getStatus() == Status.SUSPENDED)
         return;
      if (node.getStatus() != Status.RUNNING)
         throw new IllegalStateException("to suspend a node, it must be in running status, not: " + formatStatus(node));
      setStateOnNode(Status.PENDING, node);
      setStateOnNodeAfterDelay(Status.SUSPENDED, node, 50);
   }

}
