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
package org.jclouds.compute.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.getCoresAndSpeed;
import static org.jclouds.compute.util.ComputeServiceUtils.getSpace;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.domain.Location;
import org.jclouds.domain.ResourceMetadata;

import com.google.common.base.Predicate;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;

/**
 * @author Adrian Cole
 */
public class HardwareImpl extends ComputeMetadataImpl implements Hardware {

   /** The serialVersionUID */
   private static final long serialVersionUID = 8994255275911717567L;
   private final List<Processor> processors;
   private final int ram;
   private final List<Volume> volumes;
   private final Predicate<Image> supportsImage;

   public HardwareImpl(String providerId, String name, String id, @Nullable Location location, URI uri,
         Map<String, String> userMetadata, Set<String> tags, Iterable<? extends Processor> processors, int ram,
         Iterable<? extends Volume> volumes, Predicate<Image> supportsImage) {
      super(ComputeType.HARDWARE, providerId, name, id, location, uri, userMetadata, tags);
      this.processors = ImmutableList.copyOf(checkNotNull(processors, "processors"));
      this.ram = ram;
      this.volumes = ImmutableList.copyOf(checkNotNull(volumes, "volumes"));
      this.supportsImage = supportsImage;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public List<? extends Processor> getProcessors() {
      return processors;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int getRam() {
      return ram;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public List<? extends Volume> getVolumes() {
      return volumes;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int compareTo(ResourceMetadata<ComputeType> that) {
      if (that instanceof Hardware) {
         Hardware thatHardware = Hardware.class.cast(that);
         return ComparisonChain.start().compare(getCoresAndSpeed(this), getCoresAndSpeed(thatHardware))
               .compare(this.getRam(), thatHardware.getRam()).compare(getSpace(this), getSpace(thatHardware)).result();
      } else {
         return super.compareTo(that);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return "[id=" + getId() + ", providerId=" + getProviderId() + ", name=" + getName() + ", processors="
            + processors + ", ram=" + ram + ", volumes=" + volumes + ", supportsImage=" + supportsImage + ", tags=" + tags + "]";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Predicate<Image> supportsImage() {
      return supportsImage;
   }

}
