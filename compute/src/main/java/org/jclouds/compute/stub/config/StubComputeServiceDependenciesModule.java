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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rest.ResourceNotFoundException;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * 
 * @author Adrian Cole
 */
public class StubComputeServiceDependenciesModule extends AbstractModule {

   @Override
   protected void configure() {

   }

   protected static final ConcurrentMap<String, NodeMetadata> backing = new ConcurrentHashMap<String, NodeMetadata>();

   // implementation details below
   @Provides
   @Singleton
   ConcurrentMap<String, NodeMetadata> provideNodes() {
      return backing;
   }

   // STUB STUFF STATIC SO MULTIPLE CONTEXTS CAN SEE IT
   private static final AtomicInteger nodeIds = new AtomicInteger(0);
   private static final ExecutorService service = Executors.newCachedThreadPool();

   @Provides
   @Named("NODE_ID")
   Integer provideNodeId() {
      return nodeIds.incrementAndGet();
   }

   @Singleton
   @Provides
   @Named("PUBLIC_IP_PREFIX")
   String publicIpPrefix() {
      return "144.175.1.";
   }

   @Singleton
   @Provides
   @Named("PRIVATE_IP_PREFIX")
   String privateIpPrefix() {
      return "10.1.1.";
   }

   @Singleton
   @Provides
   @Named("PASSWORD_PREFIX")
   String passwordPrefix() {
      return "password";
   }

   @Singleton
   @Provides
   SocketOpen socketOpen(StubSocketOpen in) {
      return in;
   }

   @Singleton
   public static class StubSocketOpen implements SocketOpen {
      private final ConcurrentMap<String, NodeMetadata> nodes;
      private final String publicIpPrefix;

      @Inject
      public StubSocketOpen(ConcurrentMap<String, NodeMetadata> nodes, @Named("PUBLIC_IP_PREFIX") String publicIpPrefix) {
         this.nodes = nodes;
         this.publicIpPrefix = publicIpPrefix;
      }

      @Override
      public boolean apply(IPSocket input) {
         if (input.getAddress().indexOf(publicIpPrefix) == -1)
            return false;
         String id = input.getAddress().replace(publicIpPrefix, "");
         NodeMetadata node = nodes.get(id);
         return node != null && node.getState() == NodeState.RUNNING;
      }

   }

   @Singleton
   public static class StubAddNodeWithTagStrategy implements AddNodeWithTagStrategy {
      private final Supplier<Location> location;
      private final ConcurrentMap<String, NodeMetadata> nodes;
      private final Provider<Integer> idProvider;
      private final String publicIpPrefix;
      private final String privateIpPrefix;
      private final String passwordPrefix;
      private final Map<String, Credentials> credentialStore;

      @Inject
      public StubAddNodeWithTagStrategy(ConcurrentMap<String, NodeMetadata> nodes, Supplier<Location> location,
               @Named("NODE_ID") Provider<Integer> idProvider, @Named("PUBLIC_IP_PREFIX") String publicIpPrefix,
               @Named("PRIVATE_IP_PREFIX") String privateIpPrefix, @Named("PASSWORD_PREFIX") String passwordPrefix,
               Map<String, Credentials> credentialStore) {
         this.nodes = nodes;
         this.location = location;
         this.idProvider = idProvider;
         this.publicIpPrefix = publicIpPrefix;
         this.privateIpPrefix = privateIpPrefix;
         this.passwordPrefix = passwordPrefix;
         this.credentialStore = credentialStore;
      }

      @Override
      public NodeMetadata execute(String tag, String name, Template template) {
         checkArgument(location.get().equals(template.getLocation()), "invalid location: " + template.getLocation());
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
         nodes.put(node.getId(), node);
         credentialStore.put(node.getId(), node.getCredentials());
         setState(node, NodeState.RUNNING, 100);
         return node;
      }
   }

   protected static void nodeWithState(NodeMetadata node, NodeState state) {
      backing.put(node.getId(), NodeMetadataBuilder.fromNodeMetadata(node).state(state).build());
   }

   public static void setState(final NodeMetadata node, final NodeState state, final long millis) {
      if (millis == 0l)
         nodeWithState(node, state);
      else
         service.execute(new Runnable() {

            @Override
            public void run() {
               try {
                  Thread.sleep(millis);
               } catch (InterruptedException e) {
                  Throwables.propagate(e);
               }
               nodeWithState(node, state);
            }

         });
   }

   @Singleton
   public static class StubGetNodeMetadataStrategy implements GetNodeMetadataStrategy {
      private final ConcurrentMap<String, NodeMetadata> nodes;

      @Inject
      protected StubGetNodeMetadataStrategy(ConcurrentMap<String, NodeMetadata> nodes) {
         this.nodes = nodes;
      }

      @Override
      public NodeMetadata execute(String id) {
         return nodes.get(id);
      }
   }

