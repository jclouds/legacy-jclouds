/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.elasticstack.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

/**
 * 
 * @author Adrian Cole
 */
public class NIC {
   private final String dhcp;
   private final Model model;
   private final String vlan;
   private final String mac;

   public NIC(@Nullable String dhcp, Model model, @Nullable String vlan, @Nullable String mac) {
      this.dhcp = dhcp;
      this.model = checkNotNull(model, "model");
      this.vlan = vlan;
      this.mac = mac;
   }

   /**
    * 
    * @return The IP address offered by DHCP to network interface 0. If unset, no address is
    *         offered. Set to 'auto' to allocate a temporary IP at boot.
    */
   public String getDhcp() {
      return dhcp;
   }

   /**
    * 
    * @return Create network interface with given type (use 'e1000' as default value; 'rtl8139' or
    *         'virtio' are also available).
    */
   public Model getModel() {
      return model;
   }

   /**
    * 
    * @return The VLAN to which the network interface is attached.
    */
   public String getVlan() {
      return vlan;
   }

   /**
    * 
    * @return The MAC address of the network interface. If unset, a randomly generated address is
    *         used. If set, should be unique on the VLAN.
    */
   public String getMac() {
      return mac;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((dhcp == null) ? 0 : dhcp.hashCode());
      result = prime * result + ((mac == null) ? 0 : mac.hashCode());
      result = prime * result + ((model == null) ? 0 : model.hashCode());
      result = prime * result + ((vlan == null) ? 0 : vlan.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      NIC other = (NIC) obj;
      if (dhcp == null) {
         if (other.dhcp != null)
            return false;
      } else if (!dhcp.equals(other.dhcp))
         return false;
      if (mac == null) {
         if (other.mac != null)
            return false;
      } else if (!mac.equals(other.mac))
         return false;
      if (model != other.model)
         return false;
      if (vlan == null) {
         if (other.vlan != null)
            return false;
      } else if (!vlan.equals(other.vlan))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[dhcp=" + dhcp + ", model=" + model + ", vlan=" + vlan + ", mac=" + mac + "]";
   }
}