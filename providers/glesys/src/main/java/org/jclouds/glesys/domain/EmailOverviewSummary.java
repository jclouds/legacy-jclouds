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
package org.jclouds.glesys.domain;

import java.beans.ConstructorProperties;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Summary information of e-mail settings and limits for a GleSYS account
 *
 * @author Adam Lowe
 * @see <a href="https://customer.glesys.com/api.php?a=doc#email_overview" />
 */
//TODO: find a better name for this class
@Beta
public class EmailOverviewSummary {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromEmailOverviewSummary(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected int accounts;
      protected int maxAccounts;
      protected int aliases;
      protected int maxAliases;

      /**
       * @see EmailOverviewSummary#getAccounts()
       */
      public T accounts(int accounts) {
         this.accounts = accounts;
         return self();
      }

      /**
       * @see EmailOverviewSummary#getMaxAccounts()
       */
      public T maxAccounts(int maxAccounts) {
         this.maxAccounts = maxAccounts;
         return self();
      }

      /**
       * @see EmailOverviewSummary#getAliases()
       */
      public T aliases(int aliases) {
         this.aliases = aliases;
         return self();
      }

      /**
       * @see EmailOverviewSummary#getMaxAliases()
       */
      public T maxAliases(int maxAliases) {
         this.maxAliases = maxAliases;
         return self();
      }

      public EmailOverviewSummary build() {
         return new EmailOverviewSummary(accounts, maxAccounts, aliases, maxAliases);
      }

      public T fromEmailOverviewSummary(EmailOverviewSummary in) {
         return this.accounts(in.getAccounts())
               .maxAccounts(in.getMaxAccounts())
               .aliases(in.getAliases())
               .maxAliases(in.getMaxAliases());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final int accounts;
   private final int maxAccounts;
   private final int aliases;
   private final int maxAliases;

   @ConstructorProperties({
         "accounts", "maxaccounts", "aliases", "maxaliases"
   })
   protected EmailOverviewSummary(int accounts, int maxAccounts, int aliases, int maxAliases) {
      this.accounts = accounts;
      this.maxAccounts = maxAccounts;
      this.aliases = aliases;
      this.maxAliases = maxAliases;
   }

   /**
    * @return the number of e-mail accounts
    */
   public int getAccounts() {
      return this.accounts;
   }

   /**
    * @return the maximum number of e-mail accounts
    */
   public int getMaxAccounts() {
      return this.maxAccounts;
   }

   /**
    * @return the number of e-mail aliases
    */
   public int getAliases() {
      return this.aliases;
   }

   /**
    * @return the maximum number of e-mail aliases
    */
   public int getMaxAliases() {
      return this.maxAliases;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(accounts, maxAccounts, aliases, maxAliases);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      EmailOverviewSummary that = EmailOverviewSummary.class.cast(obj);
      return Objects.equal(this.accounts, that.accounts)
            && Objects.equal(this.maxAccounts, that.maxAccounts)
            && Objects.equal(this.aliases, that.aliases)
            && Objects.equal(this.maxAliases, that.maxAliases);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("accounts", accounts).add("maxAccounts", maxAccounts).add("aliases", aliases).add("maxAliases", maxAliases);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
