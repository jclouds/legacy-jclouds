/*
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
package org.jclouds.opsource.servers.domain;

import static com.google.common.base.Objects.equal;
import static org.jclouds.opsource.servers.OpSourceNameSpaces.DIRECTORY;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;

/**
 * 
 * 
 */
@XmlRootElement(name = "Account", namespace = DIRECTORY)
public class Account {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromAccount(this);
   }

   public static class Builder {

      private String orgId;

      /**
       * @see Account#getOrgId()
       */
      public Builder orgId(String orgId) {
         this.orgId = orgId;
         return this;
      }

      public Account build() {
         return new Account(orgId);
      }

      public Builder fromAccount(Account in) {
         return orgId(in.getOrgId());
      }
   }

   private Account() {
      // For JAXB and builder use
   }

   @XmlElement(namespace = DIRECTORY)
   protected String orgId;

   private Account(String orgId) {
      this.orgId = orgId;
   }

   /**
    * 
    * @return your Organization ID that will be used as the basis for subsequent
    *         API interactions
    * 
    */
   public String getOrgId() {
      return orgId;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Account that = Account.class.cast(o);
      return equal(orgId, that.orgId);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(orgId);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("orgId", orgId).toString();
   }

}
