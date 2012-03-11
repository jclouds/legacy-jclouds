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
package org.jclouds.glesys.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.parseGroupFromName;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.collect.FindResourceInSet;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.glesys.domain.Ip;
import org.jclouds.glesys.domain.ServerDetails;
import org.jclouds.logging.Logger;
import org.jclouds.util.InetAddresses2.IsPrivateIPAddress;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class ServerDetailsToNodeMetadata implements Function<ServerDetails, NodeMetadata> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   public static final Map<ServerDetails.State, NodeState> serverStateToNodeState = ImmutableMap
         .<ServerDetails.State, NodeState> builder().put(ServerDetails.State.STOPPED, NodeState.SUSPENDED)
         .put(ServerDetails.State.LOCKED, NodeState.PENDING)
         .put(ServerDetails.State.RUNNING, NodeState.RUNNING)
         .put(ServerDetails.State.UNRECOGNIZED, NodeState.UNRECOGNIZED).build();

   protected final Supplier<Set<? extends Image>> images;
   protected final FindLocationForServerDetails findLocationForServerDetails;

   private static class FindImageForServer implements Predicate<Image> {
      private final ServerDetails instance;

      private FindImageForServer(ServerDetails instance) {
         this.instance = instance;
      }

      @Override
      public boolean apply(Image input) {
         return input.getProviderId().equals(instance.getTemplateName());
      }
   }

   @Inject
   ServerDetailsToNodeMetadata(FindLocationForServerDetails findLocationForServerDetails,
         @Memoized Supplier<Set<? extends Image>> images) {
      this.findLocationForServerDetails = checkNotNull(findLocationForServerDetails, "findLocationForServerDetails");
      this.images = checkNotNull(images, "images");
   }

   @Override
   public NodeMetadata apply(ServerDetails from) {
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.ids(from.getId() + "");
      builder.name(from.getHostname());
      builder.hostname(from.getHostname());
      Location location = findLocationForServerDetails.apply(from);
      assert (location != null) : String.format("no location matched ServerDetails %s", from);
      builder.group(parseGroupFromName(from.getDescription()));
      builder.imageId(from.getTemplateName() + "");
      builder.operatingSystem(parseOperatingSystem(from));
      builder.hardware(new HardwareBuilder().ids(from.getId() + "").ram(from.getMemorySizeMB())
            .processors(ImmutableList.of(new Processor(from.getCpuCores(), 1.0)))
            .volumes(ImmutableList.<Volume> of(new VolumeImpl((float) from.getDiskSizeGB(), true, true)))
            .hypervisor(from.getPlatform()).build());
      builder.state(serverStateToNodeState.get(from.getState()));
      Iterable<String> addresses = Iterables.filter(Iterables.transform(from.getIps(), new Function<Ip, String>() {

         @Override
         public String apply(Ip arg0) {
            return Strings.emptyToNull(arg0.getIp());
         }

      }), Predicates.notNull());
      builder.publicAddresses(Iterables.filter(addresses, Predicates.not(IsPrivateIPAddress.INSTANCE)));
      builder.privateAddresses(Iterables.filter(addresses, IsPrivateIPAddress.INSTANCE));
      return builder.build();
   }

   protected OperatingSystem parseOperatingSystem(ServerDetails from) {
      try {
         return Iterables.find(images.get(), new FindImageForServer(from)).getOperatingSystem();
      } catch (NoSuchElementException e) {
         logger.debug("could not find a matching image for server %s", from);
      }
      return null;
   }

   @Singleton
   public static class FindLocationForServerDetails extends FindResourceInSet<ServerDetails, Location> {

      @Inject
      public FindLocationForServerDetails(@Memoized Supplier<Set<? extends Location>> location) {
         super(location);
      }

      @Override
      public boolean matches(ServerDetails from, Location input) {
         return input.getId().equals(from.getDatacenter());
      }
   }
}
