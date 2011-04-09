/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.SocketOpen;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
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
   static final ExecutorService service = Executors.newCachedThreadPool();

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

   static Hardware stub(String type, int cores, int ram, float disk) {
      return new org.jclouds.compute.domain.HardwareBuilder().ids(type).name(type)
            .processors(ImmutableList.of(new Processor(cores, 1.0))).ram(ram)
            .volumes(ImmutableList.<Volume> of(new VolumeImpl(disk, true, false))).build();
   }

}
