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
 */
@XmlSeeAlso(
      IpsecVpnLocalPeer.class
)
public abstract class IpsecVpnManagedPeerType<T extends IpsecVpnManagedPeerType<T>> extends IpsecVpnPeerType<T> {

   public abstract static class Builder<T extends IpsecVpnManagedPeerType<T>> {
      protected String id;
      protected String name;

      /**
       * @see IpRange#getStartAddress()
       */
      public Builder<T> id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see IpRange#getEndAddress()
       */
      public Builder<T> name(String name) {
         this.name = name;
         return this;
      }

      public Builder<T> fromIpsecVpnManagedPeerType(IpsecVpnManagedPeerType<T> in) {
         return id(in.getId())
               .name(in.getName());
      }
   }
   
   @XmlElement(name = "Id", required = true)
   protected String id;
   @XmlElement(name = "Name", required = true)
   protected String name;

   protected IpsecVpnManagedPeerType(String id, String name) {
      this.id = id;
      this.name = name;
   }

   protected IpsecVpnManagedPeerType() {
      // for JAXB
   }

   /**
    * @return id of peer network
    */
   public String getId() {
      return id;
   }

   /**
    * @return the name of the peer network
    */
   public String getName() {
      return name;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      IpsecVpnManagedPeerType<?> that = IpsecVpnManagedPeerType.class.cast(o);
      return equal(id, that.id) && equal(name, that.name);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper("").add("id", id).add("name", name);
   }
}
