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
package org.jclouds.joyent.sdc.v6_5.compute.functions;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.tryFind;
import static com.google.common.collect.Maps.filterKeys;
import static org.jclouds.compute.util.ComputeServiceUtils.addMetadataAndParseTagsFromCommaDelimitedValue;
import static org.jclouds.compute.util.ComputeServiceUtils.getSpace;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.joyent.sdc.v6_5.domain.Machine;
import org.jclouds.joyent.sdc.v6_5.domain.datacenterscoped.DatacenterAndId;
import org.jclouds.joyent.sdc.v6_5.domain.datacenterscoped.DatacenterAndName;
import org.jclouds.joyent.sdc.v6_5.domain.datacenterscoped.MachineInDatacenter;
import org.jclouds.joyent.sdc.v6_5.reference.Metadata;
import org.jclouds.logging.Logger;
import org.jclouds.util.InetAddresses2;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;

/**
 * A function for transforming a sdc-specific Machine into a generic
 * NodeMetadata object.
 * 
 * @author Adrian Cole
 */
public class MachineInDatacenterToNodeMetadata implements Function<MachineInDatacenter, NodeMetadata> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected Map<Machine.State, org.jclouds.compute.domain.NodeMetadata.Status> toPortableNodeStatus;
   protected final Supplier<Map<String, Location>> locationIndex;
   protected final Supplier<Set<? extends Image>> images;
   protected final Supplier<Set<? extends Hardware>> hardwares;
   protected final GroupNamingConvention nodeNamingConvention;

   @Inject
   public MachineInDatacenterToNodeMetadata(Map<Machine.State, NodeMetadata.Status> toPortableNodeStatus,
         Supplier<Map<String, Location>> locationIndex, @Memoized Supplier<Set<? extends Image>> images,
         @Memoized Supplier<Set<? extends Hardware>> hardwares, GroupNamingConvention.Factory namingConvention) {
      this.toPortableNodeStatus = checkNotNull(toPortableNodeStatus, "toPortableNodeStatus");
      this.nodeNamingConvention = checkNotNull(namingConvention, "namingConvention").createWithoutPrefix();
      this.locationIndex = checkNotNull(locationIndex, "locationIndex");
      this.images = checkNotNull(images, "images");
      this.hardwares = checkNotNull(hardwares, "hardwares");
   }

   @Override
   public NodeMetadata apply(MachineInDatacenter machineInDatacenter) {
      Location zone = locationIndex.get().get(machineInDatacenter.getDatacenter());
      checkState(zone != null, "location %s not in locationIndex: %s", machineInDatacenter.getDatacenter(),
            locationIndex.get());
      Machine from = machineInDatacenter.get();

      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.id(machineInDatacenter.slashEncode());
      builder.providerId(from.getId());
      builder.name(from.getName());
      builder.hostname(from.getName());
      builder.location(zone);
      addMetadataAndParseTagsFromCommaDelimitedValue(builder, filterKeys(from.getMetadata(), new Predicate<String>() {

         @Override
         public boolean apply(String input) {
            // TODO make this more efficient
            for (Metadata key : Metadata.values())
               if (key.key().equals(input))
                  return false;
            return true;
         }

      }));
      builder.group(nodeNamingConvention.groupInUniqueNameOrNull(from.getName()));
      builder.imageId(DatacenterAndName.fromDatacenterAndName(machineInDatacenter.getDatacenter(), from.get())
            .slashEncode());
      builder.operatingSystem(findOperatingSystemForMachineOrNull(machineInDatacenter));
      builder.hardware(findHardwareForMachineOrNull(machineInDatacenter));
      builder.status(toPortableNodeStatus.get(from.getState()));
      builder.publicAddresses(filter(from.getIps(), not(InetAddresses2.IsPrivateIPAddress.INSTANCE)));
      builder.privateAddresses(filter(from.getIps(), InetAddresses2.IsPrivateIPAddress.INSTANCE));
      return builder.build();
   }

   protected Hardware findHardwareForMachineOrNull(final MachineInDatacenter machineInDatacenter) {
      return tryFind(hardwares.get(), new Predicate<Hardware>() {
         @Override
         public boolean apply(Hardware input) {
            return input.getRam() == machineInDatacenter.get().getMemorySizeMb()
                  && getSpace(input) == machineInDatacenter.get().getDiskSizeGb()
                  && input.getLocation().getId().equals(machineInDatacenter.getDatacenter());
         }
      }).orNull();
   }

   protected OperatingSystem findOperatingSystemForMachineOrNull(MachineInDatacenter machineInDatacenter) {
      Image image = findObjectOfTypeForMachineOrNull(images.get(), "image", machineInDatacenter.get()
            .get(), machineInDatacenter);
      return (image != null) ? image.getOperatingSystem() : null;
   }

   public <T extends ComputeMetadata> T findObjectOfTypeForMachineOrNull(Set<? extends T> supply, String type,
         final String objectId, final DatacenterAndId machineInDatacenter) {
      return tryFind(supply, new Predicate<T>() {
         @Override
         public boolean apply(T input) {
            return input.getId().equals(
                  DatacenterAndId.fromDatacenterAndId(machineInDatacenter.getDatacenter(), objectId).slashEncode());
         }
      }).orNull();
   }

}
