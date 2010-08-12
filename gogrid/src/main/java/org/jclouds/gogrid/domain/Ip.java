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

package org.jclouds.gogrid.domain;

import com.google.common.primitives.Longs;
import com.google.gson.annotations.SerializedName;

/**
 * @author Oleksiy Yarmula
 */
public class Ip implements Comparable<Ip> {

   private long id;

   private String ip;
   private String subnet;
   @SerializedName("public")
   private boolean isPublic;
   private IpState state;
   private Option datacenter;

   /**
    * A no-args constructor is required for deserialization
    */
   public Ip() {
   }

   /**
    * Constructs a generic IP address without any additional options.
    * 
    * @param ip
    *           ip address
    */
   public Ip(String ip) {
      this.ip = ip;
   }

   public Ip(long id, String ip, String subnet, boolean isPublic, IpState state, Option datacenter) {
      this.id = id;
      this.ip = ip;
      this.subnet = subnet;
      this.isPublic = isPublic;
      this.state = state;
      this.datacenter = datacenter;
   }

   public long getId() {
      return id;
   }

   public Option getDatacenter() {
      return datacenter;
   }

   public String getIp() {
      return ip;
   }

   public String getSubnet() {
      return subnet;
   }

   public boolean isPublic() {
      return isPublic;
   }

   public IpState getState() {
      return state;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Ip other = (Ip) obj;
      if (datacenter == null) {
         if (other.datacenter != null)
            return false;
      } else if (!datacenter.equals(other.datacenter))
         return false;
      if (id != other.id)
         return false;
      if (ip == null) {
         if (other.ip != null)
            return false;
      } else if (!ip.equals(other.ip))
         return false;
      if (isPublic != other.isPublic)
         return false;
      if (state == null) {
         if (other.state != null)
            return false;
      } else if (!state.equals(other.state))
         return false;
      if (subnet == null) {
         if (other.subnet != null)
            return false;
      } else if (!subnet.equals(other.subnet))
         return false;
      return true;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((datacenter == null) ? 0 : datacenter.hashCode());
      result = prime * result + (int) (id ^ (id >>> 32));
      result = prime * result + ((ip == null) ? 0 : ip.hashCode());
      result = prime * result + (isPublic ? 1231 : 1237);
      result = prime * result + ((state == null) ? 0 : state.hashCode());
      result = prime * result + ((subnet == null) ? 0 : subnet.hashCode());
      return result;
   }

   @Override
   public String toString() {
      return "Ip [datacenter=" + datacenter + ", id=" + id + ", ip=" + ip + ", isPublic="
               + isPublic + ", state=" + state + ", subnet=" + subnet + "]";
   }

   @Override
   public int compareTo(Ip o) {
      return Longs.compare(id, o.getId());
   }
}
