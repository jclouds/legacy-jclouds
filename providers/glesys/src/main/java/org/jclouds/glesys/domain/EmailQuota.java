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

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Information on an Email Account Quota size
 *
 * @author Adam Lowe
 * @see <a href="https://customer.glesys.com/api.php?a=doc#email_list" />
 */
public class EmailQuota {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromEmailAccount(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected int max;
      protected String unit;

      /**
       * @see EmailQuota#getMax()
       */
      public T max(int max) {
         this.max = max;
         return self();
      }

      /**
       * @see EmailQuota#getUnit()
       */
      public T unit(String unit) {
         this.unit = checkNotNull(unit, "unit");
         return self();
      }

      public EmailQuota build() {
         return new EmailQuota(max, unit);
      }

      public T fromEmailAccount(EmailQuota in) {
         return this.max(in.getMax()).unit(in.getUnit());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final int max;
   private final String unit;

   @ConstructorProperties({
         "max", "unit"
   })
   protected EmailQuota(int max, String unit) {
      this.max = max;
      this.unit = unit;
   }

   /**
    * @return the maximum size of the mailbox (in units)
    * @see #getUnit
    */
   public int getMax() {
      return this.max;
   }

   /**
    * @return the quota for this e-mail account
    */
   public String getUnit() {
      return this.unit;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(max, unit);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      EmailQuota that = EmailQuota.class.cast(obj);
      return Objects.equal(this.max, that.max) && Objects.equal(this.unit, that.unit);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").add("max", max).add("unit", unit);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
