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
package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * A list of IpAddresses.
 *
 * @author danikov
 */
@XmlRootElement(name = "IpAddresses")
public class IpAddresses {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromIpAddresses(this);
   }

   public static class Builder {
      private Set<String> ipAddresses = Sets.newLinkedHashSet();

      /**
       * @see IpAddresses#getIpAddresses()
       */
      public Builder ipAddresses(Set<String> ipAddresses) {
         this.ipAddresses.addAll(checkNotNull(ipAddresses, "ipAddresses"));
         return this;
      }

      /**
       * @see IpAddresses#getIpAddresses()
       */
      public Builder ipAddress(String ipAddress) {
         ipAddresses.add(checkNotNull(ipAddress, "ipAddress"));
         return this;
      }

      public IpAddresses build() {
         return new IpAddresses(ipAddresses);
      }

      public Builder fromIpAddresses(IpAddresses in) {
         return ipAddresses(in.getIpAddresses());
      }
   }

   private IpAddresses() {
      // For JAXB and builder use
   }

   private IpAddresses(Set<String> orgs) {
      this.ipAddresses = ImmutableSet.copyOf(orgs);
   }

   @XmlElement(name = "IpAddress")
   private Set<String> ipAddresses = Sets.newLinkedHashSet();

   public Set<String> getIpAddresses() {
      return Collections.unmodifiableSet(ipAddresses);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      IpAddresses that = IpAddresses.class.cast(o);
      return equal(ipAddresses, that.ipAddresses);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(ipAddresses);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("ipAddresses", ipAddresses).toString();
   }
}
