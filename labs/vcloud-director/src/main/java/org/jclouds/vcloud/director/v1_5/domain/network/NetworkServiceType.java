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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import com.google.common.base.Objects;

/**
 * Represents a network service
 *
 * @author danikov
 * @author Adam Lowe
 */
@XmlSeeAlso({
   DhcpService.class,
   IpsecVpnService.class,
   FirewallService.class,
   DhcpService.class,
   StaticRoutingService.class,
   NatService.class
})
public abstract class NetworkServiceType<T extends NetworkServiceType<T>> {
   public abstract Builder<T> toBuilder();

   public abstract static class Builder<T extends NetworkServiceType<T>> {
      protected boolean isEnabled;

      /**
       * @see NetworkServiceType#isEnabled()
       */
      public Builder<T> enabled(boolean isEnabled) {
         this.isEnabled = isEnabled;
         return this;
      }

      public abstract NetworkServiceType<T> build();

      public Builder<T> fromNetworkServiceType(NetworkServiceType<T> in) {
         return enabled(in.isEnabled());
      }
   }

   protected NetworkServiceType(boolean enabled) {
      isEnabled = enabled;
   }

   protected NetworkServiceType() {
      // for JAXB
   }

   @XmlElement(name = "IsEnabled")
   private boolean isEnabled;

   /**
    * @return Enable or disable the service using this flag
    */
   public boolean isEnabled() {
      return isEnabled;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      NetworkServiceType<?> that = NetworkServiceType.class.cast(o);
      return equal(isEnabled, that.isEnabled);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(isEnabled);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper("").add("isEnabled", isEnabled);
   }
}
