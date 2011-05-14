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
package org.jclouds.ibm.smartcloud.domain;

import javax.annotation.Nullable;

/**
 * 
 * The current state of a Address
 * 
 * @author Adrian Cole
 */
public class Address implements Comparable<Address> {

   public static enum State {
      NEW, ALLOCATING, FREE, ATTACHED, RELEASING, RELEASED, FAILED, RELEASE_PENDING;
      public static State fromValue(int v) {
         switch (v) {
            case 0:
               return NEW;
            case 1:
               return ALLOCATING;
            case 2:
               return FREE;
            case 3:
               return ATTACHED;
            case 4:
               return RELEASING;
            case 5:
               return RELEASED;
            case 6:
               return FAILED;
            case 7:
               return RELEASE_PENDING;
            default:
               throw new IllegalArgumentException("invalid state:" + v);
         }
      }
   }

   private int state;
   private String location;
   private String ip;
   private String id;
   @Nullable
   private String instanceId;

   public Address(int state, String location, String ip, String id, String instanceId) {
      this.state = state;
      this.location = location;
      this.ip = ip;
      this.id = id;
      this.instanceId = instanceId;
   }

   Address() {

   }

   public State getState() {
      return State.fromValue(state);
   }

   public String getLocation() {
      return location;
   }

   public String getIP() {
      return "".equals(ip.trim()) ? null : ip.trim();
   }

   public String getId() {
      return id;
   }

   public String getInstanceId() {
      return instanceId;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((instanceId == null) ? 0 : instanceId.hashCode());
      result = prime * result + ((ip == null) ? 0 : ip.hashCode());
      result = prime * result + ((location == null) ? 0 : location.hashCode());
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
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
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
      if (location == null) {
         if (other.location != null)
            return false;
      } else if (!location.equals(other.location))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + id + ", ip=" + ip + ", location=" + location + ", state=" + getState() + ", instanceId="
               + instanceId + "]";
   }

   @Override
   public int compareTo(Address arg0) {
      return id.compareTo(arg0.getId());
   }
}
