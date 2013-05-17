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
package org.jclouds.openstack.nova.v2_0.compute.functions;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.FloatingIP;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneAndId;
import org.jclouds.openstack.nova.v2_0.extensions.FloatingIPApi;
import org.jclouds.rest.InsufficientResourcesException;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * A function for adding and allocating an ip to a node
 * 
 * @author Adrian Cole
 */
public class AllocateAndAddFloatingIpToNode implements
         Function<AtomicReference<NodeMetadata>, AtomicReference<NodeMetadata>> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Predicate<AtomicReference<NodeMetadata>> nodeRunning;
   private final NovaApi novaApi;
   private final LoadingCache<ZoneAndId, Iterable<? extends FloatingIP>> floatingIpCache;

   @Inject
   public AllocateAndAddFloatingIpToNode(@Named(TIMEOUT_NODE_RUNNING) Predicate<AtomicReference<NodeMetadata>> nodeRunning,
            NovaApi novaApi, @Named("FLOATINGIP") LoadingCache<ZoneAndId, Iterable<? extends FloatingIP>> floatingIpCache) {
      this.nodeRunning = checkNotNull(nodeRunning, "nodeRunning");
      this.novaApi = checkNotNull(novaApi, "novaApi");
      this.floatingIpCache = checkNotNull(floatingIpCache, "floatingIpCache");
   }

   @Override
   public AtomicReference<NodeMetadata> apply(AtomicReference<NodeMetadata> input) {
      checkState(nodeRunning.apply(input), "node never achieved state running %s", input.get());
      NodeMetadata node = input.get();
      // node's location is a host
      String zoneId = node.getLocation().getParent().getId();
      FloatingIPApi floatingIpApi = novaApi.getFloatingIPExtensionForZone(zoneId).get();

      FloatingIP ip = null;
      try {
         logger.debug(">> allocating or reassigning floating ip for node(%s)", node.getId());
         ip = floatingIpApi.create();
      } catch (InsufficientResourcesException e) {
         logger.trace("<< [%s] allocating a new floating ip for node(%s)", e.getMessage(), node.getId());
         logger.trace(">> searching for existing, unassigned floating ip for node(%s)", node.getId());
         ArrayList<FloatingIP> unassignedIps = Lists.newArrayList(Iterables.filter(floatingIpApi.list(),
                  new Predicate<FloatingIP>() {

                     @Override
                     public boolean apply(FloatingIP arg0) {
                        return arg0.getFixedIp() == null;
                     }

                  }));
         // try to prevent multiple parallel launches from choosing the same ip.
         Collections.shuffle(unassignedIps);
         ip = Iterables.getLast(unassignedIps);
      }
      logger.debug(">> adding floatingIp(%s) to node(%s)", ip.getIp(), node.getId());

      floatingIpApi.addToServer(ip.getIp(), node.getProviderId());
      input.set(NodeMetadataBuilder.fromNodeMetadata(node).publicAddresses(ImmutableSet.of(ip.getIp())).build());
      floatingIpCache.invalidate(ZoneAndId.fromSlashEncoded(node.getId()));
      return input;
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("AllocateAndAddFloatingIpToNode").toString();
   }
}