   @Singleton
   public static class StubListNodesStrategy implements ListNodesStrategy {
      private final ConcurrentMap<String, NodeMetadata> nodes;

      @Inject
      protected StubListNodesStrategy(ConcurrentMap<String, NodeMetadata> nodes) {
         this.nodes = nodes;
      }

      @Override
      public Iterable<? extends ComputeMetadata> list() {
         return listDetailsOnNodesMatching(NodePredicates.all());
      }

      @Override
      public Iterable<? extends NodeMetadata> listDetailsOnNodesMatching(Predicate<ComputeMetadata> filter) {
         return Iterables.filter(nodes.values(), filter);
      }
   }

   @Singleton
   public static class StubRebootNodeStrategy implements RebootNodeStrategy {
      private final ConcurrentMap<String, NodeMetadata> nodes;

      @Inject
      protected StubRebootNodeStrategy(ConcurrentMap<String, NodeMetadata> nodes) {
         this.nodes = nodes;
      }

      @Override
      public NodeMetadata execute(String id) {
         NodeMetadata node = nodes.get(id);
         if (node == null)
            throw new ResourceNotFoundException("node not found: " + id);
         setState(node, NodeState.PENDING, 0);
         setState(node, NodeState.RUNNING, 50);
         return node;
      }
   }

   @Singleton
   public static class StubDestroyNodeStrategy implements DestroyNodeStrategy {
      private final ConcurrentMap<String, NodeMetadata> nodes;
      private final ExecutorService service;

      @Inject
      protected StubDestroyNodeStrategy(ConcurrentMap<String, NodeMetadata> nodes,
               @Named(Constants.PROPERTY_USER_THREADS) ExecutorService service) {
         this.nodes = nodes;
         this.service = service;
      }

      @Override
      public NodeMetadata execute(final String id) {
         NodeMetadata node = nodes.get(id);
         if (node == null)
            return node;
         setState(node, NodeState.PENDING, 0);
         setState(node, NodeState.TERMINATED, 50);
         service.execute(new Runnable() {

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
         return node;
      }
   }

   @Singleton
   public static class StubImageSupplier implements Supplier<Set<? extends Image>> {
      private final Supplier<Location> defaultLocation;

      @Inject
      StubImageSupplier(Supplier<Location> defaultLocation) {
         this.defaultLocation = defaultLocation;
      }

      @Override
      public Set<? extends Image> get() {
         Location zone = defaultLocation.get().getParent();
         String parentId = zone.getId();
         Credentials defaultCredentials = new Credentials("root", null);
         return ImmutableSet
                  .<Image> of(new ImageBuilder().providerId("1").name(OsFamily.UBUNTU.name()).id(parentId + "/1")
                           .location(zone).operatingSystem(
                                    new OperatingSystem(OsFamily.UBUNTU, "ubuntu 32", null, "X86_32", "ubuntu 32",
                                             false)).description("stub ubuntu 32").defaultCredentials(
                                    defaultCredentials).build(), //
                           new ImageBuilder().providerId("2").name(OsFamily.UBUNTU.name()).id(parentId + "/2")
                                    .location(zone).operatingSystem(
                                             new OperatingSystem(OsFamily.UBUNTU, "ubuntu 64", null, "X86_64",
                                                      "ubuntu 64", true)).description("stub ubuntu 64")
                                    .defaultCredentials(defaultCredentials).build(), //
                           new ImageBuilder().providerId("3").name(OsFamily.CENTOS.name()).id(parentId + "/3")
                                    .location(zone).operatingSystem(
                                             new OperatingSystem(OsFamily.CENTOS, "centos 64", null, "X86_64",
                                                      "centos 64", true)).description("stub centos 64")
                                    .defaultCredentials(defaultCredentials).build() //

                  );
      }

   }

   @Provides
   @Singleton
   protected Set<? extends Location> provideLocations(@org.jclouds.rest.annotations.Provider String providerName) {
      Location provider = new LocationImpl(LocationScope.PROVIDER, providerName, providerName, null);
      Location region = new LocationImpl(LocationScope.REGION, providerName + "region", providerName + "region",
               provider);
      return ImmutableSet
               .of(new LocationImpl(LocationScope.ZONE, providerName + "zone", providerName + "zone", region));
   }

   @Singleton
   public static class StubHardwareSupplier implements Supplier<Set<? extends Hardware>> {

      static Hardware stub(String type, int cores, int ram, float disk) {
         return new org.jclouds.compute.domain.HardwareBuilder().ids(type).name(type).processors(
                  ImmutableList.of(new Processor(cores, 1.0))).ram(ram).volumes(
                  ImmutableList.<Volume> of(new VolumeImpl(disk, true, false))).build();
      }

      @Override
      public Set<? extends Hardware> get() {
         return ImmutableSet.<Hardware> of(stub("small", 1, 1740, 160), stub("medium", 4, 7680, 850), stub("large", 8,
                  15360, 1690));
      }
   }

}
