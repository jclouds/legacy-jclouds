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

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class SizeImpl implements Size {

   private final Integer cores;
   private final Integer ram;
   private final Integer disk;
   private final Set<Architecture> supportedArchitectures = Sets.newHashSet();

   public SizeImpl(Integer cores, Integer ram, Integer disk,
            Iterable<Architecture> supportedArchitectures) {
      this.cores = cores;
      this.ram = ram;
      this.disk = disk;
      Iterables.addAll(this.supportedArchitectures, supportedArchitectures);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Integer getCores() {
      return cores;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Integer getRam() {
      return ram;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Integer getDisk() {
      return disk;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((cores == null) ? 0 : cores.hashCode());
      result = prime * result + ((disk == null) ? 0 : disk.hashCode());
      result = prime * result + ((ram == null) ? 0 : ram.hashCode());
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
      if (cores == null) {
         if (other.cores != null)
            return false;
      } else if (!cores.equals(other.cores))
         return false;
      if (disk == null) {
         if (other.disk != null)
            return false;
      } else if (!disk.equals(other.disk))
         return false;
      if (ram == null) {
         if (other.ram != null)
            return false;
      } else if (!ram.equals(other.ram))
         return false;
      if (supportedArchitectures == null) {
         if (other.supportedArchitectures != null)
            return false;
      } else if (!supportedArchitectures.equals(other.supportedArchitectures))
         return false;
      return true;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return "SizeImpl [cores=" + cores + ", disk=" + disk + ", ram=" + ram + "]";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int compareTo(Size o) {
      return (this == o) ? 0 : getCores().compareTo(o.getCores());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean supportsArchitecture(Architecture architecture) {
      return supportedArchitectures.contains(architecture);
   }
}
