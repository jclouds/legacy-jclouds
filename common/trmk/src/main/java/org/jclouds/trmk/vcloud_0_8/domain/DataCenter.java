/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.trmk.vcloud_0_8.domain;


/**
 * 
 * @author Adrian Cole
 */
public class DataCenter {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String id;
      private String name;
      private String code;

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder code(String code) {
         this.code = code;
         return this;
      }

      public DataCenter build() {
         return new DataCenter(id, name, code);
      }
   }

   private final String id;
   private final String name;
   private final String code;

   public DataCenter(String id, String name, String code) {
      this.id = id;
      this.name = name;
      this.code = code;
   }

   /**
    * 
    * @return id of the data center
    */
   public String getId() {
      return id;
   }

   /**
    * 
    * @return name of the data center
    */
   public String getName() {
      return name;
   }

   /**
    * 
    * @return airport code of the data center
    */
   public String getCode() {
      return code;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((code == null) ? 0 : code.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
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
      DataCenter other = (DataCenter) obj;
      if (code == null) {
         if (other.code != null)
            return false;
      } else if (!code.equals(other.code))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + id + ", name=" + name + ", code=" + code + "]";
   }
}
