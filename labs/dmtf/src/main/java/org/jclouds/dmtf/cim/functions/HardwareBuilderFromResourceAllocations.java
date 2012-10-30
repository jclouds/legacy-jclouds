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
package org.jclouds.dmtf.cim.functions;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.transform;

import javax.inject.Singleton;

import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.dmtf.CIMPredicates;
import org.jclouds.dmtf.cim.ResourceAllocationSettingData;
import org.jclouds.dmtf.cim.ResourceAllocationSettingData.ResourceType;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class HardwareBuilderFromResourceAllocations implements
         Function<Iterable<? extends ResourceAllocationSettingData>, HardwareBuilder> {
   @Override
   public HardwareBuilder apply(Iterable<? extends ResourceAllocationSettingData> from) {
      HardwareBuilder builder = new HardwareBuilder();
      builder.volumes(transform(filter(from, CIMPredicates.resourceTypeIn(ResourceType.DISK_DRIVE,
               ResourceType.BASE_PARTITIONABLE_UNIT, ResourceType.PARTITIONABLE_UNIT)),
               new Function<ResourceAllocationSettingData, Volume>() {

                  @Override
                  public Volume apply(ResourceAllocationSettingData from) {
                     return HardwareBuilderFromResourceAllocations.this.apply(from);
                  }

               }));

      builder.ram((int) find(from, CIMPredicates.resourceTypeIn(ResourceType.MEMORY)).getVirtualQuantity().longValue());

      builder.processors(transform(filter(from, CIMPredicates.resourceTypeIn(ResourceType.PROCESSOR)),
               new Function<ResourceAllocationSettingData, Processor>() {

                  @Override
                  public Processor apply(ResourceAllocationSettingData arg0) {
                     return new Processor(arg0.getVirtualQuantity().longValue(), 1);
                  }
               }));
      return builder;
   }

   public Volume apply(ResourceAllocationSettingData from) {
      return new VolumeImpl(from.getAddressOnParent() + "", Volume.Type.LOCAL, from.getVirtualQuantity() == null ? null
               : from.getVirtualQuantity().longValue() / 1024 / 1024f, null, "0".equals(from.getAddressOnParent())
               || ResourceType.BASE_PARTITIONABLE_UNIT.equals(from.getResourceType()), true);
   }
}
