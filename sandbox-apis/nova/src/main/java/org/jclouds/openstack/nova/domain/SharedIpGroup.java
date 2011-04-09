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
package org.jclouds.openstack.nova.domain;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * A shared IP group is a collection of servers that can share IPs with other members of the group.
 * Any server in a group can share one or more public IPs with any other server in the group. With
 * the exception of the first server in a shared IP group, servers must be launched into shared IP
 * groups. A server may only be a member of one shared IP group.
 * 
 * @author Adrian Cole
 */
public class SharedIpGroup {

   private int id;
   private String name;

   private List<Integer> servers = Lists.newArrayList();

   public SharedIpGroup() {
   }

   public SharedIpGroup(int id, String name) {
      this.id = id;
      this.name = name;
   }

   public void setId(int id) {
      this.id = id;
   }

   public int getId() {
      return id;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public void setServers(List<Integer> servers) {
      this.servers = servers;
   }

   public List<Integer> getServers() {
      return servers;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + id;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((servers == null) ? 0 : servers.hashCode());
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
      SharedIpGroup other = (SharedIpGroup) obj;
      if (id != other.id)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (servers == null) {
         if (other.servers != null)
            return false;
      } else if (!servers.equals(other.servers))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "SharedIpGroup [id=" + id + ", name=" + name + ", servers=" + servers + "]";
   }

}
