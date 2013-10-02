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
import java.util.Date;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * @author Everett Toews
 */
public final class AccountUsage {
   private final int numPublicVIPs;
   private final int numServiceNetVIPs;
   private final int numLoadBalancers;
   private final Date startTime;
   private final Optional<Date> endTime;

   @ConstructorProperties({ "numPublicVips", "numServicenetVips", "numLoadBalancers", "startTime", "endTime" })
   protected AccountUsage(int numPublicVIPs, int numServiceNetVIPs, int numLoadBalancers, Date startTime, Date endTime) {
      this.numPublicVIPs = numPublicVIPs;
      this.numServiceNetVIPs = numServiceNetVIPs;
      this.numLoadBalancers = numLoadBalancers;
      this.startTime = checkNotNull(startTime, "startTime");
      this.endTime = Optional.fromNullable(endTime);
   }

   public int getNumPublicVIPs() {
      return numPublicVIPs;
   }

   public int getNumServiceNetVIPs() {
      return numServiceNetVIPs;
   }

   public int getNumLoadBalancers() {
      return numLoadBalancers;
   }

   public Date getStartTime() {
      return startTime;
   }

   public Optional<Date> getEndTime() {
      return endTime;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(numPublicVIPs, numServiceNetVIPs, numLoadBalancers, startTime, endTime);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      AccountUsage that = AccountUsage.class.cast(obj);

      return Objects.equal(this.numPublicVIPs, that.numPublicVIPs)
            && Objects.equal(this.numServiceNetVIPs, that.numServiceNetVIPs)
            && Objects.equal(this.numLoadBalancers, that.numLoadBalancers)
            && Objects.equal(this.startTime, that.startTime)
            && Objects.equal(this.endTime, that.endTime);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("numPublicVIPs", numPublicVIPs)
            .add("numServiceNetVIPs", numServiceNetVIPs).add("numLoadBalancers", numLoadBalancers)
            .add("startTime", startTime).add("endTime", endTime.orNull()).toString();
   }
}
