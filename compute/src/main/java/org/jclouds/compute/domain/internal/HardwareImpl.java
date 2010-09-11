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

package org.jclouds.compute.domain.internal;

import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Processor;
import org.jclouds.domain.Location;
import org.jclouds.domain.ResourceMetadata;

import com.google.common.base.Predicate;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @author Adrian Cole
 */
public class HardwareImpl extends ComputeMetadataImpl implements Hardware {
   /** The serialVersionUID */
   private static final long serialVersionUID = 8994255275911717567L;
   private final List<Processor> processors = Lists.newArrayList();
   private final int ram;
   private final int disk;

   private Predicate<Image> supportsImage;

   public HardwareImpl(String providerId, String name, String id, @Nullable Location location, URI uri,
         Map<String, String> userMetadata, Iterable<? extends Processor> processors, int ram, int disk,
         Predicate<Image> supportsImage) {
      super(ComputeType.SIZE, providerId, name, id, location, uri, userMetadata);
      Iterables.addAll(this.processors, processors);
      this.ram = ram;
      this.disk = disk;
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
   public int getDisk() {
      return disk;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int compareTo(ResourceMetadata<ComputeType> that) {
      if (that instanceof Hardware) {
         Hardware thatHardware = Hardware.class.cast(that);
         return ComparisonChain.start()
               .compare(sumProcessors(this.getProcessors()), sumProcessors(thatHardware.getProcessors()))
               .compare(this.getRam(), thatHardware.getRam()).compare(this.getDisk(), thatHardware.getDisk()).result();
      } else {
         return super.compareTo(that);
      }
   }

   static double sumProcessors(List<? extends Processor> in) {
      double returnVal = 0;
      for (Processor processor : in)
         returnVal = processor.getCores() * processor.getSpeed();
      return returnVal;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return "[id=" + getId() + ", providerId=" + getProviderId() + ", name=" + getName() + ", processors="
            + processors + ", ram=" + ram + ", disk=" + disk + ", supportsImage=" + supportsImage + "]";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean supportsImage(Image image) {
      return supportsImage.apply(image);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + disk;
      result = prime * result + ((processors == null) ? 0 : processors.hashCode());
      result = prime * result + ram;
      result = prime * result + ((supportsImage == null) ? 0 : supportsImage.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      HardwareImpl other = (HardwareImpl) obj;
      if (disk != other.disk)
         return false;
      if (processors == null) {
         if (other.processors != null)
            return false;
      } else if (!processors.equals(other.processors))
         return false;
      if (ram != other.ram)
         return false;
      if (supportsImage == null) {
         if (other.supportsImage != null)
            return false;
      } else if (!supportsImage.equals(other.supportsImage))
         return false;
      return true;
   }
}
