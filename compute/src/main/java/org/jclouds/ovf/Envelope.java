/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.ovf;

import org.jclouds.ovf.internal.BaseEnvelope;

import com.google.common.collect.Multimap;

/**
 * @author Adrian Cole
 */
public class Envelope extends BaseEnvelope<VirtualSystem, Envelope> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   public Builder toBuilder() {
      return new Builder().fromEnvelope(this);
   }

   public static class Builder extends BaseEnvelope.Builder<VirtualSystem, Envelope> {

      /**
       * {@inheritDoc}
       */
      public Envelope build() {
         return new Envelope(diskSections, networkSections, additionalSections, virtualSystem);
      }

      /**
       * {@inheritDoc}
       */
      @SuppressWarnings("unchecked")
      @Override
      public Builder additionalSection(String name, Section additionalSection) {
         return Builder.class.cast(super.additionalSection(name, additionalSection));
      }

      /**
       * {@inheritDoc}
       */
      @SuppressWarnings("unchecked")
      @Override
      public Builder additionalSections(Multimap<String, Section> additionalSections) {
         return Builder.class.cast(super.additionalSections(additionalSections));
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

   @SuppressWarnings("unchecked")
   public Envelope(Iterable<? extends DiskSection> diskSections, Iterable<? extends NetworkSection> networkSections,
            Multimap<String, Section> additionalSections, VirtualSystem virtualSystem) {
      super(diskSections, networkSections, additionalSections, virtualSystem);
   }

}
