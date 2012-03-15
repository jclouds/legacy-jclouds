/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 *(Link.builder().regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless(Link.builder().required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

@XmlRootElement(name = "NetworkType")
public class NetworkType extends EntityType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromNetworkType(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static abstract class Builder<B extends Builder<B>> extends EntityType.Builder<B> {

      private NetworkConfiguration networkConfiguration;

      /**
       * @see NetworkType#getConfiguration()
       */
      public B configuration(NetworkConfiguration networkConfiguration) {
         this.networkConfiguration = networkConfiguration;
         return self();
      }

      @Override
      public NetworkType build() {
         return new NetworkType(this);
      }

      public B fromNetworkType(NetworkType in) {
         return fromEntityType(in).configuration(in.getConfiguration());
      }
   }

   public NetworkType(Builder<?> builder) {
      super(builder);
      this.networkConfiguration = builder.networkConfiguration;
   }

   protected NetworkType() {
      // for JAXB
   }

   @XmlElement(name = "Configuration")
   private NetworkConfiguration networkConfiguration;

   /**
    * @return optional configuration
    */
   public NetworkConfiguration getConfiguration() {
      return networkConfiguration;
   }

   @Override
   public boolean equals(Object o) {
      if (!super.equals(o))
         return false;
      NetworkType that = NetworkType.class.cast(o);
      return super.equals(that) && equal(networkConfiguration, that.networkConfiguration);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), networkConfiguration);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("configuration", networkConfiguration);
   }

}
