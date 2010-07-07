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
package org.jclouds.chef.domain;

import com.google.gson.annotations.SerializedName;

/**
 * User object.
 * 
 * @author Adrian Cole
 */
public class Organization implements Comparable<Organization> {
   private String name;
   @SerializedName("full_name")
   private String fullName;
   @SerializedName("org_type")
   private String orgType;
   private String clientname;

   public Organization(String name) {
      this();
      this.name = name;
   }

   // hidden but needs to be here for json deserialization to work
   Organization() {
      super();
   }

   @Override
   public int compareTo(Organization o) {
      return name.compareTo(o.name);
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getFullName() {
      return fullName;
   }

   public void setFullName(String fullName) {
      this.fullName = fullName;
   }

   public String getOrgType() {
      return orgType;
   }

   public void setOrgType(String orgType) {
      this.orgType = orgType;
   }

   public String getClientname() {
      return clientname;
   }

   public void setClientname(String clientname) {
      this.clientname = clientname;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((clientname == null) ? 0 : clientname.hashCode());
      result = prime * result + ((fullName == null) ? 0 : fullName.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((orgType == null) ? 0 : orgType.hashCode());
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
      Organization other = (Organization) obj;
      if (clientname == null) {
         if (other.clientname != null)
            return false;
      } else if (!clientname.equals(other.clientname))
         return false;
      if (fullName == null) {
         if (other.fullName != null)
            return false;
      } else if (!fullName.equals(other.fullName))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (orgType == null) {
         if (other.orgType != null)
            return false;
      } else if (!orgType.equals(other.orgType))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Organization [clientname=" + clientname + ", fullName=" + fullName + ", name=" + name
               + ", orgType=" + orgType + "]";
   }

}
