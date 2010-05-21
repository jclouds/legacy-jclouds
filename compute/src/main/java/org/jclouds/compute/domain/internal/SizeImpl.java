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

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.Size;
import org.jclouds.domain.Location;
import org.jclouds.domain.ResourceMetadata;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class SizeImpl extends ComputeMetadataImpl implements Size {
   /** The serialVersionUID */
   private static final long serialVersionUID = 8994255275911717567L;
   private final double cores;
   private final int ram;
   private final int disk;

   private final Set<Architecture> supportedArchitectures = Sets.newHashSet();

   public SizeImpl(String id, String name, String handle, @Nullable Location location, URI uri,
            Map<String, String> userMetadata, double cores, int ram, int disk,
            Iterable<Architecture> supportedArchitectures) {
      super(ComputeType.SIZE, id, name, handle, location, uri, userMetadata);
      this.cores = cores;
      this.ram = ram;
      this.disk = disk;
      Iterables.addAll(this.supportedArchitectures, checkNotNull(supportedArchitectures,
               "supportedArchitectures"));
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
      return "[id=" + getProviderId() + ", cores=" + cores + ", ram=" + ram + ", disk=" + disk
               + ", supportedArchitectures=" + supportedArchitectures + "]";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<Architecture> getSupportedArchitectures() {
      return supportedArchitectures;
   }
}
