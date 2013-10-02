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
package org.jclouds.rackspace.cloudloadbalancers.v1.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;

/**
 * @author Everett Toews
 */
public final class HistoricalUsage {
   private final int accountId;
   private final Map<String, Iterable<AccountUsage>> accountUsage;
   private final Iterable<LoadBalancerInfo> loadBalancerUsages;

   @ConstructorProperties({ "accountId", "accountUsage", "loadBalancerUsages" })
   protected HistoricalUsage(int accountId, Map<String, Iterable<AccountUsage>> accountUsage,
         Iterable<LoadBalancerInfo> loadBalancerUsages) {
      this.accountId = accountId;
      this.accountUsage = checkNotNull(accountUsage, "accountUsage");
      this.loadBalancerUsages = checkNotNull(loadBalancerUsages, "loadBalancerUsages");
   }

   public int getAccountId() {
      return accountId;
   }

   public Iterable<AccountUsage> getAccountUsage() {
      return Iterables.get(accountUsage.values(), 0);
   }

   public Iterable<LoadBalancerInfo> getLoadBalancerInfo() {
      return loadBalancerUsages;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(accountId);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      HistoricalUsage that = HistoricalUsage.class.cast(obj);

      return Objects.equal(this.accountId, that.accountId);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("accountId", accountId)
            .add("accountUsage", getAccountUsage()).add("loadBalancerInfo", loadBalancerUsages).toString();
   }
}
