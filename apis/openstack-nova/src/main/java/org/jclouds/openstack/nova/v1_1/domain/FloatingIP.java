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
package org.jclouds.openstack.nova.v1_1.domain;

import static com.google.common.base.Objects.toStringHelper;

import org.jclouds.javax.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * A Floating IP is an IP address that can be created and associated with a
 * Server instance. Floating IPs can also be disassociated and deleted from a
 * Server instance.
 * 
 * @author Jeremy Daggett
 * @author chamerling
 */
public class FloatingIP implements Comparable<FloatingIP> {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromFloatingIp(this);
   }

   public static class Builder {
      private String id;
      private String ip;
      private String fixedIp;
      private String instanceId;

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder ip(String ip) {
         this.ip = ip;
         return this;
      }

      public Builder fixedIp(String fixedIp) {
         this.fixedIp = fixedIp;
         return this;
      }

      public Builder instanceId(String instanceId) {
         this.instanceId = instanceId;
         return this;
      }

      public FloatingIP build() {
         return new FloatingIP(id, ip, fixedIp, instanceId);
      }

      public Builder fromFloatingIp(FloatingIP in) {
         return id(in.getId()).ip(in.getIp()).fixedIp(in.getFixedIp()).instanceId(in.getInstanceId());
      }

   }

   private String id;
   private String ip;
   @SerializedName("fixed_ip")
   private String fixedIp;
   @SerializedName("instance_id")
   private String instanceId;

   protected FloatingIP(String id, String ip, @Nullable String fixedIp, @Nullable String instanceId) {
      this.id = id;
      this.ip = ip;
      this.fixedIp = fixedIp;
      this.instanceId = instanceId;
   }

   public String getId() {
      return this.id;
   }

   public String getIp() {
      return this.ip;
   }

   public String getFixedIp() {
      return this.fixedIp;
   }

   public String getInstanceId() {
      return this.instanceId;
   }

   @Override
   public int compareTo(FloatingIP o) {
      return this.id.compareTo(o.getId());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((fixedIp == null) ? 0 : fixedIp.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((instanceId == null) ? 0 : instanceId.hashCode());
      result = prime * result + ((ip == null) ? 0 : ip.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      FloatingIP other = (FloatingIP) obj;
      if (fixedIp == null) {
         if (other.fixedIp != null)
            return false;
      } else if (!fixedIp.equals(other.fixedIp))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (instanceId == null) {
         if (other.instanceId != null)
            return false;
      } else if (!instanceId.equals(other.instanceId))
         return false;
      if (ip == null) {
         if (other.ip != null)
            return false;
      } else if (!ip.equals(other.ip))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return toStringHelper("").add("id", id).add("ip", ip).add("fixedIp", fixedIp).add("instanceId", instanceId)
            .toString();
   }

}
