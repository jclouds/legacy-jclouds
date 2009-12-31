/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.rackspace.cloudservers.domain;

/**
 * 
 * A flavor is an available hardware configuration for a server. Each flavor has a unique
 * combination of disk space and memory capacity.
 * 
 * @author Adrian Cole
 */
public class Flavor {

   public Flavor() {
   }

   @Override
   public String toString() {
      return "Flavor [disk=" + disk + ", id=" + id + ", name=" + name + ", ram=" + ram + "]";
   }

   public Flavor(int id, String name) {
      this.id = id;
      this.name = name;
   }

   private int id;
   private String name;
   private Integer disk;
   private Integer ram;

   public Integer getDisk() {
      return disk;
   }

   public void setDisk(Integer value) {
      this.disk = value;
   }

   public int getId() {
      return id;
   }

   public void setId(int value) {
      this.id = value;
   }

   public String getName() {
      return name;
   }

   public void setName(String value) {
      this.name = value;
   }

   public Integer getRam() {
      return ram;
   }

   public void setRam(Integer value) {
      this.ram = value;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((disk == null) ? 0 : disk.hashCode());
      result = prime * result + id;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((ram == null) ? 0 : ram.hashCode());
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
      return true;
   }

}
