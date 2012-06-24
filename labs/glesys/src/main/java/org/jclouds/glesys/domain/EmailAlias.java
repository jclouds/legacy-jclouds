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
package org.jclouds.glesys.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Detailed information on an Email Account
 *
 * @author Adam Lowe
 * @see <a href="https://customer.glesys.com/api.php?a=doc#email_list" />
 */
public class EmailAlias {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromEmailAccount(this);
   }

   public static abstract class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String account;
      protected String forwardTo;

      /**
       * @see org.jclouds.glesys.domain.EmailAlias#getAccount()
       */
      public T account(String account) {
         this.account = checkNotNull(account, "account");
         return self();
      }

      /**
       * @see EmailAlias#getForwardTo()
       */
      public T forwardTo(String forwardTo) {
         this.forwardTo = checkNotNull(forwardTo, "forwardTo");
         return self();
      }

      public EmailAlias build() {
         return new EmailAlias(account, forwardTo);
      }

      public T fromEmailAccount(EmailAlias in) {
         return this.account(in.getAccount()).forwardTo(in.getForwardTo());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String account;
   private final String forwardTo;

   @ConstructorProperties({
         "emailalias", "goto"
   })
   protected EmailAlias(String account, String forwardTo) {
      this.account = checkNotNull(account, "account");
      this.forwardTo = checkNotNull(forwardTo, "forwardTo");
   }

   /**
    * @return the e-mail address being forwarded
    */
   public String getAccount() {
      return this.account;
   }

   /**
    * @return the e-mail address this address forwards to
    */
   public String getForwardTo() {
      return this.forwardTo;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(account);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      EmailAlias that = EmailAlias.class.cast(obj);
      return Objects.equal(this.account, that.account);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("account", account).add("forwardTo", forwardTo);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
