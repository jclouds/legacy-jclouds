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

/**
 * Detailed information about e-mail settings for a single domain
 * 
 * @author Adam Lowe
 * @see <a href="https://customer.glesys.com/api.php?a=doc#email_overview" />
 */
//TODO: find a better name for this class
@Beta
public class EmailOverviewDomain {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String domain;
      private int accounts;
      private int aliases;

      public Builder domain(String domain) {
         this.domain = domain;
         return this;
      }

      public Builder accounts(int accounts) {
         this.accounts = accounts;
         return this;
      }
      
      public Builder aliases(int aliases) {
         this.aliases = aliases;
         return this;
      }
      
      public EmailOverviewDomain build() {
         return new EmailOverviewDomain(domain, accounts, aliases);
      }
      
      public Builder fromEmailOverview(EmailOverviewDomain in) {
         return domain(domain).accounts(in.getAccounts()).aliases(in.getAliases());
      }
   }

   private final String domain;
   private final int accounts;
   private final int aliases;

   public EmailOverviewDomain(String domain, int accounts, int aliases) {
      this.domain = domain;
      this.accounts = accounts;
      this.aliases = aliases;
   }

   /** @return the domain name */
   public String getDomain() {
      return domain;
   }

   /** @return the number of e-mail accounts in the domain */
   public int getAccounts() {
      return accounts;
   }

   /** @return the number of e-mail aliases in the domain */
   public int getAliases() {
      return aliases;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(domain);
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      }
      if (object instanceof EmailOverviewDomain) {
         EmailOverviewDomain other = (EmailOverviewDomain) object;
         return Objects.equal(domain, other.domain);
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      return String.format("domain=%s, accounts=%d, aliases=%d", domain, accounts, aliases);
   }

}
