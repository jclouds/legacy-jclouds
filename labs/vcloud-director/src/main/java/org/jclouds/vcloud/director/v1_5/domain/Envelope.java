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
package org.jclouds.vcloud.director.v1_5.domain;

import static org.jclouds.dmtf.DMTFConstants.OVF_NS;

import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.dmtf.ovf.internal.BaseEnvelope;

/**
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "Envelope", namespace = OVF_NS)
public class Envelope extends BaseEnvelope<VirtualSystem, Envelope> {

   @SuppressWarnings("rawtypes")
   public static Builder<?> builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder<?> toBuilder() {
      return builder().fromEnvelope(this);
   }

   public static class Builder<B extends Builder<B>> extends BaseEnvelope.Builder<B, VirtualSystem, Envelope> {

      /**
       * {@inheritDoc}
       */
      @Override
      public Envelope build() {
         return new Envelope(this);
      }
   }

   protected Envelope(Builder<?> builder) {
      super(builder);
   }
   
   protected Envelope() {
      // For JaxB
   }
}