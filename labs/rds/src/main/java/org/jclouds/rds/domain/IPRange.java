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
package org.jclouds.rds.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

/**
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonRDS/latest/APIReference/API_IPRange.html"
 *      >doc</a>
 * 
 * @author Adrian Cole
 */
public class IPRange extends Authorization {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromIPRange(this);
   }

   public static class Builder extends Authorization.Builder<Builder> {

      protected String cidrIp;

      /**
       * @see IPRange#getCidrIp()
       */
      public Builder cidrIp(String cidrIp) {
         this.cidrIp = cidrIp;
         return this;
      }

      public IPRange build() {
         return new IPRange(cidrIp, rawStatus, status);
      }

      public Builder fromIPRange(IPRange in) {
         return fromAuthorization(in).cidrIp(in.getCIDRIP());
      }

      @Override
      protected Builder self() {
         return this;
      }
   }

   protected final String cidrIp;

   protected IPRange(String cidrIp, String rawStatus, Status status) {
      super(rawStatus, status);
      this.cidrIp = checkNotNull(cidrIp, "cidrIp");
   }

   /**
    * Specifies the IP range.
    */
   public String getCIDRIP() {
      return cidrIp;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(cidrIp, rawStatus);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      IPRange other = IPRange.class.cast(obj);
      return Objects.equal(this.cidrIp, other.cidrIp) && Objects.equal(this.rawStatus, other.rawStatus);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("cidrIp", cidrIp).add("status", rawStatus).toString();
   }

}
