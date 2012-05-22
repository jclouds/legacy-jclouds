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

import java.util.Set;

import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.gson.annotations.SerializedName;

/**
 * @author Andrei Savu
 */
public class FirewallRule implements Comparable<FirewallRule> {

   public static enum Protocol {
      TCP,
      UDP,
      ICMP,
      UNKNOWN;

      public static Protocol fromValue(String value) {
         try {
            return valueOf(value.toUpperCase());
         } catch(IllegalArgumentException e) {
            return UNKNOWN;
         }
      }

      @Override
      public String toString() {
         return name().toUpperCase();
      }
   }

   public static enum State {
      STAGED,     // Rule been created but has never got through network rule conflict detection.
                  // Rules in this state can not be sent to network elements.
      ADD,        // Add means the rule has been created and has gone through network rule conflict detection.
      ACTIVE,     // Rule has been sent to the network elements and reported to be active.
      DELETEING,  // Revoke means this rule has been revoked. If this rule has been sent to the
                  // network elements, the rule will be deleted from database.
      UNKNOWN;

      public static State fromValue(String value) {
         try {
            return valueOf(value.toUpperCase());
         } catch(IllegalArgumentException e) {
            return UNKNOWN;
         }
      }

      @Override
      public String toString() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String id;
      private Set<String> CIDRs;

      private int startPort;
      private int endPort;

      private String icmpCode;
      private String icmpType;

      private String ipAddress;
      private String ipAddressId;

      private Protocol protocol;
      private State state;

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder CIDRs(Set<String> CIDRs) {
         this.CIDRs = ImmutableSet.copyOf(CIDRs);
         return this;
      }

      public Builder startPort(int startPort) {
         this.startPort = startPort;
         return this;
      }

      public Builder endPort(int endPort) {
         this.endPort = endPort;
         return this;
      }

      public Builder icmpCode(String icmpCode) {
         this.icmpCode = icmpCode;
         return this;
      }

      public Builder icmpType(String icmpType) {
         this.icmpType = icmpType;
         return this;
      }

      public Builder ipAddress(String ipAddress) {
         this.ipAddress = ipAddress;
         return this;
      }

      public Builder ipAddressId(String ipAddressId) {
         this.ipAddressId = ipAddressId;
         return this;
      }

      public Builder protocol(Protocol protocol) {
         this.protocol = protocol;
         return this;
      }

      public Builder state(State state) {
         this.state = state;
         return this;
      }

      public FirewallRule build() {
         return new FirewallRule(id, CIDRs, startPort, endPort, icmpCode,
            icmpType, ipAddress, ipAddressId, protocol, state);
      }
   }

   private String id;
   @SerializedName("cidrlist")
   private Set<String> CIDRs;
   @SerializedName("startport")
   private int startPort;
   @SerializedName("endport")
   private int endPort;
   @SerializedName("icmpcode")
   private String icmpCode;
   @SerializedName("icmptype")
   private String icmpType;
   @SerializedName("ipaddress")
   private String ipAddress;
   @SerializedName("ipaddressid")
   private String ipAddressId;
   private Protocol protocol;
   private State state;

   public FirewallRule(String id, Set<String> CIDRs, int startPort, int endPort,
         String icmpCode, String icmpType, String ipAddress, String ipAddressId,
         Protocol protocol, State state) {
      this.id = id;
      this.CIDRs = ImmutableSet.copyOf(CIDRs);
      this.startPort = startPort;
      this.endPort = endPort;
      this.icmpCode = icmpCode;
      this.icmpType = icmpType;
      this.ipAddress = ipAddress;
      this.ipAddressId = ipAddressId;
      this.protocol = protocol;
      this.state = state;
   }

   @Override
   public int compareTo(FirewallRule arg0) {
      return id.compareTo(arg0.getId());
   }

   public String getId() {
      return id;
   }

   public Set<String> getCIDRs() {
      return CIDRs;
   }

   public int getStartPort() {
      return startPort;
   }

   public int getEndPort() {
      return endPort;
   }

   public String getIcmpCode() {
      return icmpCode;
   }

   public String getIcmpType() {
      return icmpType;
   }

   public String getIpAddress() {
      return ipAddress;
   }

   public String getIpAddressId() {
      return ipAddressId;
   }

   public Protocol getProtocol() {
      return protocol;
   }

   public State getState() {
      return state;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      FirewallRule that = (FirewallRule) o;

      if (!Objects.equal(endPort, that.endPort)) return false;
      if (!Objects.equal(id, that.id)) return false;
      if (!Objects.equal(startPort, that.startPort)) return false;
      if (!Objects.equal(CIDRs, that.CIDRs)) return false;
      if (!Objects.equal(icmpCode, that.icmpCode)) return false;
      if (!Objects.equal(icmpType, that.icmpType)) return false;
      if (!Objects.equal(ipAddress, that.ipAddress)) return false;
      if (!Objects.equal(ipAddressId, that.ipAddressId)) return false;
      if (!Objects.equal(protocol, that.protocol)) return false;
      if (!Objects.equal(state, that.state)) return false;

      return true;
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(endPort, id, startPort, CIDRs, icmpCode, icmpType, ipAddress, ipAddressId, protocol, state);
   }

   @Override
   public String toString() {
      return "FirewallRule{" +
         "id=" + id +
         ", CIDRs='" + CIDRs + '\'' +
         ", startPort=" + startPort +
         ", endPort=" + endPort +
         ", icmpCode='" + icmpCode + '\'' +
         ", icmpType='" + icmpType + '\'' +
         ", ipAddress='" + ipAddress + '\'' +
         ", ipAddressId='" + ipAddressId + '\'' +
         ", protocol='" + protocol + '\'' +
         ", state='" + state + '\'' +
         '}';
   }
}
