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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.domain.Credentials;
import org.jclouds.location.Provider;
import org.jclouds.predicates.SocketOpen;

import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.net.HostAndPort;
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

   // STUB STUFF STATIC SO MULTIPLE CONTEXTS CAN SEE IT
   protected static final LoadingCache<String, ConcurrentMap<String, NodeMetadata>> backing = CacheBuilder.newBuilder()
            .build(new CacheLoader<String, ConcurrentMap<String, NodeMetadata>>() {

               @Override
               public ConcurrentMap<String, NodeMetadata> load(String arg0) throws Exception {
                  return new ConcurrentHashMap<String, NodeMetadata>();
               }

            });

   @Provides
   @Singleton
   protected ConcurrentMap<String, NodeMetadata> provideNodesForIdentity(@Provider Supplier<Credentials> creds)
            throws ExecutionException {
      return backing.get(creds.get().identity);
   }

   protected static final LoadingCache<String, AtomicInteger> nodeIds = CacheBuilder.newBuilder().build(
            new CacheLoader<String, AtomicInteger>() {

               @Override
               public AtomicInteger load(String arg0) throws Exception {
                  return new AtomicInteger(0);
               }

            });

   @Provides
   @Named("NODE_ID")
   protected Integer provideNodeIdForIdentity(@Provider Supplier<Credentials> creds) throws ExecutionException {
      return nodeIds.get(creds.get().identity).incrementAndGet();
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
      public boolean apply(HostAndPort input) {
         if (input.getHostText().indexOf(publicIpPrefix) == -1)
            return false;
         String id = input.getHostText().replace(publicIpPrefix, "");
         NodeMetadata node = nodes.get(id);
         return node != null && node.getStatus() == Status.RUNNING;
      }

   }

   static Hardware stub(String type, int cores, int ram, float disk) {
      return new org.jclouds.compute.domain.HardwareBuilder().ids(type).name(type)
            .processors(ImmutableList.of(new Processor(cores, 1.0))).ram(ram)
            .volumes(ImmutableList.<Volume> of(new VolumeImpl(disk, true, false))).build();
   }

}
