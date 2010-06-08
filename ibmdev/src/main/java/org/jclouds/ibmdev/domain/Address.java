/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the ;License;);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an ;AS IS; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.ibmdev.domain;

import javax.annotation.Nullable;

/**
 * 
 * The current state of a Address
 * 
 * @author Adrian Cole
 */
public class Address {
   private int state;
   private int location;
   private String ip;
   private long id;
   @Nullable
   private Long instanceId;

   public Address(int state, int location, String ip, long id, Long instanceId) {
      this.state = state;
      this.location = location;
      this.ip = ip;
      this.id = id;
      this.instanceId = instanceId;
   }

   public Address() {

   }

   public int getState() {
      return state;
   }

   public void setState(int state) {
      this.state = state;
   }

   public int getLocation() {
      return location;
   }

   public void setLocation(int location) {
      this.location = location;
   }

   public String getIp() {
      return ip;
   }

   public void setIp(String ip) {
      this.ip = ip;
   }

   public long getId() {
      return id;
   }

   public void setId(long id) {
      this.id = id;
   }

   public Long getInstanceId() {
      return instanceId;
   }

   public void setInstanceId(Long instanceId) {
      this.instanceId = instanceId;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (id ^ (id >>> 32));
      result = prime * result + ((instanceId == null) ? 0 : instanceId.hashCode());
      result = prime * result + ((ip == null) ? 0 : ip.hashCode());
      result = prime * result + location;
      result = prime * result + state;
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
      Address other = (Address) obj;
      if (id != other.id)
         return false;
      if (instanceId == null) {
         if (other.instanceId != null)
            return false;
      } else if (!instanceId.equals(other.instanceId))
         return false;
      if (ip == null) {
         if (other.ip != null)
            return false;
      } else if (!ip.equals(other.ip))
         return false;
      if (location != other.location)
         return false;
      if (state != other.state)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + id + ", ip=" + ip + ", location=" + location + ", state=" + state
               + ", instanceId=" + instanceId + "]";
   }

}