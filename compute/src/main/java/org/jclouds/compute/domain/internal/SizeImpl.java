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
import java.util.Map;

import javax.annotation.Nullable;

import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Size;
import org.jclouds.domain.Location;
import org.jclouds.domain.ResourceMetadata;

import com.google.common.base.Predicate;
import com.google.common.collect.ComparisonChain;

/**
 * @author Adrian Cole
 */
public class SizeImpl extends ComputeMetadataImpl implements Size {
   /** The serialVersionUID */
   private static final long serialVersionUID = 8994255275911717567L;
   private final double cores;
   private final int ram;
   private final int disk;

   private Predicate<Image> supportsImage;

   public SizeImpl(String providerId, String name, String id, @Nullable Location location, URI uri,
            Map<String, String> userMetadata, double cores, int ram, int disk,
            Predicate<Image> supportsImage) {
      super(ComputeType.SIZE, providerId, name, id, location, uri, userMetadata);
      this.cores = cores;
      this.ram = ram;
      this.disk = disk;
      this.supportsImage = supportsImage;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public double getCores() {
      return cores;
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
      if (that instanceof Size) {
         Size thatSize = Size.class.cast(that);
         return ComparisonChain.start().compare(this.getCores(), thatSize.getCores()).compare(
                  this.getRam(), thatSize.getRam()).compare(this.getDisk(), thatSize.getDisk())
                  .result();
      } else {
         return super.compareTo(that);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return "[id=" + getId() + ", providerId=" + getProviderId() + ", name=" + getName()
               + ", cores=" + cores + ", ram=" + ram + ", disk=" + disk + ", supportsImage="
               + supportsImage + "]";
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
      long temp;
      temp = Double.doubleToLongBits(cores);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      result = prime * result + disk;
      result = prime * result + ram;
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
      SizeImpl other = (SizeImpl) obj;
      if (Double.doubleToLongBits(cores) != Double.doubleToLongBits(other.cores))
         return false;
      if (disk != other.disk)
         return false;
      if (ram != other.ram)
         return false;
      return true;
   }
}
