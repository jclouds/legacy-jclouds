/*
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
package org.jclouds.vcloud.director.v1_5.domain.ovf;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.VCLOUD_OVF_NS;

import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.vcloud.director.v1_5.domain.ovf.internal.BaseEnvelope;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 * @author Adam Lowe
 */
@XmlRootElement(name = "Envelope", namespace = VCLOUD_OVF_NS)
public class Envelope extends BaseEnvelope<VirtualSystem, Envelope> {

   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromEnvelope(this);
   }

   public static class Builder extends BaseEnvelope.Builder<VirtualSystem, Envelope> {

      /**
       * {@inheritDoc}
       */
      @Override
      public Envelope build() {
         return new Envelope(diskSections, networkSections, additionalSections, virtualSystem);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder additionalSection(SectionType additionalSection) {
         this.additionalSections.add(checkNotNull(additionalSection, "additionalSection"));
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder additionalSections(Iterable<? extends SectionType> additionalSections) {
         this.additionalSections = ImmutableSet.<SectionType> copyOf(checkNotNull(additionalSections, "additionalSections"));
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder diskSection(DiskSection diskSection) {
         return Builder.class.cast(super.diskSection(diskSection));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder diskSections(Iterable<? extends DiskSection> diskSections) {
         return Builder.class.cast(super.diskSections(diskSections));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromEnvelope(BaseEnvelope<VirtualSystem, Envelope> in) {
         return Builder.class.cast(super.fromEnvelope(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder networkSection(NetworkSection networkSection) {
         return Builder.class.cast(super.networkSection(networkSection));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder networkSections(Iterable<? extends NetworkSection> networkSections) {
         return Builder.class.cast(super.networkSections(networkSections));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder virtualSystem(VirtualSystem virtualSystem) {
         return Builder.class.cast(super.virtualSystem(virtualSystem));
      }

   }

   private Envelope(Iterable<? extends DiskSection> diskSections, Iterable<? extends NetworkSection> networkSections,
            Iterable<? extends SectionType> additionalSections, VirtualSystem virtualSystem) {
      super(diskSections, networkSections, additionalSections, virtualSystem);
   }
   
   private Envelope() {
      // For JaxB
   }

}