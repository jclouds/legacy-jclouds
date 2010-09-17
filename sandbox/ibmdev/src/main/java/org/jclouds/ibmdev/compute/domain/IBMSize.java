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

package org.jclouds.ibmdev.compute.domain;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.internal.SizeImpl;
import org.jclouds.ibmdev.domain.InstanceType;

import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
public class IBMSize extends SizeImpl {

   /** The serialVersionUID */
   private static final long serialVersionUID = -8520373150950058296L;

   private final IBMImage image;

   private final InstanceType instanceType;

   // until we can lookup cores by id, we are multiplying price *100 to get a positive integer we
   // can compare against.
   public IBMSize(IBMImage in, InstanceType instanceType) {
      super(instanceType.getId(), instanceType.getLabel(), in.getId() + "/" + instanceType.getId(), in.getLocation(),
               in.getRawImage().getManifest(), ImmutableMap.<String, String> of(), (int) (instanceType.getPrice()
                        .getRate() * 100), (int) (instanceType.getPrice().getRate() * 1024d), (int) (instanceType
                        .getPrice().getRate() * 100d), null);
      this.image = in;
      this.instanceType = instanceType;
   }

   public IBMImage getImage() {
      return image;
   }

   public InstanceType getInstanceType() {
      return instanceType;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean supportsImage(Image input) {
      return image.getId().equals(input.getId());
   }

   @Override
   public String toString() {
      return "[id=" + getId() + ", providerId=" + getProviderId() + ", name=" + getName() + ", cores=" + getCores()
               + ", ram=" + getRam() + ", disk=" + getDisk() + ", supportsImage=" + image.getId() + ", rate="
               + instanceType.getPrice().getRate() + "]";
   }

}