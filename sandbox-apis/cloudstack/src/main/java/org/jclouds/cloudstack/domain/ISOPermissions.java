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
package org.jclouds.cloudstack.domain;

import com.google.gson.annotations.SerializedName;

/**
 * @author Richard Downer
 */
public class ISOPermissions implements Comparable<ISOPermissions> {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private long id;
      private String account;
      private long domainId;
      private boolean isPublic;

      /**
       * @param id the template ID
       */
      public Builder id(long id) {
         this.id = id;
         return this;
      }

      /**
       * @param account the list of accounts the template is available for
       */
      public Builder account(String account) {
         this.account = account;
         return this;
      }

      /**
       * @param domainId the ID of the domain to which the template belongs
       */
      public Builder domainId(long domainId) {
         this.domainId = domainId;
         return this;
      }

      /**
       * @param isPublic true if this template is a public template, false otherwise
       */
      public Builder isPublic(boolean isPublic) {
         this.isPublic = isPublic;
         return this;
      }

   }

   private long id;
   private String account;
   @SerializedName("domainid")
   private long domainId;
   @SerializedName("ispublic")
   private boolean isPublic;

   /**
    * present only for serializer
    */
   ISOPermissions() {
   }

   /**
    * @return the template ID
    */
   public long getId() {
      return id;
   }

   /**
    * @return the list of accounts the template is available for
    */
   public String getAccount() {
      return account;
   }

   /**
    * @return the ID of the domain to which the template belongs
    */
   public long getDomainId() {
      return domainId;
   }

   /**
    * @return true if this template is a public template, false otherwise
    */
   public boolean getIsPublic() {
      return isPublic;
   }

   @Override
   public boolean equals(Object o) {
      throw new RuntimeException("FIXME: Implement me");
   }

   @Override
   public int hashCode() {
      throw new RuntimeException("FIXME: Implement me");
   }

   @Override
   public String toString() {
      throw new RuntimeException("FIXME: Implement me");
   }

   @Override
   public int compareTo(ISOPermissions other) {
      throw new RuntimeException("FIXME: Implement me");
   }

}
