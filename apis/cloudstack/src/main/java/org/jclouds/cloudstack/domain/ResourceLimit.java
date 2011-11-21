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

package org.jclouds.cloudstack.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;

/**
 * @author Vijay Kiran
 */
public class ResourceLimit implements Comparable<ResourceLimit> {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private String account;
      private String domain;
      private long domainId;
      private int max;
      private ResourceType resourceType;

      public Builder account(String account) {
         this.account = account;
         return this;
      }

      public Builder domain(String domain) {
         this.domain = domain;
         return this;
      }

      public Builder domainId(long domainId) {
         this.domainId = domainId;
         return this;
      }

      public Builder max(int max) {
         this.max = max;
         return this;
      }

      public Builder resourceType(ResourceType resourceType) {
         this.resourceType = resourceType;
         return this;
      }

      public ResourceLimit build() {
         return new ResourceLimit(account, domain, domainId, max, resourceType);
      }
   }

   // for deserialization
   ResourceLimit() {

   }

   private String account;
   private String domain;
   @SerializedName("domainid")
   private long domainId;
   private int max;
   @SerializedName("resourcetype")
   private ResourceType resourceType;

   public ResourceLimit(String account, String domain, long domainId, int max, ResourceType resourceType) {
      this.account = account;
      this.domain = domain;
      this.domainId = domainId;
      this.max = max;
      this.resourceType = resourceType;
   }

   public String getAccount() {
      return account;
   }

   public String getDomain() {
      return domain;
   }

   public long getDomainId() {
      return domainId;
   }

   public int getMax() {
      return max;
   }

   public ResourceType getResourceType() {
      return resourceType;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ResourceLimit that = (ResourceLimit) o;

      if (domainId != that.domainId) return false;
      if (max != that.max) return false;
      if (resourceType != that.resourceType) return false;
      if (!account.equals(that.account)) return false;
      if (!domain.equals(that.domain)) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = account.hashCode();
      result = 31 * result + domain.hashCode();
      result = 31 * result + (int) (domainId ^ (domainId >>> 32));
      result = 31 * result + max;
      return result;
   }

   @Override
   public int compareTo(ResourceLimit that) {
      return this.account.compareTo(that.account);
   }

   @Override
   public String toString() {
      return String.format("[account=%s, domain=%s, domainId=%d, max=%d, resourceType=%s]",
            account, domain, domainId, max, resourceType);
   }

   /**
    * Type of resource to update.
    */
   public enum ResourceType {
      /**
       * 0 - Instance. Number of instances a user can create.
       */
      INSTANCE(0),
      /**
       * 1 - IP. Number of public IP addresses a user can own.
       */
      IP(1),
      /**
       * 2 - Volume. Number of disk volumes a user can create.
       */
      VOLUME(2),
      /**
       * 3 - Snapshot. Number of snapshots a user can create.
       */
      SNAPSHOT(3),
      /**
       * 4 - Template. Number of templates that a user can register/create.
       */
      TEMPLATE(4),

      UNRECOGNIZED(Integer.MAX_VALUE);

      private int code;

      private static final Map<Integer, ResourceType> INDEX = Maps.uniqueIndex(ImmutableSet.copyOf(ResourceType.values()),
            new Function<ResourceType, Integer>() {

               @Override
               public Integer apply(ResourceType input) {
                  return input.code;
               }

            });

      ResourceType(int code) {
         this.code = code;
      }

      public int getCode(){
         return code;
      }
      
      @Override
      public String toString() {
         return name();
      }

      public static ResourceType fromValue(String resourceType) {
         Integer code = new Integer(checkNotNull(resourceType, "resourcetype"));
         return INDEX.containsKey(code) ? INDEX.get(code) : UNRECOGNIZED;
      }

   }
}
