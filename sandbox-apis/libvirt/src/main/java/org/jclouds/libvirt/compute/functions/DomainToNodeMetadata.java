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

package org.jclouds.libvirt.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.parseGroupFromName;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.FindResourceInSet;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OperatingSystemBuilder;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.libvirt.Domain;
import org.libvirt.DomainInfo;
import org.libvirt.LibvirtException;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
@Singleton
public class DomainToNodeMetadata implements Function<Domain, NodeMetadata> {

   public static final Map<DomainInfo.DomainState, NodeState> domainStateToNodeState = ImmutableMap
            .<DomainInfo.DomainState, NodeState> builder().put(DomainInfo.DomainState.VIR_DOMAIN_RUNNING,
                     NodeState.RUNNING)//
            .put(DomainInfo.DomainState.VIR_DOMAIN_BLOCKED, NodeState.PENDING)//
            .put(DomainInfo.DomainState.VIR_DOMAIN_PAUSED, NodeState.SUSPENDED)//
            .put(DomainInfo.DomainState.VIR_DOMAIN_SHUTDOWN, NodeState.SUSPENDED)//
            .put(DomainInfo.DomainState.VIR_DOMAIN_SHUTOFF, NodeState.SUSPENDED)//
            .put(DomainInfo.DomainState.VIR_DOMAIN_CRASHED, NodeState.ERROR)//

            .put(DomainInfo.DomainState.VIR_DOMAIN_NOSTATE, NodeState.UNRECOGNIZED)//
            .build();

   private final Function<Domain, Hardware> findHardwareForDomain;
   private final FindLocationForDomain findLocationForDomain;
   private final FindImageForDomain findImageForDomain;
   private final Map<String, Credentials> credentialStore;

   @Inject
   DomainToNodeMetadata(Map<String, Credentials> credentialStore, Function<Domain, Hardware> findHardwareForDomain,
            FindLocationForDomain findLocationForDomain, FindImageForDomain findImageForDomain) {
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.findHardwareForDomain = checkNotNull(findHardwareForDomain, "findHardwareForDomain");
      this.findLocationForDomain = checkNotNull(findLocationForDomain, "findLocationForDomain");
      this.findImageForDomain = checkNotNull(findImageForDomain, "findImageForDomain");
   }

   @Override
   public NodeMetadata apply(Domain from) {

      // convert the result object to a jclouds NodeMetadata
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      try {
         builder.id(from.getUUIDString());
         builder.providerId(from.getID() + "");
         builder.name(from.getName());
         builder.location(findLocationForDomain.apply(from));
         builder.group(parseGroupFromName(from.getName()));

         builder.operatingSystem(new OperatingSystemBuilder().description(from.getOSType()).build());
         builder.hardware(findHardwareForDomain.apply(from));

         builder.state(domainStateToNodeState.get(from.getInfo().state));
         // builder.publicAddresses(ImmutableSet.<String> of(from.publicAddress));
         // builder.privateAddresses(ImmutableSet.<String> of(from.privateAddress));
         builder.credentials(credentialStore.get("node#" + from.getUUIDString()));

      } catch (LibvirtException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return builder.build();
   }

   @Singleton
   public static class FindImageForDomain extends FindResourceInSet<Domain, Image> {

      @Inject
      public FindImageForDomain(@Memoized Supplier<Set<? extends Image>> hardware) {
         super(hardware);
      }

      @Override
      public boolean matches(Domain from, Image input) {
         // TODO
         // return input.getProviderId().equals(from.imageId + "");
         return true;
      }
   }

   @Singleton
   public static class FindLocationForDomain extends FindResourceInSet<Domain, Location> {

      @Inject
      public FindLocationForDomain(@Memoized Supplier<Set<? extends Location>> hardware) {
         super(hardware);
      }

      @Override
      public boolean matches(Domain from, Location input) {
         // TODO
         // return input.getId().equals(from.datacenter + "");
         return true;
      }
   }
}
