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
package org.jclouds.openstack.nova.domain;

/**
 * 
 * A flavor is an available hardware configuration for a server. Each flavor has a unique
 * combination of disk space and memory capacity.
 * 
 * @author Adrian Cole
 */
public class Flavor extends Resource {

   private final int id;
   private final String name;
   private final Integer disk;
   private final Integer ram;
   private final Integer vcpus;

   //Required because of how Gson is being used to do wire marshalling with the Server class
   private Flavor(){
      id=0;
      name=null;
      disk=null;
      ram=null;
      vcpus=null;
   }

   public Flavor(int id, String name, Integer disk, Integer ram, Integer vcpus) {
      this.id = id;
      this.name = name;
      this.disk = disk;
      this.ram = ram;
      this.vcpus = vcpus;
   }

   public Integer getDisk() {
      return disk;
   }

   public int getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public Integer getRam() {
      return ram;
   }

   public Integer getVcpus() {
      return vcpus;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((disk == null) ? 0 : disk.hashCode());
      result = prime * result + id;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((ram == null) ? 0 : ram.hashCode());
      result = prime * result + ((vcpus == null) ? 0 : vcpus.hashCode());
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
      Flavor other = (Flavor) obj;
      if (disk == null) {
         if (other.disk != null)
            return false;
      } else if (!disk.equals(other.disk))
         return false;
      if (id != other.id)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (ram == null) {
         if (other.ram != null)
            return false;
      } else if (!ram.equals(other.ram))
         return false;
      if (vcpus == null) {
         if (other.vcpus != null)
            return false;
      } else if (!vcpus.equals(other.vcpus))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Flavor [disk=" + disk + ", id=" + id + ", name=" + name + ", ram=" + ram + ", vcpus=" + vcpus +"]";
   }
}
