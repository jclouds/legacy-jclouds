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
package org.jclouds.glesys.domain;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

/**
 * Summary information of e-mail settings and limits for a GleSYS account
 * 
 * @author Adam Lowe
 * @see <a href="https://customer.glesys.com/api.php?a=doc#email_overview" />
 */
//TODO: find a better name for this class
@Beta
public class EmailOverviewSummary {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private int accounts;
      private int maxAccounts;
      private int aliases;
      private int maxAliases;

      public Builder accounts(int accounts) {
         this.accounts = accounts;
         return this;
      }
      
      public Builder maxAccounts(int maxAccounts) {
         this.maxAccounts = maxAccounts;
         return this;
      }
      
      public Builder aliases(int aliases) {
         this.aliases = aliases;
         return this;
      }
      
      public Builder maxAliases(int maxAliases) {
         this.maxAliases = maxAliases;
         return this;
      }
      
      public EmailOverviewSummary build() {
         return new EmailOverviewSummary(accounts, maxAccounts, aliases, maxAliases);
      }
      
      public Builder fromEmailOverview(EmailOverviewSummary in) {
         return accounts(in.getAccounts()).maxAccounts(in.getMaxAccounts()).aliases(in.getAliases()).maxAliases(in.getMaxAliases());
      }
   }

   private final int accounts;
   @SerializedName("maxaccounts")
   private final int maxAccounts;
   private final int aliases;
   @SerializedName("maxaliases")
   private final int maxAliases;

   public EmailOverviewSummary(int accounts, int maxAccounts, int aliases, int maxAliases) {
      this.accounts = accounts;
      this.maxAccounts = maxAccounts;
      this.aliases = aliases;
      this.maxAliases = maxAliases;
   }

   /** @return the number of e-mail accounts */
   public int getAccounts() {
      return accounts;
   }

   /** @return the maximum number of e-mail accounts */
   public int getMaxAccounts() {
      return maxAccounts;
   }

   /** @return the number of e-mail aliases */
   public int getAliases() {
      return aliases;
   }

   /** @return the maximum number of e-mail aliases */
   public int getMaxAliases() {
      return maxAliases;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(accounts, maxAccounts, aliases, maxAliases);
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      }
      if (object instanceof EmailOverviewSummary) {
         EmailOverviewSummary other = (EmailOverviewSummary) object;
         return Objects.equal(accounts, other.accounts)
               && Objects.equal(maxAccounts, other.maxAccounts)
               && Objects.equal(aliases, other.aliases)
               && Objects.equal(maxAliases, other.maxAliases);
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      return String.format("accounts=%d, maxAccounts=%d, aliases=%d, maxAliases=%d", accounts, maxAccounts, aliases, maxAliases);
   }

}
