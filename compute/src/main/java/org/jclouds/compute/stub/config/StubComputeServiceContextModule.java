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
import static org.jclouds.compute.predicates.ImagePredicates.any;

import java.net.URI;
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
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.LoadBalancerService;
import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.config.ComputeServiceTimeoutsModule;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.internal.ImageImpl;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.concurrent.SingleThreaded;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rest.ResourceNotFoundException;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Providers;

/**
 * 
 * @author Adrian Cole
 */
@SingleThreaded
public class StubComputeServiceContextModule extends BaseComputeServiceContextModule {
   // STUB STUFF STATIC SO MULTIPLE CONTEXTS CAN SEE IT
   private static final AtomicInteger nodeIds = new AtomicInteger(0);
   private static final ConcurrentMap<Integer, StubNodeMetadata> nodes = new ConcurrentHashMap<Integer, StubNodeMetadata>();

   @Provides
   @Singleton
   ConcurrentMap<Integer, StubNodeMetadata> provideNodes() {
      return nodes;
   }

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
      private final ConcurrentMap<Integer, StubNodeMetadata> nodes;
      private final String publicIpPrefix;

      @Inject
      public StubSocketOpen(ConcurrentMap<Integer, StubNodeMetadata> nodes,
            @Named("PUBLIC_IP_PREFIX") String publicIpPrefix) {
         this.nodes = nodes;
         this.publicIpPrefix = publicIpPrefix;
      }

