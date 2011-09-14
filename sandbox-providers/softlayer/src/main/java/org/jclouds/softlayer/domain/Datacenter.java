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
package org.jclouds.softlayer.domain;

/**
 * 
 * @author Adrian Cole
 * @see <a href= "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Location_Datacenter"
 *      />
 */
public class Datacenter implements Comparable<Datacenter> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private long id = -1;
      private String name;
      private String longName;

      public Builder id(long id) {
         this.id = id;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder longName(String longName) {
         this.longName = longName;
         return this;
      }

      public Datacenter build() {
         return new Datacenter(id, name, longName);
      }

      public static Builder fromDatacenter(Datacenter in) {
         return Datacenter.builder().id(in.getId()).name(in.getName()).longName(in.getLongName());
      }
   }

   private long id = -1;
   private String name;
   private String longName;

   // for deserializer
   Datacenter() {

   }

   public Datacenter(long id, String name, String longName) {
      this.id = id;
      this.name = name;
      this.longName = longName;
   }

   @Override
   public int compareTo(Datacenter arg0) {
      return new Long(id).compareTo(arg0.getId());
   }

   /**
    * @return The unique identifier of a specific location.
    */
   public long getId() {
      return id;
   }

   /**
    * @return A short location description.
    */
   public String getName() {
      return name;
   }

   /**
    * @return A longer location description.
    */
   public String getLongName() {
      return longName;
   }

   public Builder toBuilder() {
      return Builder.fromDatacenter(this);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (id ^ (id >>> 32));
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
      Datacenter other = (Datacenter) obj;
      if (id != other.id)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + id + ", name=" + name + ", longName=" + longName + "]";
   }
   
   
}
