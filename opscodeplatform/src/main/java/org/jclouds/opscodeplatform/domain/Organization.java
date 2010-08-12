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

package org.jclouds.opscodeplatform.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.security.PrivateKey;

import com.google.gson.annotations.SerializedName;

/**
 * Organization object.
 * 
 * @author Adrian Cole
 */
public class Organization {

   public interface Type {
      public static final String BUSINESS = "Business";
      public static final String NON_PROFIT = "Non-Profit";
      public static final String PERSONAL = "Personal";
   }

   private String guid;
   @SerializedName("name")
   private String name;
   @SerializedName("full_name")
   private String fullName;
   private String clientname;
   @SerializedName("org_type")
   private String orgType;
   @SerializedName("private_key")
   private PrivateKey privateKey;

   Organization() {

   }

   public Organization(String name, String fullName, String clientname, String orgType) {
      this(null, name, fullName, clientname, orgType, null);
   }

   public Organization(String name, String orgType) {
      this(null, name, name, name + "-validator", orgType, null);
   }

   public Organization(String guid, String name, String fullName, String clientname, String orgType,
         PrivateKey privateKey) {
      this.guid = guid;
      this.name = name;
      this.fullName = fullName;
      this.clientname = checkNotNull(clientname, "clientname");
      this.orgType = checkNotNull(orgType, "orgType");
      this.privateKey = privateKey;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((clientname == null) ? 0 : clientname.hashCode());
      result = prime * result + ((fullName == null) ? 0 : fullName.hashCode());
      result = prime * result + ((guid == null) ? 0 : guid.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((orgType == null) ? 0 : orgType.hashCode());
      result = prime * result + ((privateKey == null) ? 0 : privateKey.hashCode());
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
      if (guid == null) {
         if (other.guid != null)
            return false;
      } else if (!guid.equals(other.guid))
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
      if (privateKey == null) {
         if (other.privateKey != null)
            return false;
      } else if (!privateKey.equals(other.privateKey))
         return false;
      return true;
   }

   public String getGuid() {
      return guid;
   }

   public String getName() {
      return name;
   }

   public String getFullName() {
      return fullName;
   }

   public String getClientname() {
      return clientname;
   }

   public String getOrgType() {
      return orgType;
   }

   public PrivateKey getPrivateKey() {
      return privateKey;
   }

   @Override
   public String toString() {
      return "[name=" + name + ", clientname=" + clientname + ", fullName=" + fullName + ", guid=" + guid
            + ", orgType=" + orgType + ", privateKey=" + (privateKey != null) + "]";
   }

}
