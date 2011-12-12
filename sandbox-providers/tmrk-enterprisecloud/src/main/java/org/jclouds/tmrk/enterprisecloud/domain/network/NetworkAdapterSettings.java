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
package org.jclouds.tmrk.enterprisecloud.domain.network;

import com.google.common.collect.Sets;

import javax.xml.bind.annotation.XmlElement;
import java.util.Collections;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <xs:complexType name="NetworkAdapterSettingsType">
 * @author Jason King
 */
public class NetworkAdapterSettings {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromNetworkAdapterSettings(this);
   }

   public static class Builder {

       private Set<NetworkAdapterSetting> adapters = Sets.newLinkedHashSet();

       /**
        * @see org.jclouds.tmrk.enterprisecloud.domain.network.NetworkAdapterSettings#getNetworkAdapterSettings()
        */
       public Builder adapters(Set<NetworkAdapterSetting> adapters) {
          this.adapters = Sets.newLinkedHashSet(checkNotNull(adapters, "adapters"));
          return this;
       }

       public Builder addNetworkAdapterSetting(NetworkAdapterSetting adapter) {
          adapters.add(checkNotNull(adapter, "adapter"));
          return this;
       }

       public NetworkAdapterSettings build() {
           return new NetworkAdapterSettings(adapters);
       }

       public Builder fromNetworkAdapterSettings(NetworkAdapterSettings in) {
          return adapters(in.getNetworkAdapterSettings());
       }
   }

   private NetworkAdapterSettings() {
      //For JAXB and builder use
   }

   private NetworkAdapterSettings(Set<NetworkAdapterSetting> adapters) {
      this.adapters = Sets.newLinkedHashSet(adapters);
   }

   @XmlElement(name = "NetworkAdapter")
   private Set<NetworkAdapterSetting> adapters = Sets.newLinkedHashSet();

   public Set<NetworkAdapterSetting> getNetworkAdapterSettings() {
      return Collections.unmodifiableSet(adapters);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      NetworkAdapterSettings that = (NetworkAdapterSettings) o;

      if (!adapters.equals(that.adapters)) return false;

      return true;
   }

   @Override
   public int hashCode() {
      return adapters.hashCode();
   }

   public String toString() {
      return "["+ adapters.toString()+"]";
   }
}
