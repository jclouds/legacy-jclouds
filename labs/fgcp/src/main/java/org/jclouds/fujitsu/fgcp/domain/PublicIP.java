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
package org.jclouds.fujitsu.fgcp.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.xml.bind.annotation.XmlElement;

import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;

/**
 * Represents a public IP address.
 * <p>
 * A public IP address can be allocated to a virtual system, then needs to be
 * enabled/attached before it can be mapped to a virtual server by configuring
 * the NAT settings of virtual system's firewall.
 * 
 * @author Dies Koper
 */
public class PublicIP {

   public static enum Version {
      IPv4, IPv6, UNRECOGNIZED;

      @Override
      public String toString() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL,
               name());
      }

      public static Version fromValue(String version) {
         try {
            return valueOf(CaseFormat.UPPER_CAMEL.to(
                  CaseFormat.UPPER_UNDERSCORE,
                  checkNotNull(version, "version")));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }

   }

   protected String address;
   @XmlElement(name = "v4v6Flag")
   protected Version version;

   public String getAddress() {
      return address;
   }

   public Version getVersion() {
      return version;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(address);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      PublicIP that = PublicIP.class.cast(obj);
      return Objects.equal(this.address, that.address);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues()
            .add("address", address).add("version", version).toString();
   }
}
