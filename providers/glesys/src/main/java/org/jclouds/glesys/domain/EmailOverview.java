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
import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

/**
 * Structure containing all information about e-mail addresses for a GleSYS account
 *
 * @author Adam Lowe
 * @see <a href="https://customer.glesys.com/api.php?a=doc#email_overview" />
 */
//TODO: find a better name for this class
@Beta
public class EmailOverview {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromEmailOverview(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected EmailOverviewSummary summary;
      protected Set<EmailOverviewDomain> domains = ImmutableSet.of();

      /**
       * @see EmailOverview#getSummary()
       */
      public T summary(EmailOverviewSummary summary) {
         this.summary = checkNotNull(summary, "summary");
         return self();
      }

      /**
       * @see EmailOverview#gets()
       */
      public T domains(Set<EmailOverviewDomain> domains) {
         this.domains = ImmutableSet.copyOf(checkNotNull(domains, "domains"));
         return self();
      }

      public T domains(EmailOverviewDomain... in) {
         return domains(ImmutableSet.copyOf(in));
      }

      public EmailOverview build() {
         return new EmailOverview(summary, domains);
      }

      public T fromEmailOverview(EmailOverview in) {
         return this.summary(in.getSummary()).domains(in.gets());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final EmailOverviewSummary summary;
   private final Set<EmailOverviewDomain> domains;

   @ConstructorProperties({
         "summary", "domains"
   })
   protected EmailOverview(EmailOverviewSummary summary, Set<EmailOverviewDomain> domains) {
      this.summary = checkNotNull(summary, "summary");
      this.domains = ImmutableSet.copyOf(checkNotNull(domains, "domains"));
   }

   /**
    * @return summary information about the account
    */
   public EmailOverviewSummary getSummary() {
      return this.summary;
   }

   /**
    * @return the set of detailed information about the e-mail addresses and aliases for each domain
    */
   public Set<EmailOverviewDomain> gets() {
      return this.domains;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(summary, domains);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      EmailOverview that = EmailOverview.class.cast(obj);
      return Objects.equal(this.summary, that.summary)
            && Objects.equal(this.domains, that.domains);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("summary", summary).add("domains", domains);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
