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

package org.jclouds.googlecompute.domain;

import com.google.common.base.Objects;
import org.jclouds.javax.annotation.Nullable;

import java.beans.ConstructorProperties;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;

/**
 * Access configuration to an instance's network.
 * <p/>
 * This specifies how this interface is configured to interact with other network services,
 * such as connecting to the internet. Currently, ONE_TO_ONE_NAT is the only access config supported.
 *
 * @author David Alves
 */
public class InstanceNetworkInterfaceAccessConfig {

   public enum Type {
      ONE_TO_ONE_NAT
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromAccessConfig(this);
   }

   public static class Builder {

      private String name;
      private Type type;
      private String natIP;

      /**
       * @see InstanceNetworkInterfaceAccessConfig#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see InstanceNetworkInterfaceAccessConfig#getType()
       */
      public Builder type(Type type) {
         this.type = type;
         return this;
      }

      /**
       * @see InstanceNetworkInterfaceAccessConfig#getNatIP()
       */
      public Builder natIP(String natIP) {
         this.natIP = natIP;
         return this;
      }

      public InstanceNetworkInterfaceAccessConfig build() {
         return new InstanceNetworkInterfaceAccessConfig(name, type, natIP);
      }

      public Builder fromAccessConfig(InstanceNetworkInterfaceAccessConfig in) {
         return this.name(in.getName()).type(in.getType()).natIP
                 (in.getNatIP());
      }
   }

   private String name;
   private Type type;
   private String natIP;


   @ConstructorProperties({
           "name", "type", "natIP"
   })
   public InstanceNetworkInterfaceAccessConfig(String name, Type type, String natIP) {
      this.name = name;
      this.type = type;
      this.natIP = natIP;
   }

   /**
    * @return name of this access configuration.
    */
   public String getName() {
      return name;
   }

   /**
    * @return type of configuration. Must be set to ONE_TO_ONE_NAT. This configures port-for-port NAT to the internet.
    */
   public Type getType() {
      return type;
   }

   /**
    * @return an external IP address associated with this instance. Specify an unused static IP address available to
    *         the project. If left blank, the external IP will be drawn from a shared ephemeral pool.
    */
   @Nullable
   public String getNatIP() {
      return natIP;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(name, type, natIP);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      InstanceNetworkInterfaceAccessConfig that = InstanceNetworkInterfaceAccessConfig.class.cast(obj);
      return equal(this.name, that.name)
              && equal(this.type, that.type)
              && equal(this.natIP, that.natIP);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return toStringHelper(this)
              .add("name", name).add("type", type).add("natIP", natIP);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }


}
