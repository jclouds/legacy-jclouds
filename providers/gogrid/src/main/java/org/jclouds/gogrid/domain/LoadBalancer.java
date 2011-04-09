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
package org.jclouds.gogrid.domain;

import java.util.Set;

import com.google.common.primitives.Longs;
import com.google.gson.annotations.SerializedName;

/**
 * @author Oleksiy Yarmula
 */
public class LoadBalancer implements Comparable<LoadBalancer> {

   private long id;
   private String name;
   private String description;
   @SerializedName("virtualip")
   private IpPortPair virtualIp;
   @SerializedName("realiplist")
   private Set<IpPortPair> realIpList;
   private LoadBalancerType type;
   private LoadBalancerPersistenceType persistence;
   private LoadBalancerOs os;
   private LoadBalancerState state;
   private Option datacenter;

   /**
    * A no-args constructor is required for deserialization
    */
   public LoadBalancer() {
   }

   public LoadBalancer(long id, String name, String description, IpPortPair virtualIp,
            Set<IpPortPair> realIpList, LoadBalancerType type,
            LoadBalancerPersistenceType persistence, LoadBalancerOs os, LoadBalancerState state,
            Option datacenter) {
      this.id = id;
      this.name = name;
      this.description = description;
      this.virtualIp = virtualIp;
      this.realIpList = realIpList;
      this.type = type;
      this.persistence = persistence;
      this.os = os;
      this.state = state;
      this.datacenter = datacenter;
   }

   public long getId() {
      return id;
   }

   public Option getDatacenter() {
      return datacenter;
   }

   public String getName() {
      return name;
   }

   public String getDescription() {
      return description;
   }

   public IpPortPair getVirtualIp() {
      return virtualIp;
   }

   public Set<IpPortPair> getRealIpList() {
      return realIpList;
   }

   public LoadBalancerType getType() {
      return type;
   }

   public LoadBalancerPersistenceType getPersistence() {
      return persistence;
   }

   public LoadBalancerOs getOs() {
      return os;
   }

   public LoadBalancerState getState() {
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
      LoadBalancer other = (LoadBalancer) obj;
      if (datacenter == null) {
         if (other.datacenter != null)
            return false;
      } else if (!datacenter.equals(other.datacenter))
         return false;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (id != other.id)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (os == null) {
         if (other.os != null)
            return false;
      } else if (!os.equals(other.os))
         return false;
      if (persistence == null) {
         if (other.persistence != null)
            return false;
      } else if (!persistence.equals(other.persistence))
         return false;
      if (realIpList == null) {
         if (other.realIpList != null)
            return false;
      } else if (!realIpList.equals(other.realIpList))
         return false;
      if (state == null) {
         if (other.state != null)
            return false;
      } else if (!state.equals(other.state))
         return false;
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.equals(other.type))
         return false;
      if (virtualIp == null) {
         if (other.virtualIp != null)
            return false;
      } else if (!virtualIp.equals(other.virtualIp))
         return false;
      return true;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((datacenter == null) ? 0 : datacenter.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + (int) (id ^ (id >>> 32));
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((os == null) ? 0 : os.hashCode());
      result = prime * result + ((persistence == null) ? 0 : persistence.hashCode());
      result = prime * result + ((realIpList == null) ? 0 : realIpList.hashCode());
      result = prime * result + ((state == null) ? 0 : state.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      result = prime * result + ((virtualIp == null) ? 0 : virtualIp.hashCode());
      return result;
   }

   @Override
   public int compareTo(LoadBalancer o) {
      return Longs.compare(id, o.getId());
   }

   @Override
   public String toString() {
      return "LoadBalancer [datacenter=" + datacenter + ", description=" + description + ", id="
               + id + ", name=" + name + ", os=" + os + ", persistence=" + persistence
               + ", realIpList=" + realIpList + ", state=" + state + ", type=" + type
               + ", virtualIp=" + virtualIp + "]";
   }
}
