/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import java.util.Set;

import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.Size;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class SizeImpl implements Size {
   private String id;
   private final int cores;
   private final int ram;
   private final int disk;
   private final Set<Architecture> supportedArchitectures = Sets.newHashSet();

   public SizeImpl(String id, int cores, int ram, int disk,
            Iterable<Architecture> supportedArchitectures) {
      this.id = id;
      this.cores = cores;
      this.ram = ram;
      this.disk = disk;
      Iterables.addAll(this.supportedArchitectures, supportedArchitectures);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int getCores() {
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

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + cores;
      result = prime * result + disk;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ram;
      result = prime * result
               + ((supportedArchitectures == null) ? 0 : supportedArchitectures.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      SizeImpl other = (SizeImpl) obj;
      if (cores != other.cores)
         return false;
      if (disk != other.disk)
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (ram != other.ram)
         return false;
      if (supportedArchitectures == null) {
         if (other.supportedArchitectures != null)
            return false;
      } else if (!supportedArchitectures.equals(other.supportedArchitectures))
         return false;
      return true;
   }

   public int compareTo(Size that) {
      return ComparisonChain.start().compare(this.getCores(), that.getCores()).compare(
               this.getRam(), that.getRam()).compare(this.getDisk(), that.getDisk()).result();
   }

   @Override
   public String toString() {
      return "[id=" + id + ", cores=" + cores + ", ram=" + ram + ", disk=" + disk
               + ", supportedArchitectures=" + supportedArchitectures + "]";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean supportsArchitecture(Architecture architecture) {
      return supportedArchitectures.contains(architecture);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getId() {
      return id;
   }
}
