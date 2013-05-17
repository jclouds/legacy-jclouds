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
package org.jclouds.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableMap;

/**
 * Amazon EC2 VPCs contain one or more subnets.
 * 
 * @see <a href="http://docs.aws.amazon.com/AmazonVPC/latest/UserGuide/VPC_Subnets.html" >doc</a>
 * 
 * @author Adrian Cole
 * @author Andrew Bayer
 */
public final class Subnet {

   public static enum State {
      /**
       * The subnet is available for use.
       */
      AVAILABLE,
      /**
       * The subnet is not yet available for use.
       */
      PENDING, UNRECOGNIZED;
      public String value() {
         return name().toLowerCase();
      }

      public static State fromValue(String v) {
         try {
            return valueOf(v.toUpperCase());
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   private final String subnetId;
   private final State subnetState;
   private final String vpcId;
   private final String cidrBlock;
   private final int availableIpAddressCount;
   private final String availabilityZone;
   private final Map<String, String> tags;

   private Subnet(String subnetId, State subnetState, String vpcId, String cidrBlock, int availableIpAddressCount,
         String availabilityZone, ImmutableMap<String, String> tags) {
      this.subnetId = checkNotNull(subnetId, "subnetId");
      this.subnetState = checkNotNull(subnetState, "subnetState for %s", subnetId);
      this.vpcId = checkNotNull(vpcId, "vpcId for %s", subnetId);
      this.cidrBlock = checkNotNull(cidrBlock, "cidrBlock for %s", subnetId);
      this.availableIpAddressCount = availableIpAddressCount;
      this.availabilityZone = checkNotNull(availabilityZone, "availabilityZone for %s", subnetId);
      this.tags = checkNotNull(tags, "tags for %s", subnetId);
   }

   /**
    * The subnet ID, ex. subnet-c5473ba8
    */
   public String getSubnetId() {
      return subnetId;
   }

   /**
    * The subnet state - either available or pending.
    */
   public State getSubnetState() {
      return subnetState;
   }

   /**
    * The vpc ID this subnet belongs to.
    */
   public String getVpcId() {
      return vpcId;
   }

   /**
    * The CIDR block for this subnet.
    */
   public String getCidrBlock() {
      return cidrBlock;
   }

   /**
    * The number of available IPs in this subnet.
    */
   public int getAvailableIpAddressCount() {
      return availableIpAddressCount;
   }

   /**
    * The availability zone this subnet is in.
    */
   public String getAvailabilityZone() {
      return availabilityZone;
   }

   /**
    * Tags that are attached to this subnet.
    */
   public Map<String, String> getTags() {
      return tags;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(subnetId, vpcId, availabilityZone);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      Subnet that = Subnet.class.cast(obj);
      return Objects.equal(this.subnetId, that.subnetId) && Objects.equal(this.vpcId, that.vpcId)
            && Objects.equal(this.availabilityZone, that.availabilityZone);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   private final ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues().add("subnetId", subnetId).add("subnetState", subnetState)
            .add("vpcId", vpcId).add("cidrBlock", cidrBlock).add("availableIpAddressCount", availableIpAddressCount)
            .add("availabilityZone", availabilityZone).add("tags", tags);
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public static final class Builder {
      private String subnetId;
      private State subnetState;
      private String vpcId;
      private String cidrBlock;
      private int availableIpAddressCount;
      private String availabilityZone;
      private ImmutableMap.Builder<String, String> tags = ImmutableMap.<String, String> builder();

      /**
       * @see Subnet#getSubnetId()
       */
      public Builder subnetId(String subnetId) {
         this.subnetId = subnetId;
         return this;
      }

      /**
       * @see Subnet#getState()
       */
      public Builder subnetState(State subnetState) {
         this.subnetState = subnetState;
         return this;
      }

      /**
       * @see Subnet#getVpcId()
       */
      public Builder vpcId(String vpcId) {
         this.vpcId = vpcId;
         return this;
      }

      /**
       * @see Subnet#getCidrBlock()
       */
      public Builder cidrBlock(String cidrBlock) {
         this.cidrBlock = cidrBlock;
         return this;
      }

      /**
       * @see Subnet#getAvailableIpAddressCount()
       */
      public Builder availableIpAddressCount(int availableIpAddressCount) {
         this.availableIpAddressCount = availableIpAddressCount;
         return this;
      }

      /**
       * @see Subnet#getAvailabilityZone()
       */
      public Builder availabilityZone(String availabilityZone) {
         this.availabilityZone = availabilityZone;
         return this;
      }

      /**
       * @see Subnet#getTags()
       */
      public Builder tags(Map<String, String> tags) {
         this.tags.putAll(checkNotNull(tags, "tags"));
         return this;
      }

      /**
       * @see Subnet#getTags()
       */
      public Builder tag(String key) {
         return tag(key, "");
      }

      /**
       * @see Subnet#getTags()
       */
      public Builder tag(String key, String value) {
         this.tags.put(checkNotNull(key, "key"), checkNotNull(value, "value"));
         return this;
      }

      public Subnet build() {
         return new Subnet(subnetId, subnetState, vpcId, cidrBlock, availableIpAddressCount, availabilityZone,
               tags.build());
      }

      public Builder from(Subnet in) {
         return this.subnetId(in.getSubnetId()).subnetState(in.getSubnetState()).vpcId(in.getVpcId())
               .cidrBlock(in.getCidrBlock()).availableIpAddressCount(in.getAvailableIpAddressCount())
               .availabilityZone(in.getAvailabilityZone()).tags(in.getTags());
      }
   }
}