      @Override
      public boolean apply(IPSocket input) {
         if (input.getAddress().indexOf(publicIpPrefix) == -1)
            return false;
         String id = input.getAddress().replace(publicIpPrefix, "");
         int intId = Integer.parseInt(id);
         StubNodeMetadata node = nodes.get(intId);
         return node != null && node.getState() == NodeState.RUNNING;
      }

   }

   @SuppressWarnings({ "rawtypes" })
   @Override
   protected void configure() {
      bind(new TypeLiteral<ComputeServiceContext>() {
      }).to(new TypeLiteral<ComputeServiceContextImpl<ConcurrentMap, ConcurrentMap>>() {
      }).in(Scopes.SINGLETON);
      install(new ComputeServiceTimeoutsModule());
      bind(ConcurrentMap.class).toInstance(nodes);
      bind(AddNodeWithTagStrategy.class).to(StubAddNodeWithTagStrategy.class);
      bind(ListNodesStrategy.class).to(StubListNodesStrategy.class);
      bind(GetNodeMetadataStrategy.class).to(StubGetNodeMetadataStrategy.class);
      bind(RebootNodeStrategy.class).to(StubRebootNodeStrategy.class);
      bind(DestroyNodeStrategy.class).to(StubDestroyNodeStrategy.class);
      bind(LoadBalancerService.class).toProvider(Providers.<LoadBalancerService> of(null));
   }

   public static class StubNodeMetadata extends NodeMetadataImpl {

      /** The serialVersionUID */
      private static final long serialVersionUID = 5538798859671465494L;
      private NodeState state;
      private final ExecutorService service;

      public StubNodeMetadata(String providerId, String name, String id, Location location, URI uri,
            Map<String, String> userMetadata, String tag, String imageId, Image image, OperatingSystem os,
            NodeState state, Iterable<String> publicAddresses, Iterable<String> privateAddresses,
            Map<String, String> extra, Credentials credentials, ExecutorService service) {
         super(providerId, name, id, location, uri, userMetadata, tag, imageId, os, state, publicAddresses,
               privateAddresses, extra, credentials);
         this.setState(state, 0);
         this.service = service;
      }

      public void setState(final NodeState state, final long millis) {
         if (millis == 0l)
            this.state = state;
         else
            service.execute(new Runnable() {

               @Override
               public void run() {
                  try {
                     Thread.sleep(millis);
                  } catch (InterruptedException e) {
                     Throwables.propagate(e);
                  }
                  StubNodeMetadata.this.state = state;
               }

            });
      }

      @Override
      public NodeState getState() {
         return state;
      }

   }

   @Singleton
   public static class StubAddNodeWithTagStrategy implements AddNodeWithTagStrategy {
      private final Supplier<Location> location;
      private final ExecutorService service;
      private final ConcurrentMap<Integer, StubNodeMetadata> nodes;
      private final Provider<Integer> idProvider;
      private final String publicIpPrefix;
      private final String privateIpPrefix;
      private final String passwordPrefix;

      @Inject
      public StubAddNodeWithTagStrategy(ConcurrentMap<Integer, StubNodeMetadata> nodes, Supplier<Location> location,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService service,
            @Named("NODE_ID") Provider<Integer> idProvider, @Named("PUBLIC_IP_PREFIX") String publicIpPrefix,
            @Named("PRIVATE_IP_PREFIX") String privateIpPrefix, @Named("PASSWORD_PREFIX") String passwordPrefix) {
         this.nodes = nodes;
         this.location = location;
         this.service = Executors.newCachedThreadPool();
         this.idProvider = idProvider;
         this.publicIpPrefix = publicIpPrefix;
         this.privateIpPrefix = privateIpPrefix;
         this.passwordPrefix = passwordPrefix;
      }

      @Override
      public NodeMetadata execute(String tag, String name, Template template) {
         checkArgument(location.get().equals(template.getLocation()), "invalid location: " + template.getLocation());
         int id = idProvider.get();
         StubNodeMetadata node = new StubNodeMetadata(id + "", name, id + "", location.get(), null,
               ImmutableMap.<String, String> of(), tag, template.getImage().getId(), template.getImage(), template
                     .getImage().getOperatingSystem(), NodeState.PENDING,
               ImmutableSet.<String> of(publicIpPrefix + id), ImmutableSet.<String> of(privateIpPrefix + id),
               ImmutableMap.<String, String> of(), new Credentials("root", passwordPrefix + id), service);
         nodes.put(id, node);
         node.setState(NodeState.RUNNING, 100);
         return node;
      }

   }

   @Singleton
   public static class StubGetNodeMetadataStrategy implements GetNodeMetadataStrategy {
      private final ConcurrentMap<Integer, StubNodeMetadata> nodes;

      @Inject
      protected StubGetNodeMetadataStrategy(ConcurrentMap<Integer, StubNodeMetadata> nodes) {
         this.nodes = nodes;
      }

      @Override
      public NodeMetadata execute(String id) {
         return nodes.get(Integer.parseInt(id));
      }
   }

   @Singleton
   public static class StubListNodesStrategy implements ListNodesStrategy {
      private final ConcurrentMap<Integer, StubNodeMetadata> nodes;

      @Inject
      protected StubListNodesStrategy(ConcurrentMap<Integer, StubNodeMetadata> nodes) {
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
      private final ConcurrentMap<Integer, StubNodeMetadata> nodes;

      @Inject
      protected StubRebootNodeStrategy(ConcurrentMap<Integer, StubNodeMetadata> nodes) {
         this.nodes = nodes;
      }

      @Override
      public StubNodeMetadata execute(String id) {
         StubNodeMetadata node = nodes.get(Integer.parseInt(id));
         if (node == null)
            throw new ResourceNotFoundException("node not found: " + id);
         node.setState(NodeState.PENDING, 0);
         node.setState(NodeState.RUNNING, 50);
         return node;
      }
   }

   @Singleton
   public static class StubDestroyNodeStrategy implements DestroyNodeStrategy {
      private final ConcurrentMap<Integer, StubNodeMetadata> nodes;
      private final ExecutorService service;

      @Inject
      protected StubDestroyNodeStrategy(ConcurrentMap<Integer, StubNodeMetadata> nodes,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService service) {
         this.nodes = nodes;
         this.service = service;
      }

      @Override
      public StubNodeMetadata execute(String id) {
         final int nodeId = Integer.parseInt(id);
         StubNodeMetadata node = nodes.get(nodeId);
         if (node == null)
            return node;
         node.setState(NodeState.PENDING, 0);
         node.setState(NodeState.TERMINATED, 50);
         service.execute(new Runnable() {

            @Override
            public void run() {
               try {
                  Thread.sleep(200);
               } catch (InterruptedException e) {
                  Throwables.propagate(e);
               } finally {
                  nodes.remove(nodeId);
               }
            }

         });
         return node;
      }
   }

   @Override
   protected Supplier<Set<? extends Image>> getSourceImageSupplier(Injector injector) {
      Supplier<Location> defaultLocation = injector.getInstance(Key.get(new TypeLiteral<Supplier<Location>>() {
      }));
      Location zone = defaultLocation.get().getParent();
      String parentId = zone.getId();
      return Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet.<Image> of(//
            new ImageImpl("1", OsFamily.UBUNTU.name(), parentId + "/1", zone, null,
                  ImmutableMap.<String, String> of(), //
                  new OperatingSystem(OsFamily.UBUNTU, "ubuntu 32", null, "X86_32", "ubuntu 32", false),
                  "stub ubuntu 32", "", new Credentials("root", null)), //
            new ImageImpl("2", OsFamily.UBUNTU.name(), parentId + "/2", zone, null,
                  ImmutableMap.<String, String> of(),//
                  new OperatingSystem(OsFamily.UBUNTU, "ubuntu 64", null, "X86_64", "ubuntu 64", true),
                  "stub ubuntu 64", "", new Credentials("root", null)),//
            new ImageImpl("3", OsFamily.CENTOS.name(), parentId + "/3", zone, null,
                  ImmutableMap.<String, String> of(), //
                  new OperatingSystem(OsFamily.CENTOS, "centos 64", null, "X86_64", "centos 64", true),
                  "stub centos 64", "", new Credentials("root", null))));
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

   @Override
   protected Supplier<Set<? extends Hardware>> getSourceSizeSupplier(Injector injector) {
      return Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet.<Hardware> of(new StubHardware("small", 1,
            1740, 160), new StubHardware("medium", 4, 7680, 850), new StubHardware("large", 8, 15360, 1690)));
   }

   private static class StubHardware extends org.jclouds.compute.domain.internal.HardwareImpl {
      /** The serialVersionUID */
      private static final long serialVersionUID = -1842135761654973637L;

      StubHardware(String type, int cores, int ram, int disk) {
         super(type, type, type, null, null, ImmutableMap.<String, String> of(), ImmutableList.of(new Processor(cores,
               1.0)), ram, disk, any());
      }
   }

}
