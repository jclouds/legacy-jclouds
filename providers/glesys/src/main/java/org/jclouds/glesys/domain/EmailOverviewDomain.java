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

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Detailed information about e-mail settings for a single domain
 *
 * @author Adam Lowe
 * @see <a href="https://customer.glesys.com/api.php?a=doc#email_overview" />
 */
@Beta
public class EmailOverviewDomain {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromEmailOverviewDomain(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String domain;
      protected int accounts;
      protected int aliases;

      /**
       * @see EmailOverviewDomain#get()
       */
      public T domain(String domain) {
         this.domain = checkNotNull(domain, "domain");
         return self();
      }

      /**
       * @see EmailOverviewDomain#getAccounts()
       */
      public T accounts(int accounts) {
         this.accounts = accounts;
         return self();
      }

      /**
       * @see EmailOverviewDomain#getAliases()
       */
      public T aliases(int aliases) {
         this.aliases = aliases;
         return self();
      }

      public EmailOverviewDomain build() {
         return new EmailOverviewDomain(domain, accounts, aliases);
      }

      public T fromEmailOverviewDomain(EmailOverviewDomain in) {
         return this.domain(in.get()).accounts(in.getAccounts()).aliases(in.getAliases());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String domain;
   private final int accounts;
   private final int aliases;

   @ConstructorProperties({
         "domainname", "accounts", "aliases"
   })
   protected EmailOverviewDomain(String domain, int accounts, int aliases) {
      this.domain = checkNotNull(domain, "domain");
      this.accounts = accounts;
      this.aliases = aliases;
   }

   /** @return the domain name */
   public String get() {
      return this.domain;
   }

   /** @return the number of e-mail accounts in the domain */
   public int getAccounts() {
      return this.accounts;
   }

   /** @return the number of e-mail aliases in the domain */
   public int getAliases() {
      return this.aliases;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(domain);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      EmailOverviewDomain that = EmailOverviewDomain.class.cast(obj);
      return Objects.equal(this.domain, that.domain);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").add("domain", domain).add("accounts", accounts).add("aliases", aliases);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
