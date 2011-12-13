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
 * <xs:complexType name="IpAddressesType">
 * @author Jason King
 */
public class IpAddresses {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromIpAddresses(this);
   }

   public static class Builder {

       private Set<IpAddress> addresses = Sets.newLinkedHashSet();

       /**
        * @see org.jclouds.tmrk.enterprisecloud.domain.network.IpAddresses#getIpAddresses
        */
       public Builder addresses(Set<IpAddress> addresses) {
          this.addresses = Sets.newLinkedHashSet(checkNotNull(addresses, "addresses"));
          return this;
       }

       public Builder addIpAddress(IpAddress address) {
          addresses.add(checkNotNull(address, "address"));
          return this;
       }

       public IpAddresses build() {
           return new IpAddresses(addresses);
       }

       public Builder fromIpAddresses(IpAddresses in) {
          return addresses(in.getIpAddresses());
       }
   }

   private IpAddresses() {
      //For JAXB and builder use
   }

   private IpAddresses(Set<IpAddress> addresses) {
      this.addresses = Sets.newLinkedHashSet(addresses);
   }

   @XmlElement(name = "IpAddress")
   private Set<IpAddress> addresses = Sets.newLinkedHashSet();

   public Set<IpAddress> getIpAddresses() {
      return Collections.unmodifiableSet(addresses);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      IpAddresses that = (IpAddresses) o;

      if (!addresses.equals(that.addresses)) return false;

      return true;
   }

   @Override
   public int hashCode() {
      return addresses.hashCode();
   }

   public String toString() {
      return "["+ addresses.toString()+"]";
   }
}
