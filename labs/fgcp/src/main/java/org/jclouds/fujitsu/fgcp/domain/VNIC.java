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

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;

/**
 * Represents a virtual network interface card (NIC).
 * 
 * @author Dies Koper
 */
@XmlRootElement(name = "vnic")
public class VNIC {
   private String networkId;

   private String privateIp;

   private int nicNo;

   public String getNetworkId() {
      return networkId;
   }

   public String getPrivateIp() {
      return privateIp;
   }

   public int getNicNo() {
      return nicNo;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(networkId);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      VNIC that = VNIC.class.cast(obj);
      return Objects.equal(this.networkId, that.networkId);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues()
            .add("networkId", networkId).add("privateIp", privateIp)
            .add("nicNo", nicNo).toString();
   }
}
