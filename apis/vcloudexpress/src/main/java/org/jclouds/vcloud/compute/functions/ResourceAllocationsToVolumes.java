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

package org.jclouds.vcloud.compute.functions;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.vcloud.predicates.VCloudPredicates.resourceType;

import javax.inject.Singleton;

import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.vcloud.domain.ovf.ResourceAllocation;
import org.jclouds.vcloud.domain.ovf.ResourceType;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class ResourceAllocationsToVolumes implements Function<Iterable<? extends ResourceAllocation>, Iterable<Volume>> {
   @Override
   public Iterable<Volume> apply(Iterable<? extends ResourceAllocation> resourceAllocations) {
      Iterable<Volume> volumes = transform(filter(resourceAllocations, resourceType(ResourceType.DISK_DRIVE)),
               new Function<ResourceAllocation, Volume>() {

                  @Override
                  public Volume apply(ResourceAllocation from) {
                     return new VolumeImpl(from.getAddressOnParent() + "", Volume.Type.LOCAL,
                              from.getVirtualQuantity() / 1024 / 1024f, null, from.getAddressOnParent() == 0, true);

                  }

               });
      return volumes;
   }
}