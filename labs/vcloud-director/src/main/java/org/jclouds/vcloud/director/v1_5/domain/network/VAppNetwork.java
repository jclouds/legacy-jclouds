/**
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

package org.jclouds.vcloud.director.v1_5.domain.network;

import static com.google.common.base.Objects.equal;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents a vApp network.
 *
 * <pre>
 * &lt;complexType name="VAppNetwork" /&gt;
 * </pre>
 */
@XmlType(name = "VAppNetwork")
public class VAppNetwork extends Network {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromVAppNetwork(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends Network.Builder<B> {

      private Boolean deployed;

      /**
       * @see VAppNetwork#isDeployed()
       */
      public B isDeployed(Boolean deployed) {
         this.deployed = deployed;
         return self();
      }

      /**
       * @see VAppNetwork#isDeployed()
       */
      public B deployed() {
         this.deployed = Boolean.TRUE;
         return self();
      }

      /**
       * @see VAppNetwork#isDeployed()
       */
      public B notDeployed() {
         this.deployed = Boolean.FALSE;
         return self();
      }

      @Override
      public VAppNetwork build() {
         return new VAppNetwork(this);
      }

      public B fromVAppNetwork(VAppNetwork in) {
         return fromNetwork(in).isDeployed(in.isDeployed());
      }
   }

   protected VAppNetwork() {
      // For JAXB and builder use
   }

   public VAppNetwork(Builder<?> builder) {
      super(builder);
      deployed = builder.deployed;

   }

   @XmlAttribute
   private Boolean deployed;

   /**
    * Gets the value of the deployed property.
    */
   public Boolean isDeployed() {
      return deployed;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      VAppNetwork that = VAppNetwork.class.cast(o);
      return super.equals(that) && equal(this.deployed, that.deployed);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), deployed);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("deployed", deployed);
   }
}
