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

import java.util.Set;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * A list of IpAddresses.
 *
 * @author danikov
 */
@XmlRootElement(name = "IpRanges")
public class IpRanges {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromIpRanges(this);
   }

   public static class Builder {
      private Set<IpRange> ipRanges = Sets.newLinkedHashSet();

      /**
       * @see IpRanges#getIpRanges()
       */
      public Builder ipRanges(Set<IpRange> ipRanges) {
         this.ipRanges = Sets.newLinkedHashSet(checkNotNull(ipRanges, "ipRanges"));
         return this;
      }

      /**
       * @see IpRanges#getIpRanges()
       */
      public Builder ipRange(IpRange ipRange) {
         ipRanges.add(checkNotNull(ipRange, "ipRange"));
         return this;
      }

      public IpRanges build() {
         return new IpRanges(ipRanges);
      }

      public Builder fromIpRanges(IpRanges in) {
         return ipRanges(in.getIpRanges());
      }
   }

   private IpRanges() {
      // For JAXB and builder use
   }

   private IpRanges(Set<IpRange> ipRanges) {
      this.ipRanges = ImmutableSet.copyOf(ipRanges);
   }

   @XmlElementRef
   private Set<IpRange> ipRanges = Sets.newLinkedHashSet();

   public Set<IpRange> getIpRanges() {
      return ImmutableSet.copyOf(ipRanges);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      IpRanges that = IpRanges.class.cast(o);
      return equal(ipRanges, that.ipRanges);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(ipRanges);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("ipRanges", ipRanges).toString();
   }
}
