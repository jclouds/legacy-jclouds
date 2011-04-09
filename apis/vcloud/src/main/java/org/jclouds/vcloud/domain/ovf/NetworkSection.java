/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud.domain.ovf;

import java.util.Set;

import org.jclouds.vcloud.domain.ovf.network.Network;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * The NetworkSection element shall list all logical networks used in the OVF package.
 * 
 * @author Adrian Cole
 */
public class NetworkSection {
   private final String info;
   private final Set<Network> networks = Sets.newLinkedHashSet();

   public NetworkSection(String info, Iterable<Network> networks) {
      this.info = info;
      Iterables.addAll(this.networks, networks);
   }

   public String getInfo() {
      return info;
   }

   /**
    * All networks referred to from Connection elements in all {@link VirtualHardwareSection} elements shall
    * be defined in the NetworkSection.
    * 
    * @return
    */
   public Set<Network> getNetworks() {
      return networks;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((info == null) ? 0 : info.hashCode());
      result = prime * result + ((networks == null) ? 0 : networks.hashCode());
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
      NetworkSection other = (NetworkSection) obj;
      if (info == null) {
         if (other.info != null)
            return false;
      } else if (!info.equals(other.info))
         return false;
      if (networks == null) {
         if (other.networks != null)
            return false;
      } else if (!networks.equals(other.networks))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[info=" + info + ", networks=" + networks + "]";
   }

}